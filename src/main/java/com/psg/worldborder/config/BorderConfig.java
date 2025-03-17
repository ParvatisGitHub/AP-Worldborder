package com.psg.worldborder.config;

import com.psg.worldborder.border.BorderShape;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@RequiredArgsConstructor
public class BorderConfig {
    private final BorderShape shape;
    private final double centerX;
    private final double centerZ;
    private final double radius;
    private final double width;
    private final double height;
    private final boolean wrappingEnabled;
    private final boolean xAxisWrapping;
    private final boolean zAxisWrapping;
    private final double pushbackDistance;
    private final boolean debugMode;
    private final int cooldownTime;
    private final String cooldownMessage;
    private final boolean denyEnderpearl;
    private final boolean denyBlockPlace;
    private final boolean denyVehicles;
    private final int playerLeeway;

    public static BorderConfig fromConfig(final FileConfiguration config) {
        return new BorderConfig(
            BorderShape.valueOf(config.getString("border.shape", "CIRCULAR")),
            config.getDouble("border.center.x", 0.0),
            config.getDouble("border.center.z", 0.0),
            config.getDouble("border.size.radius", 1000.0),
            config.getDouble("border.size.width", 2000.0),
            config.getDouble("border.size.height", 2000.0),
            config.getBoolean("border.wrapping.enabled", true),
            config.getBoolean("border.wrapping.x-axis", true),
            config.getBoolean("border.wrapping.z-axis", true),
            config.getDouble("border.pushback-distance", 10.0),
            config.getBoolean("border.debugMode.enabled", true),
            config.getInt("border.cooldown.time", 5),
            config.getString("border.cooldown.message", "§cYou must wait %time% seconds before crossing the border again!"),
            config.getBoolean("border.denyEnderpearl", true),
            config.getBoolean("border.denyBlockPlace", true),
            config.getBoolean("border.denyVehicles", true),
            config.getInt("border.playerLeeway", 50)
        );
    }
}