package com.psg.worldborder.commands.cooldown;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.commands.BorderCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class BorderCooldownMessageCommand extends BorderCommand {
    public BorderCooldownMessageCommand(WorldBorderPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getRequiredPermission() {
        return "apworldborder.bordercooldownmessage";
    }

    @Override
    protected int getRequiredArgs() {
        return 1;
    }

    @Override
    protected String getUsage() {
        return "/bordercooldownmessage <message>";
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        String message = String.join(" ", args);
        plugin.getConfig().set("border.cooldown.message", message);
        plugin.saveConfig();
        plugin.getConfigManager().loadConfig();

        sender.sendMessage("§aBorder cooldown message set to: " + message);
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            return null;
        }

        if (args.length == 1) {
            // Suggest the <message> parameter
            return Collections.singletonList("<message>");
        }
        return null;
    }
}