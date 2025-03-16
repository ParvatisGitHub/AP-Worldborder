package com.psg.worldborder;

import com.psg.worldborder.config.BorderConfig;
import com.psg.worldborder.config.ConfigManager;
import com.psg.worldborder.border.BorderManager;
import com.psg.worldborder.border.BorderCooldownManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import com.psg.worldborder.commands.*;
import com.psg.worldborder.commands.size.*;
import com.psg.worldborder.commands.cooldown.*;
import com.psg.worldborder.commands.wrapping.*;
import com.psg.worldborder.CraftBorderHandler;
import com.psg.worldborder.listener.CraftMoveListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldBorderPlugin extends JavaPlugin {
    
    @Getter
    private static WorldBorderPlugin instance;
    
    @Getter
    private ConfigManager configManager;
    
    @Getter
    private BorderManager borderManager;

    @Getter
    private CraftBorderHandler borderHandler;

    @Getter
    private BorderCooldownManager borderCooldownManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        this.saveDefaultConfig();
        this.configManager = new ConfigManager(this);

        this.borderCooldownManager = new BorderCooldownManager();

        this.borderManager = new BorderManager(this);

        BorderConfig borderConfig = BorderConfig.fromConfig(this.getConfig());
        this.borderHandler = new CraftBorderHandler(this);

        // Register listeners
        this.getServer().getPluginManager().registerEvents(
            new CraftMoveListener(this, this.borderHandler), this);

        // Register commands and tab completers
        getCommand("crafttp").setExecutor(new CraftTeleportCommand(this));
        getCommand("bordershape").setExecutor(new BorderShapeCommand(this));
        getCommand("bordercenter").setExecutor(new BorderCenterCommand(this));

        // Size commands
        getCommand("borderradius").setExecutor(new BorderRadiusCommand(this));
        getCommand("borderwidth").setExecutor(new BorderWidthCommand(this));
        getCommand("borderlength").setExecutor(new BorderLengthCommand(this));

        // Wrapping commands
        getCommand("borderwrapping").setExecutor(new BorderWrappingCommand(this));
        getCommand("borderwrappingx").setExecutor(new BorderWrappingXCommand(this));
        getCommand("borderwrappingz").setExecutor(new BorderWrappingZCommand(this));

        // Other commands
        getCommand("borderpushback").setExecutor(new BorderPushbackCommand(this));
        getCommand("borderdebug").setExecutor(new BorderDebugCommand(this));

        // Cooldown commands
        getCommand("bordercooldowntime").setExecutor(new BorderCooldownTimeCommand(this));
        getCommand("bordercooldownmessage").setExecutor(new BorderCooldownMessageCommand(this));

        this.getLogger().info("WorldBorder plugin has been enabled!");
    }

    private void registerCommand(String name, BorderCommand command) {
        getCommand(name).setExecutor(command);
        getCommand(name).setTabCompleter(command);
    }
    @Override
    public void onDisable() {
        this.getLogger().info("WorldBorder plugin has been disabled!");
    }
}