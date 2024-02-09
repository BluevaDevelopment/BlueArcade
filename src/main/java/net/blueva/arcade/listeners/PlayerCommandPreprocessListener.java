package net.blueva.arcade.listeners;

import net.blueva.arcade.managers.CacheManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import net.blueva.arcade.Main;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.utils.StringUtils;

import java.io.IOException;
import java.util.List;

public class PlayerCommandPreprocessListener implements Listener {

    private Main main;

    public PlayerCommandPreprocessListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PCPL(PlayerCommandPreprocessEvent event) throws IOException {
        Player p = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        if(event.getMessage().equalsIgnoreCase("/leave") || event.getMessage().equalsIgnoreCase("/quit")) {
            PlayerManager.LeavePlayer(main, PlayerManager.PlayerArena.get(p), p, true);
            event.setCancelled(true);
            return;
        }
        if(main.configManager.getSettings().getBoolean("game.global.cancel_commands.enabled")) {
            if(PlayerManager.PlayerStatus.containsKey(p)) {
                if(PlayerManager.PlayerStatus.get(p).equals("Playing")) {
                    List<String> acmd = CacheManager.Settings.GAME_GLOBAL_CANCEL_COMMANDS_ALLOWED_COMMANDS;
                    for (String s : acmd) {
                        if (command.startsWith(s)) {
                            return;
                        }
                    }
                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_ERROR_NOT_ALLOWED_COMMAND);
                    event.setCancelled(true);
                }
            }
        }
    }
}