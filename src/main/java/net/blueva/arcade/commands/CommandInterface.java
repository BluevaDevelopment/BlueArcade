package net.blueva.arcade.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public interface CommandInterface {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) throws IOException;
}
