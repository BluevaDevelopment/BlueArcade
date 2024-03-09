package net.blueva.arcade.commands.main;

import net.blueva.arcade.managers.CacheManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import net.blueva.arcade.Main;
import net.blueva.arcade.commands.CommandInterface;
import net.blueva.arcade.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class CommandHandler implements CommandExecutor
{

    public CommandHandler(Main main) {
    }

    //This is where we will store the commands
    private static final HashMap<String, CommandInterface> commands = new HashMap<String, CommandInterface>();

    //Register method. When we register commands in our onEnable() we will use this.
    public void register(String name, CommandInterface cmd) {

        //When we register the command, this is what actually will put the command in the hashmap.
        commands.put(name, cmd);
    }

    //This will be used to check if a string exists or not.
    public boolean exists(String name) {

        //To actually check if the string exists, we will return the hashmap
        return commands.containsKey(name);
    }

    //Getter method for the Executor.
    public CommandInterface getExecutor(String name) {

        //Returns a command in the hashmap of the same name.
        return commands.get(name);
    }

    //This will be a template. All commands will have this in common.
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        //If there aren't any arguments, what is the command name going to be? For this example, we are going to call it /example.
        //This means that all commands will have the base of /example.
        if(args.length == 0) {
            try {
                getExecutor("arcade").onCommand(sender, cmd, commandLabel, args);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        //What if there are arguments in the command? Such as /example args
        //If that argument exists in our registration in the onEnable();
        if(exists(args[0])){

            //Get The executor with the name of args[0]. With our example, the name of the executor will be args because in
            //the command /example args, args is our args[0].
            try {
                getExecutor(args[0]).onCommand(sender, cmd, commandLabel, args);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {

            //We want to send a message to the sender if the command doesn't exist.
            StringUtils.sendMessage(sender, sender.getName(), CacheManager.Language.GLOBAL_ERROR_UNKNOWN_COMMAND);
        }
        return true;
    }

}