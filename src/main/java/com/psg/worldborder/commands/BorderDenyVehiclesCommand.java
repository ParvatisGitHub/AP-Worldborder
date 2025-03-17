package com.psg.worldborder.commands;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.border.BorderShape;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BorderDenyVehiclesCommand extends BorderCommand {
    public BorderDenyVehiclesCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.borderdenyvehicles";
    }

    @Override
    protected int getRequiredArgs() {
        return 1;
    }

    @Override
    protected String getUsage() {
        return "/borderdenyvehicles <True|False>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        try {
            boolean enabled = Boolean.parseBoolean(args[0]);
            plugin.getConfig().set("border.denyVehicles", enabled);
            plugin.saveConfig();
            plugin.getConfigManager().loadConfig();
            sender.sendMessage("§Deny Vehicles " + (enabled ? "enabled" : "disabled"));
            return true;
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid Arguments! Use True or False");
            return false;
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return Arrays.asList("true", "false").stream()
                    .filter(s -> s.startsWith(input))
                    .collect(Collectors.toList());
        }
        return null;
    }
}