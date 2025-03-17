package com.psg.worldborder;

import com.psg.worldborder.border.BorderShape;
import com.psg.worldborder.config.BorderConfig;
import com.psg.worldborder.config.*;
import lombok.RequiredArgsConstructor;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.MovecraftLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class PlayerBorderHandler {
    private final WorldBorderPlugin plugin;

    public void handlePlayerBorderCrossing(Player player, Location location) {
        // Skip if player is piloting a craft
        if (CraftManager.getInstance().getCraftByPlayer(player) != null) {
            return;
        }

        // Convert Location to MovecraftLocation for border check
        MovecraftLocation movecraftLoc = new MovecraftLocation(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
//Player leeway from config
        int playerLeeway = this.plugin.getConfigManager().getBorderConfig().getPlayerLeeway();
        final BorderConfig config = this.plugin.getConfigManager().getBorderConfig();
        Location borderLocation;
        // Check if outside border with leeway
        if (!isOutsideBorderWithLeeway(movecraftLoc)) {
            return;
        }
        // Check if player is on cooldown
        if (plugin.getBorderCooldownManager().isOnCooldown(player)) {
            // If on cooldown, always push back
            borderLocation = handlePushback(location);
            if (!borderLocation.equals(location)) {
                player.teleport(borderLocation);
                long remainingTime = this.plugin.getBorderCooldownManager().getRemainingCooldown(player);
                String message = this.plugin.getConfigManager().getBorderConfig().getCooldownMessage()
                        .replace("%time%", String.format("%.1f", remainingTime / 1000.0));
                player.sendMessage(message);
            }
            return;
        }

        if (config.isWrappingEnabled()) {
            borderLocation = handleWrapping(location);
        } else {
            borderLocation = handlePushback(location);
        }

        // Only teleport if the location changed
        if (!borderLocation.equals(location)) {
            player.teleport(borderLocation);
            player.sendMessage("§eYou've reached the world border!");
            // Set cooldown
            this.plugin.getBorderCooldownManager().setCooldown(player);
        }
    }

    private boolean isOutsideBorderWithLeeway(MovecraftLocation location) {
        final BorderConfig config = this.plugin.getConfigManager().getBorderConfig();
        int playerLeeway = this.plugin.getConfigManager().getBorderConfig().getPlayerLeeway();

        if (config.getShape() == BorderShape.CIRCULAR) {
            // Calculate distance from center using Pythagorean theorem
            final double dx = location.getX() - config.getCenterX();
            final double dz = location.getZ() - config.getCenterZ();
            final double distanceSquared = dx * dx + dz * dz;
            final double maxRadiusSquared = (config.getRadius() + playerLeeway) * (config.getRadius() + playerLeeway);

            return distanceSquared > maxRadiusSquared;
        } else {
            final double dx = Math.abs(location.getX() - config.getCenterX());
            final double dz = Math.abs(location.getZ() - config.getCenterZ());
            return dx > (config.getWidth() / 2 + playerLeeway) ||
                    dz > (config.getHeight() / 2 + playerLeeway);
        }
    }

    private Location handleWrapping(Location location) {
        final BorderConfig config = this.plugin.getConfigManager().getBorderConfig();
        int playerLeeway = this.plugin.getConfigManager().getBorderConfig().getPlayerLeeway();
        double newX = location.getX();
        double newZ = location.getZ();
        boolean wrapped = false;

        if (config.getShape() == BorderShape.CIRCULAR) {
            if (config.isXAxisWrapping() || config.isZAxisWrapping()) {
                // Calculate distance and angle from center
                double dx = location.getX() - config.getCenterX();
                double dz = location.getZ() - config.getCenterZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                double angle = Math.atan2(dz, dx);

                if (distance > config.getRadius() + playerLeeway) {
                    // Calculate opposite point on the circle
                    if (config.isXAxisWrapping()) {
                        newX = config.getCenterX() - (dx / distance) * config.getRadius();
                    }
                    if (config.isZAxisWrapping()) {
                        newZ = config.getCenterZ() - (dz / distance) * config.getRadius();
                    }
                    wrapped = true;
                }
            }
        } else {
            if (config.isXAxisWrapping()) {
                double maxX = config.getCenterX() + (config.getWidth() / 2) + playerLeeway;
                double minX = config.getCenterX() - (config.getWidth() / 2) - playerLeeway;

                if (location.getX() > maxX) {
                    newX = config.getCenterX() - (config.getWidth() / 2);
                    wrapped = true;
                } else if (location.getX() < minX) {
                    newX = config.getCenterX() + (config.getWidth() / 2);
                    wrapped = true;
                }
            }

            if (config.isZAxisWrapping()) {
                double maxZ = config.getCenterZ() + (config.getHeight() / 2) + playerLeeway;
                double minZ = config.getCenterZ() - (config.getHeight() / 2) - playerLeeway;

                if (location.getZ() > maxZ) {
                    newZ = config.getCenterZ() - (config.getHeight() / 2);
                    wrapped = true;
                } else if (location.getZ() < minZ) {
                    newZ = config.getCenterZ() + (config.getHeight() / 2);
                    wrapped = true;
                }
            }
        }

        return wrapped ? new Location(location.getWorld(), newX, location.getY(), newZ, location.getYaw(), location.getPitch()) : location;
    }

    private Location handlePushback(Location location) {
        int playerLeeway = this.plugin.getConfigManager().getBorderConfig().getPlayerLeeway();
        final BorderConfig config = this.plugin.getConfigManager().getBorderConfig();
        Location center = new Location(location.getWorld(), config.getCenterX(), location.getY(), config.getCenterZ());

        if (config.getShape() == BorderShape.CIRCULAR) {
            double distance = location.distance(center);
            double maxRadius = config.getRadius() + playerLeeway;

            if (distance > maxRadius) {
                Vector direction = location.toVector().subtract(center.toVector()).normalize();
                double pushbackDistance = config.getRadius() - 5; // Push back 5 blocks from border
                Vector newPosition = center.toVector().add(direction.multiply(pushbackDistance));
                return new Location(location.getWorld(), newPosition.getX(), location.getY(), newPosition.getZ(), location.getYaw(), location.getPitch());
            }
        } else {
            double dx = location.getX() - config.getCenterX();
            double dz = location.getZ() - config.getCenterZ();
            double maxWidth = (config.getWidth() / 2) + playerLeeway;
            double maxHeight = (config.getHeight() / 2) + playerLeeway;
            boolean needsPushback = false;

            if (Math.abs(dx) > maxWidth) {
                dx = dx > 0 ? config.getWidth() / 2 - 5 : -config.getWidth() / 2 + 5;
                needsPushback = true;
            }
            if (Math.abs(dz) > maxHeight) {
                dz = dz > 0 ? config.getHeight() / 2 - 5 : -config.getHeight() / 2 + 5;
                needsPushback = true;
            }

            if (needsPushback) {
                return new Location(
                        location.getWorld(),
                        config.getCenterX() + dx,
                        location.getY(),
                        config.getCenterZ() + dz,
                        location.getYaw(),
                        location.getPitch()
                );
            }
        }

        return location;
    }
}