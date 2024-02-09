package net.blueva.arcade.commands.main.subcommands;

import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class RemoveSubCommand implements CommandInterface {
    private final Main main;

    public RemoveSubCommand(Main main) {
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


        if(sender.hasPermission("bluearcade.admin") || sender.hasPermission("bluearcade.*")) {
            if(args.length == 1) {
                StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_OTHER_USE_REMOVE_SUBCOMMAND);
            } else if(args.length == 2) {
                if(org.apache.commons.lang.StringUtils.isNumeric(args[1])) {
                    if(ArenaManager.ArenaList.contains(Integer.valueOf(args[1]))) {
                        File arena = new File(main.getDataFolder()+"/data/arenas/"+args[1]+".yml");
                        if(arena.delete()) {
                            main.updateArenaList();
                            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_SUCCESS_ARENA_REMOVED.replace("{arena_id}", args[1]));
                        } else {
                            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_ERROR_DELETING_FILE.replace("{file}", args[1]));
                        }
                    } else {
                        StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_UNKNOWN_ID);
                    }
                } else {
                    StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_ONLY_NUMBERS);
                }
            }
        } else {
           StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
        }
        return true;
    }
}
