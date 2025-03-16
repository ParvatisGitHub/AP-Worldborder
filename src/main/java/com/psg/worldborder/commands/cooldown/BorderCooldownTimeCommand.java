package com.psg.worldborder.commands.cooldown;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.commands.BorderCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BorderCooldownTimeCommand extends BorderCommand {
    public BorderCooldownTimeCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.bordercooldowntime";
    }

    @Override
    protected int getRequiredArgs() {
        return 1;
    }

    @Override
    protected String getUsage() {
        return "/bordercooldowntime <seconds>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        try {
            int time = Integer.parseInt(args[0]);
            if (time < 0) {
                sender.sendMessage("§cCooldown time must be positive!");
                return false;
            }
            
            plugin.getConfig().set("border.cooldown.time", time);
            plugin.saveConfig();
            plugin.getConfigManager().loadConfig();
            
            sender.sendMessage(String.format("§aBorder cooldown time set to %d seconds", time));
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid time! Please use a whole number.");
            return false;
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            return null;
        }
        if (args.length == 1) {
            return Collections.singletonList("<seconds>");
        }
        return null;
    }
}