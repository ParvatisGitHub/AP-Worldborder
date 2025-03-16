package com.psg.worldborder.commands.size;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.commands.BorderCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BorderRadiusCommand extends BorderCommand {
    public BorderRadiusCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.borderradius";
    }

    @Override
    protected int getRequiredArgs() {
        return 1;
    }

    @Override
    protected String getUsage() {
        return "/borderradius <radius>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        try {
            double radius = Double.parseDouble(args[0]);
            if (radius <= 0) {
                sender.sendMessage("§cRadius must be greater than 0!");
                return false;
            }
            
            plugin.getConfig().set("border.size.radius", radius);
            plugin.saveConfig();
            plugin.getConfigManager().loadConfig();
            
            sender.sendMessage(String.format("§aWorld border radius set to %.1f", radius));
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid radius! Please use a number.");
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