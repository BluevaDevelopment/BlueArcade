package net.blueva.arcade.listeners;

import net.blueva.arcade.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import net.blueva.arcade.Main;

public class PlayerDropItemListener implements Listener {
    private final Main main;

    public PlayerDropItemListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PDIL(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        if(PlayerManager.PlayerStatus.get(p).equals("Playing")) {
            event.setCancelled(true);
        }
        if(main.SetupProcess.containsKey(p) && main.SetupProcess.get(p).equals(true)) {
            event.setCancelled(true);
        }
    }
}
