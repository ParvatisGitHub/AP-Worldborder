package com.psg.worldborder.commands;

import com.psg.worldborder.WorldBorderPlugin;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BorderCenterCommand extends BorderCommand {
    public BorderCenterCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.bordercenter";
    }

    @Override
    protected int getRequiredArgs() {
        return 2;
    }

    @Override
    protected String getUsage() {
        return "/bordercenter <x> <z>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        try {
            double x = Double.parseDouble(args[0]);
            double z = Double.parseDouble(args[1]);
            
            plugin.getConfig().set("border.center.x", x);
            plugin.getConfig().set("border.center.z", z);
            plugin.saveConfig();
            plugin.getConfigManager().loadConfig();
            
            sender.sendMessage(String.format("§aWorld border center set to X: %.1f, Z: %.1f", x, z));
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid coordinates! Please use numbers.");
            return false;
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length <= 2) {
            return Collections.singletonList("<number>");
        }
        return null;
    }
}