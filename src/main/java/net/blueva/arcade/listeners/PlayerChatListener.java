package net.blueva.arcade.listeners;

import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.blueva.arcade.Main;
import net.blueva.arcade.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

public class PlayerChatListener implements Listener {
    private final Main main;

    public PlayerChatListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PCL(@NotNull AsyncPlayerChatEvent event) {
        if(!event.isCancelled()) {
            if(PlayerManager.PlayerMuted.get(event.getPlayer()).equals(1)) {
                StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_ERROR_CHAT_MUTED);
                event.setCancelled(true);
            }

            if(PlayerManager.PlayerChat.get(event.getPlayer()).equals(true)) {
                event.setCancelled(true);
                if(main.SetupArena.containsKey(event.getPlayer()) && main.SetupProcess.containsKey(event.getPlayer())) {
                    if(event.getMessage().equals("cancel")) {
                        PlayerManager.PlayerChat.replace(event.getPlayer(), false);
                        StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_ERROR_PROCESS_CANCELLED);
                        return;
                    }
                    main.configManager.getArena(main.SetupArena.get(event.getPlayer())).set("arena.basic.display_name", event.getMessage());
                    main.configManager.saveArena(main.SetupArena.get(event.getPlayer()));
                    main.configManager.reloadArena(main.SetupArena.get(event.getPlayer()));
                    PlayerManager.PlayerChat.replace(event.getPlayer(), false);
                    StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_SUCCESS_DISPLAY_NAME_SET
                            .replace("{display_name}", event.getMessage())
                            .replace("{arena_id}", String.valueOf(main.SetupArena.get(event.getPlayer()))));
                }
            }
        }
    }
}