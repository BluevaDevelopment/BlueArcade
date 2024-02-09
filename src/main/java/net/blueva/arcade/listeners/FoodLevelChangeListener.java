package net.blueva.arcade.listeners;

import net.blueva.arcade.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import net.blueva.arcade.Main;

public class FoodLevelChangeListener implements Listener {
    private Main main;

    public FoodLevelChangeListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void FLCL(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if(PlayerManager.PlayerStatus.containsKey(p)) {
                if(PlayerManager.PlayerStatus.get(p).equals("Playing")) {
                    event.setCancelled(true);
                }
            }

        }
    }
}
