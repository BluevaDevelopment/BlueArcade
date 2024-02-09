package net.blueva.arcade.listeners;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.managers.minigames.ExplodingSheepManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import java.util.HashMap;
import java.util.Random;

public class PlayerShearEntityListener implements Listener {

    private final HashMap<Integer, Float> explosionLevel = new HashMap<>();

    @EventHandler
    public void PSEL(PlayerShearEntityEvent event) {
        if(PlayerManager.PlayerStatus.containsKey(event.getPlayer())) {
            if(PlayerManager.PlayerStatus.get(event.getPlayer()).equalsIgnoreCase("Playing")) {
                if(ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(event.getPlayer())).equalsIgnoreCase("ExplodingSheep")) {
                    explosionLevel.putIfAbsent(PlayerManager.PlayerArena.get(event.getPlayer()), (float) Main.getPlugin().configManager.getArena(PlayerManager.PlayerArena.get(event.getPlayer())).getDouble("arena.mini_games.exploding_sheep.basic.explosion_level", 2.5));
                    if (event.getEntity().getType() == EntityType.SHEEP) {
                        Entity sheep = event.getEntity();
                        Location sheepLoc = sheep.getLocation();
                        sheep.remove();

                        if (willSheepExplode()) {
                            sheep.getWorld().createExplosion(sheepLoc, explosionLevel.get(PlayerManager.PlayerArena.get(event.getPlayer())), false);
                        }

                        Integer sheared_sheep = ExplodingSheepManager.PlayerShearedSheep.get(event.getPlayer())+1;
                        ExplodingSheepManager.PlayerShearedSheep.replace(event.getPlayer(), sheared_sheep);

                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean willSheepExplode() {
        Random random = new Random();
        int chance = random.nextInt(100) + 1;
        return chance <= 20;
    }

}
