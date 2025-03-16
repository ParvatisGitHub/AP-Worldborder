package com.psg.worldborder.commands;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.border.BorderShape;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BorderShapeCommand extends BorderCommand {
    public BorderShapeCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.bordershape";
    }

    @Override
    protected int getRequiredArgs() {
        return 1;
    }

    @Override
    protected String getUsage() {
        return "/bordershape <CIRCULAR|RECTANGULAR>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        try {
            BorderShape shape = BorderShape.valueOf(args[0].toUpperCase());
            plugin.getConfig().set("border.shape", shape.name());
            plugin.saveConfig();
            plugin.getConfigManager().loadConfig();
            sender.sendMessage("§aWorld border shape set to " + shape.name());
            return true;
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid shape! Use CIRCULAR or RECTANGULAR");
            return false;
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return Arrays.stream(BorderShape.values())
                    .map(shape -> shape.name().toLowerCase())
                    .filter(shape -> shape.startsWith(input))
                    .collect(Collectors.toList());
        }
        return null;
    }
}