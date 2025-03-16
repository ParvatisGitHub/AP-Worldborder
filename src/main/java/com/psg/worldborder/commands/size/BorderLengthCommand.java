package com.psg.worldborder.commands.size;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.commands.BorderCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BorderLengthCommand extends BorderCommand {
    public BorderLengthCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.borderlength";
    }

    @Override
    protected int getRequiredArgs() {
        return 1;
    }

    @Override
    protected String getUsage() {
        return "/borderlength <length>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        try {
            double length = Double.parseDouble(args[0]);
            if (length <= 0) {
                sender.sendMessage("§cLength must be greater than 0!");
                return false;
            }
            
            plugin.getConfig().set("border.size.height", length);
            plugin.saveConfig();
            plugin.getConfigManager().loadConfig();
            
            sender.sendMessage(String.format("§aWorld border length set to %.1f", length));
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid length! Please use a number.");
            return false;
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            return null;
        }
        if (args.length == 1) {
            return Collections.singletonList("<number>");
        }
        return null;
    }
}