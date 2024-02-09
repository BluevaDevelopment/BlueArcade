package net.blueva.arcade.commands.main.subcommands;

import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.managers.ArenaManager;
import net.blueva.arcade.managers.CacheManager;
import net.blueva.arcade.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadSubCommand implements CommandInterface {
    private final Main main;

    public ReloadSubCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd,
                             String commandLabel, String @NotNull [] args) {

        String playerstring = null;
        if ((sender instanceof Player)) {
            playerstring = sender.getName();
        }


        if(sender.hasPermission("bluearcade.admin") || sender.hasPermission("bluearcade.*")) {
            if(args.length == 1) {
                StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_OTHER_USE_RELOAD_SUBCOMMAND);
            } else if (args.length == 2) {
                if(args[1].equalsIgnoreCase("all")) {
                    reloadFile("settings", playerstring, sender, false);
                    reloadFile("rewards", playerstring, sender, false);
                    reloadFile("license", playerstring, sender, false);
                    reloadFile("lang", playerstring, sender, false);
                    reloadFile("signs", playerstring, sender, false);
                    reloadFile("global", playerstring, sender, false);
                    reloadFile("arenas", playerstring, sender, false);
                    StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_SUCCESS_ALL_FILES_RELOADED);
                }
                if(args[1].equalsIgnoreCase("settings")) {
                    reloadFile("settings", playerstring, sender, true);
                }
                if(args[1].equalsIgnoreCase("lang") ||
                        args[1].equalsIgnoreCase("en_UK") || args[1].equalsIgnoreCase("en_UK.yml") ||
                        args[1].equalsIgnoreCase("es_ES") || args[1].equalsIgnoreCase("es_ES.yml")) {
                    reloadFile("lang", playerstring, sender, true);
                }
                if(args[1].equalsIgnoreCase("arenas")) {
                    reloadFile("arenas", playerstring, sender, true);
                }
                if(args[1].equalsIgnoreCase("rewards") || args[1].equalsIgnoreCase("rewards.yml")) {
                    reloadFile("rewards", playerstring, sender, true);
                }
                if(args[1].equalsIgnoreCase("license") || args[1].equalsIgnoreCase("license.yml")) {
                    reloadFile("license", playerstring, sender, true);
                }
                if(args[1].equalsIgnoreCase("lang") || args[1].equalsIgnoreCase("lang.yml")) {
                    reloadFile("lang", playerstring, sender, true);
                }
                if(args[1].equalsIgnoreCase("signs") || args[1].equalsIgnoreCase("signs.yml")) {
                    reloadFile("signs", playerstring, sender, true);
                }
                if(args[1].equalsIgnoreCase("global") || args[1].equalsIgnoreCase("global.yml")) {
                    reloadFile("global", playerstring, sender, true);
                }
            } else {
                StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_OTHER_USE_RELOAD_SUBCOMMAND);
            }
        } else {
            StringUtils.sendMessage(sender, playerstring, CacheManager.Language.GLOBAL_ERROR_INSUFFICIENT_PERMISSIONS);
        }
        return true;
    }

    private void reloadFile(String file, String player, CommandSender sender, Boolean sendSuccess) {
        try {
            if(file.equalsIgnoreCase("settings")) {
                main.configManager.reloadSettings();
                CacheManager.Settings.updateSettingsString(main);
            } else if(file.equalsIgnoreCase("rewards")) {
                main.configManager.reloadRewards();
                CacheManager.Settings.updateSettingsString(main);
            } else if(file.equalsIgnoreCase("lang")) {
                main.configManager.reloadLang();
                file = main.actualLang;
            } else if(file.equalsIgnoreCase("global")) {
                main.configManager.reloadGlobal();
            } else if(file.equalsIgnoreCase("signs")) {
                main.configManager.reloadSigns();
            } else if(file.equalsIgnoreCase("arenas")) {
                for(Integer arena : ArenaManager.ArenaList) {
                    main.configManager.reloadArena(arena);
                }
                StringBuilder resultBuilder = new StringBuilder();

                for (int i = 0; i < ArenaManager.ArenaList.size(); i++) {
                    if (i == ArenaManager.ArenaList.size() - 1) {
                        resultBuilder.append(ArenaManager.ArenaList.get(i));
                    } else {
                        resultBuilder.append(ArenaManager.ArenaList.get(i)).append(".yml");
                    }

                    if (i < ArenaManager.ArenaList.size() - 1) {
                        resultBuilder.append(", ");
                    }
                }

                file = resultBuilder.toString();
            }

        } catch (Exception e) {
            if(player != null)  {
                StringUtils.sendMessage(sender, player,
                        CacheManager.Language.GLOBAL_ERROR_ERROR_RELOADING.replace("{file}", file));
            }
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatMessage(player,
                    CacheManager.Language.GLOBAL_ERROR_ERROR_RELOADING.replace("{file}", file)));
            String[] stackTraceLines = e.toString().split("\n");
            for (String line : stackTraceLines) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BlueArcade] " + line);
            }
        }

        if(sendSuccess) {
            StringUtils.sendMessage(sender, player, CacheManager.Language.GLOBAL_SUCCESS_RELOADED_FILE
                    .replace("{file}", file));
        }
    }
}
