package com.psg.worldborder;

import com.psg.worldborder.WorldBorderPlugin;
import net.countercraft.movecraft.MovecraftLocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VehicleBorderHandler {
    private final WorldBorderPlugin plugin;
    private final Set<String> handlingPlayers;

    public VehicleBorderHandler(WorldBorderPlugin plugin) {
        this.plugin = plugin;
        this.handlingPlayers = Collections.synchronizedSet(new LinkedHashSet<>());
    }

    public void handleVehicleBorderCrossing(Player player, Entity vehicle) {
        if (handlingPlayers.contains(player.getName().toLowerCase())) {
            return;
        }
        // Check if vehicles are denied at the border (borderdenyVehicles)
        if (!plugin.getConfig().getBoolean("border.denyVehicles", true)) {
            // If vehicles are allowed at the border, simply return without doing any pushback or wrapping
            return;
        }
        // Check if player is on cooldown
        if (this.plugin.getBorderCooldownManager().isOnCooldown(player)) {
            long remainingTime = this.plugin.getBorderCooldownManager().getRemainingCooldown(player);
            String message = this.plugin.getConfigManager().getBorderConfig().getCooldownMessage()
                    .replace("%time%", String.format("%.1f", remainingTime / 1000.0));
            player.sendMessage(message);

            // Push the vehicle back since we can't cross during cooldown
            Location vehicleLoc = vehicle.getLocation();
            MovecraftLocation mcLoc = new MovecraftLocation(
                    vehicleLoc.getBlockX(),
                    vehicleLoc.getBlockY(),
                    vehicleLoc.getBlockZ()
            );
            MovecraftLocation pushback = this.plugin.getBorderManager().getPushbackLocation(mcLoc);
            Location pushbackLoc = new Location(
                    vehicleLoc.getWorld(),
                    pushback.getX(),
                    pushback.getY(),
                    pushback.getZ(),
                    vehicleLoc.getYaw(),
                    vehicleLoc.getPitch()
            );

            // Handle any passengers the vehicle might have
            List<Entity> passengers = vehicle.getPassengers();
            for (Entity passenger : passengers) {
                vehicle.removePassenger(passenger);
            }

            // Handle any passengers the player might have
            List<Entity> playerPassengers = player.getPassengers();
            for (Entity passenger : playerPassengers) {
                player.removePassenger(passenger);
            }

            // Stop momentum
            vehicle.setVelocity(new Vector(0, 0, 0));

            // Create locations for player and vehicle
            final Location playerPushbackLoc = pushbackLoc.clone();
            final Location vehiclePushbackLoc = pushbackLoc.clone().add(0, 0.5, 0);

            // Store final references for passengers
            final List<Entity> finalPassengers = passengers;
            final List<Entity> finalPlayerPassengers = playerPassengers;

            // Use runTask to ensure teleport happens on main thread
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Teleport all entities
                    player.teleport(playerPushbackLoc);
                    vehicle.teleport(vehiclePushbackLoc);

                    // Remount after a short delay
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // First remount any vehicle passengers
                            for (Entity passenger : finalPassengers) {
                                vehicle.addPassenger(passenger);
                            }

                            // Then remount the player
                            vehicle.addPassenger(player);

                            // Finally remount any player passengers
                            for (Entity passenger : finalPlayerPassengers) {
                                player.addPassenger(passenger);
                            }
                        }
                    }.runTaskLater(plugin, 5L); // 5 tick delay (0.25 seconds)
                }
            }.runTask(plugin);
            return;
        }

        handlingPlayers.add(player.getName().toLowerCase());

        // Stop vehicle momentum
        vehicle.setVelocity(new Vector(0, 0, 0));

        // Handle any passengers the vehicle might have
        List<Entity> passengers = vehicle.getPassengers();
        for (Entity passenger : passengers) {
            vehicle.removePassenger(passenger);
        }

        // Handle any passengers the player might have
        List<Entity> playerPassengers = player.getPassengers();
        for (Entity passenger : playerPassengers) {
            player.removePassenger(passenger);
        }

        Location vehicleLoc = vehicle.getLocation();
        Location newLocation;

        // Convert Bukkit Location to MovecraftLocation for border checks
        MovecraftLocation mcLoc = new MovecraftLocation(
                vehicleLoc.getBlockX(),
                vehicleLoc.getBlockY(),
                vehicleLoc.getBlockZ()
        );

        // Determine if we should wrap or pushback
        if (this.plugin.getConfigManager().getBorderConfig().isWrappingEnabled()) {
            MovecraftLocation wrapped = this.plugin.getBorderManager().getWrappedLocation(mcLoc);
            newLocation = new Location(
                    vehicleLoc.getWorld(),
                    wrapped.getX(),
                    wrapped.getY(),
                    wrapped.getZ(),
                    vehicleLoc.getYaw(),
                    vehicleLoc.getPitch()
            );

            if (this.plugin.getConfigManager().getBorderConfig().isDebugMode()) {
                this.plugin.getLogger().info(String.format("Vehicle wrapping to X: %.2f, Z: %.2f",
                        newLocation.getX(), newLocation.getZ()));
            }
        } else {
            MovecraftLocation pushback = this.plugin.getBorderManager().getPushbackLocation(mcLoc);
            newLocation = new Location(
                    vehicleLoc.getWorld(),
                    pushback.getX(),
                    pushback.getY(),
                    pushback.getZ(),
                    vehicleLoc.getYaw(),
                    vehicleLoc.getPitch()
            );

            if (this.plugin.getConfigManager().getBorderConfig().isDebugMode()) {
                this.plugin.getLogger().info(String.format("Vehicle pushing back to X: %.2f, Z: %.2f",
                        newLocation.getX(), newLocation.getZ()));
            }
        }

        // Set cooldown
        this.plugin.getBorderCooldownManager().setCooldown(player);

        // Teleport both player and vehicle
        final Location playerLoc = newLocation.clone();
        final Location vehicleNewLoc = newLocation.clone().add(0, 0.5, 0);

        // Store final references for passengers
        final List<Entity> finalPassengers = passengers;
        final List<Entity> finalPlayerPassengers = playerPassengers;

        // Use runTask to ensure teleport happens on main thread
        new BukkitRunnable() {
            @Override
            public void run() {
                // Teleport all entities
                player.teleport(playerLoc);
                vehicle.teleport(vehicleNewLoc);

                // Remount after a short delay
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // First remount any vehicle passengers
                        for (Entity passenger : finalPassengers) {
                            vehicle.addPassenger(passenger);
                        }

                        // Then remount the player
                        vehicle.addPassenger(player);

                        // Finally remount any player passengers
                        for (Entity passenger : finalPlayerPassengers) {
                            player.addPassenger(passenger);
                        }

                        handlingPlayers.remove(player.getName().toLowerCase());
                    }
                }.runTaskLater(plugin, 5L); // 5 tick delay (0.25 seconds)
            }
        }.runTask(plugin);

        if (this.plugin.getConfigManager().getBorderConfig().isDebugMode()) {
            // Send appropriate message
            if (this.plugin.getConfigManager().getBorderConfig().isWrappingEnabled()) {
                player.sendMessage("§aYou have been wrapped to the other side of the border!");
            } else {
                player.sendMessage("§cYou cannot cross the border with vehicles!");
            }
        }
    }
}