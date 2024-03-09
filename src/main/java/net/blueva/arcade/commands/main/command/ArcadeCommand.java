package net.blueva.arcade.commands.main.command;

import net.blueva.arcade.managers.CacheManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.utils.StringUtils;

import java.util.List;

//This class implements the Command Interface.
public class ArcadeCommand implements CommandInterface
{

    private Main main;

    public ArcadeCommand(Main main) {
        this.main = main;
    }

    //The command should be automatically created.
    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String commandLabel, String[] args) {

        if(sender.hasPermission("bluearcade.info") || sender.hasPermission("bluearcade.*")) {
            List<String> bainfo = CacheManager.Language.COMMANDS_BLUEARCADE_INFO;
            for (String message : bainfo) {
                String player = sender.getName();
                StringUtils.sendMessage(sender, player, message.replace("{plugin_version}", main.pluginversion));
            }
        }
        return false;
    }

}