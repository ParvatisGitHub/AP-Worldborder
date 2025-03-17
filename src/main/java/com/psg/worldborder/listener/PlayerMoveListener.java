package com.psg.worldborder.listener;

import com.psg.worldborder.PlayerBorderHandler;
import com.psg.worldborder.WorldBorderPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class PlayerMoveListener implements Listener {
    private final WorldBorderPlugin plugin;
    private final PlayerBorderHandler borderHandler;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Skip if only looking around (no position change)
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getX() == to.getX() && from.getZ() == to.getZ()) {
            return;
        }

        // Handle border crossing
        borderHandler.handlePlayerBorderCrossing(event.getPlayer(), to);
    }
}