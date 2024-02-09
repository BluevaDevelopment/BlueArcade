package net.blueva.arcade.commands.main.subcommands;

import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JoinSubCommand implements CommandInterface {
    private final Main main;

    public JoinSubCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd,
                             String commandLabel, String @NotNull [] args) throws IOException {

        String playerString = sender.getName();

        if (sender.hasPermission("bluearcade.join") || sender.hasPermission("bluearcade.*")) {
            if (args.length == 2) {
                if(!org.apache.commons.lang.StringUtils.isNumeric(args[1])) {
                    StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_OTHER_USE_JOIN_SUBCOMMAND);
                    return true;
                }
                Integer arenaID = Integer.valueOf(args[1]);
                if (sender instanceof Player) {
                    PlayerManager.JoinPlayer(main, arenaID, ((Player) sender).getPlayer());
                } else {
                    StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_OTHER_USE_JOIN_SUBCOMMAND);
                }
            } else if (args.length == 3) {
                if(sender.hasPermission("bluearcade.join.others")) {
                    if(!org.apache.commons.lang.StringUtils.isNumeric(args[1])) {
                        StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_OTHER_USE_JOIN_SUBCOMMAND);
                        return true;
                    }
                    Integer arenaID = Integer.valueOf(args[1]);
                    Player targetPlayer = Bukkit.getPlayer(args[2]);

                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        PlayerManager.JoinPlayer(main, arenaID, targetPlayer);
                    } else {
                        StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_ERROR_PLAYER_OFFLINE);
                    }
                }
            } else {
                StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_OTHER_USE_JOIN_SUBCOMMAND);
            }
        } else {
            StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
        }

        return true;
    }
}
