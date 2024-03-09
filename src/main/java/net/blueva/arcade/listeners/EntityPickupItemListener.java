package net.blueva.arcade.listeners;

import net.blueva.arcade.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jetbrains.annotations.NotNull;

public class EntityPickupItemListener implements Listener {

    private final Main main;

    public EntityPickupItemListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void EPIE(@NotNull EntityPickupItemEvent e) {
        if(e.getEntity() instanceof Player p) {
            if(main.SetupProcess.containsKey(p) && main.SetupProcess.get(p).equals(true) && main.SetupArena.containsKey(p)) {
                e.setCancelled(true);
            }
        }
    }
}
