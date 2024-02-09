package net.blueva.arcade.listeners;

import net.blueva.arcade.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPickupItemListener implements Listener {

    private final Main main;

    public PlayerPickupItemListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PPIL(@NotNull PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        if(main.SetupProcess.containsKey(p) && main.SetupProcess.get(p).equals(true) && main.SetupArena.containsKey(p)) {
            e.setCancelled(true);
        }
    }
}
