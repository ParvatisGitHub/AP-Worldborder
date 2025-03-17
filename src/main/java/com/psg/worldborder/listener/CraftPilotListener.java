package com.psg.worldborder.listener;

import com.psg.worldborder.WorldBorderPlugin;
import lombok.RequiredArgsConstructor;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.events.CraftPilotEvent;
import net.countercraft.movecraft.MovecraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


@RequiredArgsConstructor
public class CraftPilotListener implements Listener {
    private final WorldBorderPlugin plugin;
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraftPilot(CraftPilotEvent event) {
        final PlayerCraft playerCraft = (PlayerCraft) event.getCraft();
        final Player player = playerCraft.getPilot();

        if (!(event.getCraft() instanceof PlayerCraft)) {
            return;
        }

        PlayerCraft craft = (PlayerCraft) event.getCraft();
        MovecraftLocation midPoint = craft.getHitBox().getMidPoint();

        // Check if the craft is outside the border when piloted
        if (this.plugin.getBorderManager().isOutsideBorder(midPoint)) {
            // Get pushback location
            MovecraftLocation pushbackLoc = this.plugin.getBorderManager().getPushbackLocation(midPoint);

            // Only push back if the location is different
            if (pushbackLoc.getX() != midPoint.getX() || pushbackLoc.getZ() != midPoint.getZ()) {
                craft.translate(
                        pushbackLoc.getX() - midPoint.getX(),
                        0,  // Keep same Y level
                        pushbackLoc.getZ() - midPoint.getZ()
                );
                player.sendMessage("§cCraft was outside border and has been pushed back!");
            }
        }
    }
}