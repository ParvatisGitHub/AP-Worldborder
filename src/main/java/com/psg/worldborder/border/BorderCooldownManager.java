package com.psg.worldborder.border;

import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.psg.worldborder.WorldBorderPlugin;

public class BorderCooldownManager {
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    
    public boolean isOnCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return false;
        }
        
        long lastCrossing = cooldowns.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastCrossing) < getCooldownTime();
    }
    
    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    public long getRemainingCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return 0;
        }
        
        long lastCrossing = cooldowns.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();
        long remainingTime = getCooldownTime() - (currentTime - lastCrossing);
        return Math.max(0, remainingTime);
    }
    
    private long getCooldownTime() {
        // Convert seconds to milliseconds
        return WorldBorderPlugin.getInstance().getConfigManager().getBorderConfig().getCooldownTime() * 1000L;
    }
}