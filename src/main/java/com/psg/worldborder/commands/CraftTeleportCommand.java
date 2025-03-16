package com.psg.worldborder.commands;

import com.psg.worldborder.CraftTeleporter;
import com.psg.worldborder.WorldBorderPlugin;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandExecutor;
import lombok.RequiredArgsConstructor;

public class CraftTeleportCommand implements CommandExecutor {
    private final WorldBorderPlugin plugin;
    public CraftTeleportCommand(WorldBorderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("crafttp")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player!");
            return true;
        }
        if (!sender.hasPermission("apworldborder.crafttp")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        // Parse coordinates from args
        if (args.length != 3) {
            sender.sendMessage("Usage: /crafttp <x> <y> <z>");
            return true;
        }

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);

            Player player = (Player) sender;

            // Get craft
            Craft craft = CraftManager.getInstance().getCraftByPlayer(player);
            if (craft == null) {
                player.sendMessage("You must be piloting a craft!");
                return true;
            }
            player.sendMessage("Craft: " + craft + " Player: " + player.getName());
            return CraftTeleporter.teleportCraft(craft, player, x, y, z);
        } catch (NumberFormatException e) {
            sender.sendMessage("Coordinates must be numbers!");
            return true;
        }
    }
}