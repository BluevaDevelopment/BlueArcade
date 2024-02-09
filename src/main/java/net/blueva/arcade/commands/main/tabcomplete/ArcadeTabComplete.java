package net.blueva.arcade.commands.main.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArcadeTabComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> subCommands = new ArrayList<>();

        if (args.length == 1) {
            subCommands.add("quickjoin");
            subCommands.add("join");
            subCommands.add("leave");
            subCommands.add("help");
            if(sender.hasPermission("bluearcade.admin") || sender.isOp()){
                subCommands.add("setup");
                subCommands.add("goto");
                subCommands.add("reload");
                subCommands.add("setmainlobby");
            }
            return subCommands;
        } else if (args.length == 2) {
            if(args[1].equalsIgnoreCase("reload")) {
                subCommands.add("all");
                subCommands.add("lang");
                subCommands.add("arenas");
                subCommands.add("settings");
                subCommands.add("rewards");
                subCommands.add("license");
                subCommands.add("signs");
                subCommands.add("global");
            }
        }
        return null;
    }
}
