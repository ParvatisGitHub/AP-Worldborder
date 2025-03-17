package com.psg.worldborder.commands;

import com.psg.worldborder.WorldBorderPlugin;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BorderPlayerLeewayCommand extends BorderCommand {
    public BorderPlayerLeewayCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.borderleeway";
    }

    @Override
    protected int getRequiredArgs() {
        return 1;
    }

    @Override
    protected String getUsage() {
        return "/borderleeway <distance>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        try {
            double distance = Double.parseDouble(args[0]);
            if (distance < 0) {
                sender.sendMessage("§cPlayer border leeway distance must be positive!");
                return false;
            }

            plugin.getConfig().set("border.playerLeeway", distance);
            plugin.saveConfig();
            plugin.getConfigManager().loadConfig();

            sender.sendMessage(String.format("§aPlayer border leeway distance set to %.1f", distance));
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid distance! Please use a number.");
            return false;
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("<number>");
        }
        return null;
    }
}