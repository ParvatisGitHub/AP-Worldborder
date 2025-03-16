package com.psg.worldborder.commands.wrapping;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.commands.BorderCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BorderWrappingXCommand extends BorderCommand {
    public BorderWrappingXCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.borderwrappingx";
    }

    @Override
    protected int getRequiredArgs() {
        return 1;
    }

    @Override
    protected String getUsage() {
        return "/borderwrappingx <true|false>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        boolean enabled = Boolean.parseBoolean(args[0]);
        plugin.getConfig().set("border.wrapping.x-axis", enabled);
        plugin.saveConfig();
        plugin.getConfigManager().loadConfig();
        
        sender.sendMessage("§aX-axis wrapping " + (enabled ? "enabled" : "disabled"));
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            return null;
        }
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return Arrays.asList("true", "false").stream()
                    .filter(s -> s.startsWith(input))
                    .collect(Collectors.toList());
        }
        return null;
    }
}