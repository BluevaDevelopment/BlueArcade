package net.blueva.arcade.listeners;

import net.blueva.arcade.utils.InventoryUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import net.blueva.arcade.Main;
import net.blueva.arcade.managers.PlayerManager;

import java.io.IOException;

public class PlayerLeaveListener implements Listener {
    private Main main;

    public PlayerLeaveListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PLE(@NotNull PlayerQuitEvent event) throws IOException {
        if(PlayerManager.PlayerList.contains(event.getPlayer())) {
            PlayerManager.LeavePlayer(main, PlayerManager.PlayerArena.get(event.getPlayer()), event.getPlayer(), true);
            PlayerManager.PlayerList.remove(event.getPlayer());
            PlayerManager.PlayerStatus.remove(event.getPlayer());
        }
        if(main.SetupProcess.containsKey(event.getPlayer()) && main.SetupProcess.get(event.getPlayer()).equals(true)) {
            event.getPlayer().getInventory().setContents(InventoryUtil.getInventoryContent(main.configManager.getUser(event.getPlayer().getUniqueId()).getString("inventory")));
        }
    }
}
