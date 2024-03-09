package net.blueva.arcade.commands.main.subcommands;

import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GotoSubCommand implements CommandInterface {
    private final Main main;

    public GotoSubCommand(Main main) {
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
            if(args.length == 3) {
                Player player = ((Player) sender).getPlayer();
                if(player != null) {
                    if(org.apache.commons.lang.StringUtils.isNumeric(args[1])) {
                        int arenaid = Integer.parseInt(args[1]);
                        String minigame = args[2];

                        if(main.configManager.getArena(arenaid).isSet("arena.mini_games."+minigame+".basic.enabled")) {
                            Location minigameloc = new Location(
                                    Bukkit.getWorld(Objects.requireNonNull(main.configManager.getArena(arenaid).getString("arena.mini_games." + minigame + ".basic.world"))),
                                    main.configManager.getArena(arenaid).getInt("arena.mini_games."+minigame+".spawns.list.s1.x"),
                                    main.configManager.getArena(arenaid).getInt("arena.mini_games."+minigame+".spawns.list.s1.y"),
                                    main.configManager.getArena(arenaid).getInt("arena.mini_games."+minigame+".spawns.list.s1.z"),
                                    main.configManager.getArena(arenaid).getInt("arena.mini_games."+minigame+".spawns.list.s1.yaw"),
                                    main.configManager.getArena(arenaid).getInt("arena.mini_games."+minigame+".spawns.list.s1.pitch"));
                            player.teleport(minigameloc);
                            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_SUCCESS_TELEPORTED_TO_GAME
                                    .replace("{game}", minigame));
                        } else {
                            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_NO_MINI_GAME);
                        }
                    }
                }
            } else {
                StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_OTHER_USE_GOTO_SUBCOMMAND);
            }
        } else {
            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
        }
        return true;
    }
}
