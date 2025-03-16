package com.psg.worldborder;

import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.config.BorderConfig;
import com.psg.worldborder.CraftTeleporter;
import lombok.RequiredArgsConstructor;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class CraftBorderHandler {
    private final WorldBorderPlugin plugin;
    private static final int BORDER_BUFFER = 50; // Buffer distance from border in blocks

    public void handleBorderCrossing(Craft craft, Player player, MovecraftLocation midPoint) {

        // Stop the craft from cruising if it is
        if (craft.getCruising()) {
            craft.setCruising(false);
            player.sendMessage("§eCraft cruise disabled due to border crossing.");
        }
        // Check cooldown
        if (this.plugin.getBorderCooldownManager().isOnCooldown(player)) {
            long remainingTime = this.plugin.getBorderCooldownManager().getRemainingCooldown(player);
            String message = this.plugin.getConfigManager().getBorderConfig().getCooldownMessage()
                    .replace("%time%", String.format("%.1f", remainingTime / 1000.0));
            player.sendMessage(message);
            // Stop the craft from cruising if it is
            if (craft.getCruising()) {
                craft.setCruising(false);
                player.sendMessage("§eCraft cruise disabled due to border crossing.");
            }
            return;
        }

        final BorderConfig config = this.plugin.getConfigManager().getBorderConfig();
        if (config.isWrappingEnabled()) {
            handleWrapping(craft, player, midPoint);
        } else {
            handlePushback(craft, player, midPoint);
        }

        // Set cooldown after successful crossing
        this.plugin.getBorderCooldownManager().setCooldown(player);
    }

    private void handleWrapping(Craft craft, Player player, MovecraftLocation midPoint) {
        final MovecraftLocation newLocation = this.plugin.getBorderManager().getWrappedLocation(midPoint);

        if (this.plugin.getConfigManager().getBorderConfig().isDebugMode()) {
            this.plugin.getLogger().info(String.format("New Wrapped Location - X: %d, Z: %d",
                    newLocation.getX(), newLocation.getZ()));
        }

        if (newLocation.getX() != midPoint.getX() || newLocation.getZ() != midPoint.getZ()) {
            if (this.plugin.getConfigManager().getBorderConfig().isDebugMode()) {
                this.plugin.getLogger().info("Teleporting craft due to wrapping: " +
                        "New Location x: " + newLocation.getX() + ", Z: " + newLocation.getZ());
            }

            MovecraftLocation safeLocation = calculateSafeLocation(newLocation, midPoint);

            if (this.plugin.getConfigManager().getBorderConfig().isDebugMode()) {
                this.plugin.getLogger().info("Teleporting craft to X: " + safeLocation.getX() +
                        ", Y: " + safeLocation.getY() + ", Z: " + safeLocation.getZ());
                this.plugin.getLogger().info("Craft: " + craft + " Player: " + player.getName());
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    // Stop cruising if the craft is cruising
                    if (craft.getCruising()) {
                        craft.setCruising(false);
                        player.sendMessage("§eCraft cruise disabled due to border cooldown.");
                    }
                    CraftTeleporter.teleportCraft(craft, player, safeLocation.getX(), safeLocation.getY(), safeLocation.getZ());
                }
            }.runTask(this.plugin);
        }
    }

    private void handlePushback(Craft craft, Player player, MovecraftLocation midPoint) {
        final MovecraftLocation pushbackLocation = this.plugin.getBorderManager().getPushbackLocation(midPoint);

        if (pushbackLocation.getX() != midPoint.getX() || pushbackLocation.getZ() != midPoint.getZ()) {
            MovecraftLocation safeLocation = calculateSafeLocation(pushbackLocation, midPoint);
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Stop cruising if the craft is cruising
                    if (craft.getCruising()) {
                        craft.setCruising(false);
                        player.sendMessage("§eCraft cruise disabled due to border cooldown.");
                    }
                    CraftTeleporter.teleportCraft(craft, player, safeLocation.getX(), safeLocation.getY(), safeLocation.getZ());
                }
            }.runTask(this.plugin);
        }
    }

    private MovecraftLocation calculateSafeLocation(MovecraftLocation newLocation, MovecraftLocation midPoint) {
        int safeX = newLocation.getX();
        int safeZ = newLocation.getZ();

        if (Math.abs(newLocation.getX()) > Math.abs(midPoint.getX())) {
            safeX = newLocation.getX() > 0 ?
                    newLocation.getX() - BORDER_BUFFER :
                    newLocation.getX() + BORDER_BUFFER;
        }
        if (Math.abs(newLocation.getZ()) > Math.abs(midPoint.getZ())) {
            safeZ = newLocation.getZ() > 0 ?
                    newLocation.getZ() - BORDER_BUFFER :
                    newLocation.getZ() + BORDER_BUFFER;
        }

        return new MovecraftLocation(safeX, midPoint.getY(), safeZ);
    }
}