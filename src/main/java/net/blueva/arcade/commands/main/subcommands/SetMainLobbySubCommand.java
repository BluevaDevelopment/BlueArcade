package net.blueva.arcade.commands.main.subcommands;

import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMainLobbySubCommand implements CommandInterface {
    private final Main main;

    public SetMainLobbySubCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd,
                             String commandLabel, String @NotNull [] args) {

        String playerstring = sender.getName();
        if (!(sender instanceof Player)) {
            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_ONLY_PLAYERS);
            return true;
        }

        final Player player = (Player)sender;
        if(sender.hasPermission("bluearcade.admin") || sender.hasPermission("bluearcade.*")) {
            Location l = player.getLocation();
            String world = l.getWorld().getName();
            double x = l.getX();
            double y = l.getY();
            double z = l.getZ();
            float yaw = l.getYaw();
            float pitch = l.getPitch();
            main.configManager.getGlobal().set("cords.spawn.world", world);
            main.configManager.saveGlobal();
            main.configManager.getGlobal().set("cords.spawn.x", x);
            main.configManager.saveGlobal();
            main.configManager.getGlobal().set("cords.spawn.y", y);
            main.configManager.saveGlobal();
            main.configManager.getGlobal().set("cords.spawn.z", z);
            main.configManager.getGlobal().set("cords.spawn.yaw", yaw);
            main.configManager.saveGlobal();
            main.configManager.getGlobal().set("cords.spawn.pitch", pitch);
            main.configManager.saveGlobal();

            main.configManager.reloadGlobal();
            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_SUCCESS_MAIN_LOBBY_SET
                    .replace("{ml_world}", world)
                    .replace("{ml_x}", String.valueOf(x))
                    .replace("{ml_y}", String.valueOf(y))
                    .replace("{ml_z}", String.valueOf(z)));
        } else {
            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
        }

        return true;
    }
}
