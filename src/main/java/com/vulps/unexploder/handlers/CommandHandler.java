package com.vulps.unexploder.handlers;

import com.vulps.unexploder.Unexploder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(s == "Unexploder"){
            Unexploder.log("command!");
        }
        return false;
    }
}
