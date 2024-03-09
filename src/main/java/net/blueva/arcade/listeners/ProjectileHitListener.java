package net.blueva.arcade.listeners;

import net.blueva.arcade.Main;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.PlayerManager;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;

public class ProjectileHitListener implements Listener {

    @EventHandler
    public void PHL(ProjectileHitEvent event) {
        BlockIterator iterator = new BlockIterator(event.getEntity().getWorld(), event.getEntity().getLocation().toVector(), event.getEntity().getVelocity().normalize(), 0.0D, 4);
        Block hitBlock = null;

        if(event.getEntity().getShooter() instanceof Player shooter) {
            if(PlayerManager.PlayerStatus.containsKey(shooter)) {
                if(PlayerManager.PlayerStatus.get(shooter).equalsIgnoreCase("Playing")) {
                    if(ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(shooter)).equalsIgnoreCase("Spleef")) {

                        while (iterator.hasNext()) {
                            hitBlock = iterator.next();

                            if (hitBlock.getType() != Material.AIR) {
                                break;
                            }
                        }

                        if(hitBlock != null) {
                            if (hitBlock.getType() == Material.SNOW_BLOCK) {
                                hitBlock.getWorld().playEffect(hitBlock.getLocation(), Effect.STEP_SOUND, hitBlock.getType());
                                hitBlock.setType(Material.AIR);
                            }
                        }

                    } else if(ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(shooter)).equalsIgnoreCase("OneInTheChamber")) {
                        if(Main.getPlugin().configManager.getArena(PlayerManager.PlayerArena.get(shooter)).getBoolean("arena.mini_games.one_in_the_chamber.basic.remove_arrows", true)) {
                            if(event.getEntity() instanceof Arrow arrow){
                                arrow.remove();
                            }
                        }
                    }
                }
            }
        }
    }
}
