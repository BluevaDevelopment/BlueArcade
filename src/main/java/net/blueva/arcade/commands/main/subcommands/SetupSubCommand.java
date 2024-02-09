package net.blueva.arcade.commands.main.subcommands;

import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetupSubCommand implements CommandInterface {
    private final Main main;

    public SetupSubCommand(Main main) {
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
            if(args.length == 2) {
                //ba setup[1] id[2]

                if(args[1].equals("cancel")) {
                    main.setupManager.cancel((Player) sender);
                    return true;
                }

                if(args[1].equals("pause")) {
                    main.setupManager.pause((Player) sender);
                    return true;
                }

                if(args[1].equals("next") || args[1].equals("nextstep") ) {
                    main.setupManager.next((Player) sender);
                    return true;
                }

                if(org.apache.commons.lang.StringUtils.isNumeric(args[1])) {
                    main.setupManager.start(((Player) sender).getPlayer(), Integer.parseInt(args[1]));
                }
            } else {
                StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_OTHER_USE_SETUP_SUBCOMMAND);
            }
        } else {
            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
        }
        return true;
    }
}
