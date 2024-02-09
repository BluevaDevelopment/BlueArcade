package net.blueva.arcade.commands.main.subcommands;

import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.managers.PlayerManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuickjoinSubCommand implements CommandInterface {
    private final Main main;

    public QuickjoinSubCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd,
                             String commandLabel, String @NotNull [] args) {

        String playerstring = sender.getName();
        if(sender.hasPermission("bluearcade.quickjoin") || sender.hasPermission("bluearcade.*")) {
            if (!(sender instanceof Player)) {
                if (args.length < 2) {
                    StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_OTHER_USE_QUICKJOIN_SUBCOMMAND);
                    return true;
                }
                if(sender.hasPermission("bluearcade.quickjoin.others")) {
                    playerstring = args[1];
                }
            }

            final Player player = Bukkit.getPlayer(playerstring);
            if (player == null) {
                StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_PLAYER_OFFLINE);
                return true;
            }


            int arenaid = quickjoin();
            if(arenaid != -1) {
                PlayerManager.JoinPlayer(main, arenaid, player);
            } else {
                StringUtils.sendMessage(sender, player.getName(), CacheManager.Language.GLOBAL_ERROR_NO_ARENA);
            }

        } else {
            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
        }

        return true;
    }

    private static int lastSelectedArena = -1;

    public static int quickjoin() {
        List<Integer> availableArenas = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : ArenaManager.ArenaStatusInternal.entrySet()) {
            int arenaId = entry.getKey();
            String statusInternal = entry.getValue();
            String status = ArenaManager.ArenaStatus.get(arenaId);
            if (statusInternal.equalsIgnoreCase("Waiting") && status.equalsIgnoreCase("Enabled")) {
                availableArenas.add(arenaId);
            }
        }
        if (availableArenas.contains(lastSelectedArena)) {
            return lastSelectedArena;
        } else if (availableArenas.size() > 0) {
            int selectedArenaId = availableArenas.get((int) (Math.random() * availableArenas.size()));
            lastSelectedArena = selectedArenaId;
            return selectedArenaId;
        } else {
            return -1;
        }
    }

}
