package net.blueva.arcade.commands.main.subcommands;

import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//ArgsCmd also implements CommandInterface
public class HelpSubCommand implements CommandInterface
{

    private final Main main;

    public HelpSubCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String commandLabel, String @NotNull [] args) {

        String player = sender.getName();
        if(args.length == 1) {
            if(sender.hasPermission("bluearcade.help")) {
                List<String> xahelp = CacheManager.Language.COMMANDS_BLUEARCADE_HELP;
                for (String message : xahelp) {
                    StringUtils.sendMessage(sender, player, message);
                }
            } else {
                StringUtils.sendMessage(sender, player, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
            }
        } else if (args.length == 2) {
            if(args[1].equals("admin")) {
                if(sender.hasPermission("bluearcade.admin.help")) {
                    List<String> xahelp = CacheManager.Language.COMMANDS_BLUEARCADE_ADMINHELP;
                    for (String message : xahelp) {
                        StringUtils.sendMessage(sender, player, message);
                    }
                } else {
                    StringUtils.sendMessage(sender, player, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
                }
            } else if (args[1].equals("user")) {
                if(sender.hasPermission("bluearcade.help")) {
                    List<String> bahelp = CacheManager.Language.COMMANDS_BLUEARCADE_HELP;
                    for (String message : bahelp) {
                        StringUtils.sendMessage(sender, player, message);
                    }
                } else {
                    StringUtils.sendMessage(sender, player, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
                }
            } else {
                StringUtils.sendMessage(sender, player, CacheManager.Language.GLOBAL_OTHER_USE_HELP_SUBCOMMAND);
            }
        } else {
            StringUtils.sendMessage(sender, player, CacheManager.Language.GLOBAL_OTHER_USE_HELP_SUBCOMMAND);
        }

        return false;
    }

}