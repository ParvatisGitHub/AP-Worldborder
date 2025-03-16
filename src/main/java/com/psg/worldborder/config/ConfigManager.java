package com.psg.worldborder.config;

import com.psg.worldborder.WorldBorderPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final WorldBorderPlugin plugin;
    
    @Getter
    private BorderConfig borderConfig;
    
    public ConfigManager(final WorldBorderPlugin plugin) {
        this.plugin = plugin;
        this.loadConfig();
    }
    
    public void loadConfig() {
        this.plugin.reloadConfig();
        final FileConfiguration config = this.plugin.getConfig();
        this.borderConfig = BorderConfig.fromConfig(config);
    }
}