package net.blueva.arcade.listeners;

import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import net.blueva.arcade.Main;
import net.blueva.arcade.utils.StringUtils;

import java.util.Objects;

public class PlayerJoinListener implements Listener {
    private final Main main;

    public PlayerJoinListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void PJE(@NotNull PlayerJoinEvent event) {
        main.configManager.registerUser(event.getPlayer().getUniqueId());
        main.configManager.getUser(event.getPlayer().getUniqueId()).set("info.name", event.getPlayer().getName());
        main.configManager.saveUser(event.getPlayer().getUniqueId());
        main.configManager.getUser(event.getPlayer().getUniqueId()).set("info.uuid", event.getPlayer().getUniqueId().toString());
        main.configManager.saveUser(event.getPlayer().getUniqueId());
        main.configManager.reloadUser(event.getPlayer().getUniqueId());

        event.getPlayer().setAllowFlight(true);
        event.getPlayer().setFlying(false);

        if(!PlayerManager.PlayerList.contains(event.getPlayer())) {
            PlayerManager.PlayerList.add(event.getPlayer());
            PlayerManager.PlayerStatus.put(event.getPlayer(), "NotPlaying");
            PlayerManager.PlayerMuted.put(event.getPlayer(), 0);
        } else {
            PlayerManager.PlayerStatus.replace(event.getPlayer(), "NotPlaying");
        }

        if(!PlayerManager.PlayerChat.containsKey(event.getPlayer())) {
            PlayerManager.PlayerChat.put(event.getPlayer(), false);
        }

        if(main.configManager.getGlobal().getInt("cords.spawn.x") == 0 &&
                main.configManager.getGlobal().getInt("cords.spawn.y") == 0 &&
                main.configManager.getGlobal().getInt("cords.spawn.z") == 0 &&
                main.configManager.getGlobal().getInt("cords.spawn.pitch") == 0 &&
                main.configManager.getGlobal().getInt("cords.spawn.yaw") == 0) {
            if(event.getPlayer().hasPermission("bluearcade.admin") || event.getPlayer().hasPermission("bluearcade.*")) {
                StringUtils.sendMessage(event.getPlayer(), event.getPlayer().getName(), CacheManager.Language.GLOBAL_INFO_SETS_SPAWN);
            }
        } else {
            if(CacheManager.Settings.SPAWN_ON_JOIN) {
                Location loc = new Location(Bukkit.getWorld(Objects.requireNonNull(main.configManager.getGlobal().getString("cords.spawn.world"))),
                        main.configManager.getGlobal().getInt("cords.spawn.x"), main.configManager.getGlobal().getInt("cords.spawn.y"),
                        main.configManager.getGlobal().getInt("cords.spawn.z"));
                event.getPlayer().teleport(loc);
            }
        }
    }
}
