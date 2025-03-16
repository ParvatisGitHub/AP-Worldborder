package com.psg.worldborder.commands;

import com.psg.worldborder.WorldBorderPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public abstract class BorderCommand implements CommandExecutor, TabCompleter {
    protected final WorldBorderPlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length != getRequiredArgs()) {
            sender.sendMessage("§cUsage: " + getUsage());
            return true;
        }

        return execute(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(getRequiredPermission())) {
            return null;
        }
        return tabComplete(sender, args);
    }
    protected abstract String getRequiredPermission();
    protected abstract int getRequiredArgs();
    protected abstract String getUsage();
    protected abstract boolean execute(CommandSender sender, String[] args);
    protected abstract List<String> tabComplete(CommandSender sender, String[] args);
}