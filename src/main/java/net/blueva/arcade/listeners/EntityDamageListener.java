package net.blueva.arcade.listeners;

import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.managers.minigames.AllAgainstAllManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void EDL(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player p) {
            if(PlayerManager.PlayerStatus.containsKey(p)) {
                if(PlayerManager.PlayerStatus.get(p).equals("Playing")) {
                    if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        event.setCancelled(true);
                    }
                    if(ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(p)).equalsIgnoreCase("AllAgainstAll")) {
                        if (p.getHealth() - event.getFinalDamage() <= 0) {
                            p.setHealth(20);
                            p.getInventory().clear();
                            AllAgainstAllManager.finishPlayer(PlayerManager.PlayerArena.get(p), p, true);
                        }

                        return;
                    } else if (ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(p)).equalsIgnoreCase("SnowballFight")
                            || ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(p)).equalsIgnoreCase("TNTTag")
                            || ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(p)).equalsIgnoreCase("KnockBack")) {
                        p.setHealth(20);
                        p.setFoodLevel(20);
                        return;
                    } else {
                        if (ArenaManager.ArenaActualGame.get(PlayerManager.PlayerArena.get(p)).equalsIgnoreCase("OneInTheChamber")){
                            return;
                        }
                    }

                    event.setCancelled(true);
                }
            }
        }
    }
}
