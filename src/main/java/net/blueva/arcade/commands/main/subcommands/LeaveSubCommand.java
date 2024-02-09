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

public class LeaveSubCommand implements CommandInterface {
    private final Main main;

    public LeaveSubCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd,
                             String commandLabel, String @NotNull [] args) throws IOException {

        String playerString = sender.getName();

        if (sender.hasPermission("bluearcade.leave") || sender.hasPermission("bluearcade.*")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    if (PlayerManager.PlayerStatus.get(((Player) sender).getPlayer()).equals("Playing")) {
                        PlayerManager.LeavePlayer(main, PlayerManager.PlayerArena.get(((Player) sender).getPlayer()), ((Player) sender).getPlayer(), true);
                    } else {
                        StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_ERROR_NOT_IN_GAME);
                    }
                } else {
                    StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_OTHER_USE_LEAVE_SUBCOMMAND);
                }
            } else if (args.length == 2) {
                if(sender.hasPermission("bluearcade.leave.others")) {
                    String targetPlayerName = args[1];
                    Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        if (PlayerManager.PlayerStatus.get(targetPlayer).equals("Playing")) {
                            PlayerManager.LeavePlayer(main, PlayerManager.PlayerArena.get(targetPlayer), targetPlayer, true);
                        } else {
                            StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_ERROR_NOT_IN_GAME);
                        }
                    } else {
                        StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_ERROR_PLAYER_OFFLINE);
                    }
                }
            } else {
                StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_OTHER_USE_LEAVE_SUBCOMMAND);
            }
        } else {
            StringUtils.sendMessage(sender, playerString, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
        }

        return true;
    }
}
