package com.psg.worldborder.commands.size;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.commands.BorderCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BorderWidthCommand extends BorderCommand {
    public BorderWidthCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.borderwidth";
    }

    @Override
    protected int getRequiredArgs() {
        return 1;
    }

    @Override
    protected String getUsage() {
        return "/borderwidth <width>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        try {
            double width = Double.parseDouble(args[0]);
            if (width <= 0) {
                sender.sendMessage("§cWidth must be greater than 0!");
                return false;
            }
            
            plugin.getConfig().set("border.size.width", width);
            plugin.saveConfig();
            plugin.getConfigManager().loadConfig();
            
            sender.sendMessage(String.format("§aWorld border width set to %.1f", width));
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid width! Please use a number.");
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