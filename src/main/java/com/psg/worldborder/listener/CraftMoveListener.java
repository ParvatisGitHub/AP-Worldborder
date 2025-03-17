package com.psg.worldborder.listener;

import com.psg.worldborder.CraftBorderHandler;
import com.psg.worldborder.CraftTeleporter;
import com.psg.worldborder.WorldBorderPlugin;
import com.psg.worldborder.border.BorderShape;
import com.psg.worldborder.config.BorderConfig;
import lombok.RequiredArgsConstructor;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

@RequiredArgsConstructor
public class CraftMoveListener implements Listener {
    @NotNull
    private final WorldBorderPlugin plugin;
    @NotNull
    private final CraftBorderHandler borderHandler;
    @NotNull
    private final Set<Craft> crafts = new ConcurrentSkipListSet<>(Comparator.comparingInt(Object::hashCode));
    @NotNull
    private final ConcurrentMap<Player, PlayerCraft> playerCrafts = new ConcurrentHashMap<>();
    @NotNull
    private final ConcurrentMap<Craft, BukkitTask> releaseEvents = new ConcurrentHashMap<>();

    @EventHandler
    public void onCraftMove(final CraftTranslateEvent event) {
        final BorderConfig config = this.plugin.getConfigManager().getBorderConfig();
        // Check if the craft is a PlayerCraft (you can adjust this if needed for other craft types)
        if (!(event.getCraft() instanceof PlayerCraft)) {
            return;
        }

        final PlayerCraft playerCraft = (PlayerCraft) event.getCraft();
        final World world = playerCraft.getWorld();
        final MovecraftLocation midPoint = playerCraft.getHitBox().getMidPoint();
        final Player player = playerCraft.getPilot();
        final Craft craft = CraftManager.getInstance().getCraftByPlayer(player);

        if (this.plugin.getConfigManager().getBorderConfig().isDebugMode()) {
            // Log the current position of the craft
            this.plugin.getLogger().info(String.format(
                    "Craft position - X: %d, Z: %d",
                    midPoint.getX(),
                    midPoint.getZ()
            ));
        }

        // Check if the craft is outside the world border
        if (this.plugin.getBorderManager().isOutsideBorder(midPoint)) {
            if (this.plugin.getBorderCooldownManager().isOnCooldown(player)) {

                // Stop the craft from cruising and release it
                if (craft.getCruising()) {
                    craft.setCruising(false);
                    player.sendMessage("Craft cruise disabled due to border collision");
                }
                // Get pushback location
                final MovecraftLocation pushbackLocation = this.plugin.getBorderManager().getPushbackLocation(midPoint);

                // Only push back if the location is different
                if (pushbackLocation.getX() != midPoint.getX() || pushbackLocation.getZ() != midPoint.getZ()) {
                    // Schedule the pushback to run on the next tick to avoid concurrent modification
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            int dx = pushbackLocation.getX() - midPoint.getX();
                            int dy = 0; // Keep the same Y level
                            int dz = pushbackLocation.getZ() - midPoint.getZ();
                            craft.translate(dx, dy, dz);
                            CraftTeleporter.teleportCraft(craft, player, dx, dy, dz);
                        }
                    }.runTask(plugin);
                }

                // Send cooldown message
                long remainingTime = this.plugin.getBorderCooldownManager().getRemainingCooldown(player);
                String message = this.plugin.getConfigManager().getBorderConfig().getCooldownMessage()
                        .replace("%time%", String.format("%.1f", remainingTime / 1000.0));
                player.sendMessage(message);
                return;
            }


            if (this.plugin.getConfigManager().getBorderConfig().isDebugMode()) {
                if (config.getShape() == BorderShape.CIRCULAR) {
                    // Log the border crossing
                    this.plugin.getLogger().info(String.format(
                            "Craft crossed border at X: %d, Z: %d (Border type: Circular)",
                            midPoint.getX(),
                            midPoint.getZ()
                    ));
                } else {
                    this.plugin.getLogger().info(String.format(
                            "Craft crossed border at X: %d, Z: %d (Border type: Rectangular)",
                            midPoint.getX(),
                            midPoint.getZ()
                    ));
                }
            }
            // Add null check for extra safety
            if (this.borderHandler == null) {
                this.plugin.getLogger().severe("CraftBorderHandler is null! This should never happen!");
                return;
            }

            //send the border handler if outside the border
            // you know im realizing my naming conventions out of context are ... iffy
            // like fr "border handler" ? "handle the border crossings?!" what am i ice?
            //sigh
            borderHandler.handleBorderCrossing(craft, player, midPoint);
        }
    }
}