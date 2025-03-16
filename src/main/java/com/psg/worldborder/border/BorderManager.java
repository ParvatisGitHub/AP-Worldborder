package com.psg.worldborder.border;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.config.BorderConfig;
import lombok.RequiredArgsConstructor;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.MovecraftLocation;
import org.bukkit.World;

@RequiredArgsConstructor
public class BorderManager {
    private final WorldBorderPlugin plugin;

    public boolean isOutsideBorder(final MovecraftLocation location) {
        final BorderConfig config = this.plugin.getConfigManager().getBorderConfig();

        if (config.getShape() == BorderShape.CIRCULAR) {
            // Calculate distance from center using Pythagorean theorem
            final double dx = location.getX() - config.getCenterX();
            final double dz = location.getZ() - config.getCenterZ();
            final double distanceSquared = dx * dx + dz * dz;
            final double radiusSquared = config.getRadius() * config.getRadius();

            // Debug logging
            if (this.plugin.getConfigManager().getBorderConfig().isDebugMode()) {
                this.plugin.getLogger().info(String.format(
                        "Distance check - Location: (%d, %d), Distance: %.2f, Radius: %.2f",
                        location.getX(), location.getZ(),
                        Math.sqrt(distanceSquared),
                        config.getRadius()
                ));
            }

            return distanceSquared > radiusSquared;
        } else {
            final double dx = Math.abs(location.getX() - config.getCenterX());
            final double dz = Math.abs(location.getZ() - config.getCenterZ());
            return dx > config.getWidth() / 2 || dz > config.getHeight() / 2;
        }
    }

    public MovecraftLocation getWrappedLocation(final MovecraftLocation location) {
        final BorderConfig config = this.plugin.getConfigManager().getBorderConfig();
        double newX = location.getX();
        double newZ = location.getZ();

        if (config.isWrappingEnabled()) {
            if (config.getShape() == BorderShape.CIRCULAR) {
                // Calculate distance and angle from center
                double dx = location.getX() - config.getCenterX();
                double dz = location.getZ() - config.getCenterZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                double angle = Math.atan2(dz, dx);

                if (distance > config.getRadius()) {
                    // Only wrap the enabled axes
                    if (config.isXAxisWrapping()) {
                        newX = config.getCenterX() - (config.getRadius() * Math.cos(angle));
                    }
                    if (config.isZAxisWrapping()) {
                        newZ = config.getCenterZ() - (config.getRadius() * Math.sin(angle));
                    }

                    // If wrapping is disabled for an axis, push back instead
                    if (!config.isXAxisWrapping()) {
                        double scale = (config.getRadius()) / distance;
                        newX = config.getCenterX() + (dx * scale);
                    }
                    if (!config.isZAxisWrapping()) {
                        double scale = (config.getRadius()) / distance;
                        newZ = config.getCenterZ() + (dz * scale);
                    }
                }
            } else {
                // For rectangular borders, handle each axis independently
                if (config.isXAxisWrapping()) {
                    if (location.getX() > config.getCenterX() + config.getWidth() / 2) {
                        newX = config.getCenterX() - config.getWidth() / 2;
                    } else if (location.getX() < config.getCenterX() - config.getWidth() / 2) {
                        newX = config.getCenterX() + config.getWidth() / 2;
                    }
                } else {
                    // If X wrapping is disabled, keep the craft at the border
                    if (location.getX() > config.getCenterX() + config.getWidth() / 2) {
                        newX = config.getCenterX() + config.getWidth() / 2;
                    } else if (location.getX() < config.getCenterX() - config.getWidth() / 2) {
                        newX = config.getCenterX() - config.getWidth() / 2;
                    }
                }

                if (config.isZAxisWrapping()) {
                    if (location.getZ() > config.getCenterZ() + config.getHeight() / 2) {
                        newZ = config.getCenterZ() - config.getHeight() / 2;
                    } else if (location.getZ() < config.getCenterZ() - config.getHeight() / 2) {
                        newZ = config.getCenterZ() + config.getHeight() / 2;
                    }
                } else {
                    // If Z wrapping is disabled, keep the craft at the border
                    if (location.getZ() > config.getCenterZ() + config.getHeight() / 2) {
                        newZ = config.getCenterZ() + config.getHeight() / 2;
                    } else if (location.getZ() < config.getCenterZ() - config.getHeight() / 2) {
                        newZ = config.getCenterZ() - config.getHeight() / 2;
                    }
                }
            }
        }

        return new MovecraftLocation(
                (int) newX,
                location.getY(),
                (int) newZ
        );
    }

    public MovecraftLocation getPushbackLocation(final MovecraftLocation location) {
        final BorderConfig config = this.plugin.getConfigManager().getBorderConfig();
        final double pushbackDistance = config.getPushbackDistance();

        if (config.getShape() == BorderShape.CIRCULAR) {
            // Calculate vector from center to location
            double dx = location.getX() - config.getCenterX();
            double dz = location.getZ() - config.getCenterZ();
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance > 0) {  // Prevent division by zero
                // Normalize the vector and scale it to the border radius minus pushback distance
                double scale = (config.getRadius() - pushbackDistance) / distance;
                dx *= scale;
                dz *= scale;

                return new MovecraftLocation(
                        (int) (config.getCenterX() + dx),
                        location.getY(),
                        (int) (config.getCenterZ() + dz)
                );
            }
        } else {
            double dx = location.getX() - config.getCenterX();
            double dz = location.getZ() - config.getCenterZ();

            if (Math.abs(dx) > config.getWidth() / 2) {
                dx = dx > 0 ?
                        config.getWidth() / 2 - pushbackDistance :
                        -config.getWidth() / 2 + pushbackDistance;
            }
            if (Math.abs(dz) > config.getHeight() / 2) {
                dz = dz > 0 ?
                        config.getHeight() / 2 - pushbackDistance :
                        -config.getHeight() / 2 + pushbackDistance;
            }

            return new MovecraftLocation(
                    (int) (config.getCenterX() + dx),
                    location.getY(),
                    (int) (config.getCenterZ() + dz)
            );
        }

        // Fallback to original location if something went wrong
        return location;
    }
}