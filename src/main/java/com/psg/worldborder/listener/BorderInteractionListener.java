package com.psg.worldborder.listener;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.VehicleBorderHandler;
import net.countercraft.movecraft.MovecraftLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class BorderInteractionListener implements Listener {
    private final WorldBorderPlugin plugin;
    private final VehicleBorderHandler vehicleBorderHandler;

    public BorderInteractionListener(WorldBorderPlugin plugin, VehicleBorderHandler vehicleBorderHandler) {
        this.plugin = plugin;
        this.vehicleBorderHandler = vehicleBorderHandler;
    }

    private MovecraftLocation toMovecraftLocation(Location loc) {
        return new MovecraftLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private Location toBukkitLocation(MovecraftLocation movecraftLoc, World world) {
        return new Location(world, movecraftLoc.getX(), movecraftLoc.getY(), movecraftLoc.getZ());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getConfig().getBoolean("border.denyBlockPlace", true)) {
            return;
        }

        Location loc = event.getBlockPlaced().getLocation();
        MovecraftLocation movecraftLoc = toMovecraftLocation(loc);
        if (plugin.getBorderManager().isOutsideBorder(movecraftLoc)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot place blocks outside the border!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        MovecraftLocation movecraftTo = toMovecraftLocation(to);

        // Check if it's an ender pearl teleport and if ender pearls are denied
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL &&
                plugin.getConfig().getBoolean("border.denyEnderpearl", true)) {

            if (plugin.getBorderManager().isOutsideBorder(movecraftTo)) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot throw ender pearls outside the border!");
                return;
            }
        }

        // Handle regular teleports **DISABLE BREAKS MOVECRAF FUNCTIONALITY
//        if (plugin.getBorderManager().isOutsideBorder(movecraftTo)) {
//            MovecraftLocation pushbackMovecraftLoc = plugin.getBorderManager().getPushbackLocation(movecraftTo);
//            Location pushbackLoc = toBukkitLocation(pushbackMovecraftLoc, to.getWorld());
//            // Preserve the original pitch and yaw
//            pushbackLoc.setPitch(to.getPitch());
//            pushbackLoc.setYaw(to.getYaw());
//            event.setTo(pushbackLoc);
//            player.sendMessage("§cYou cannot teleport outside the border!");
//        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        // Only check if the player is riding something or being ridden
        if (!player.isInsideVehicle() && player.getPassengers().isEmpty()) {
            return;
        }

        MovecraftLocation movecraftTo = toMovecraftLocation(to);
        if (plugin.getBorderManager().isOutsideBorder(movecraftTo)) {
            if (player.isInsideVehicle()) {
                vehicleBorderHandler.handleVehicleBorderCrossing(player, player.getVehicle());
            } else if (!player.getPassengers().isEmpty()) {
                // Handle the case where the player is being ridden
                MovecraftLocation pushbackMovecraftLoc = plugin.getBorderManager().getPushbackLocation(movecraftTo);
                Location pushbackLoc = toBukkitLocation(pushbackMovecraftLoc, to.getWorld());
                // Preserve the original pitch and yaw
                pushbackLoc.setPitch(to.getPitch());
                pushbackLoc.setYaw(to.getYaw());
                event.setTo(pushbackLoc);
                player.setVelocity(new Vector(0, 0, 0));
                player.sendMessage("§cYou cannot cross the border while carrying passengers!");
            }
        }
    }

    public void handleVehicleBorderCrossing(Player player, Entity vehicle) {
        vehicleBorderHandler.handleVehicleBorderCrossing(player, vehicle);
    }
}