package com.psg.worldborder;

import com.psg.worldborder.config.BorderConfig;
import com.psg.worldborder.config.ConfigManager;
import com.psg.worldborder.border.BorderManager;
import com.psg.worldborder.border.BorderCooldownManager;
import com.psg.worldborder.listener.PlayerMoveListener;
import com.psg.worldborder.VehicleBorderHandler;
import com.psg.worldborder.listener.BorderInteractionListener;
import com.psg.worldborder.listener.CraftPilotListener;
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
    private PlayerBorderHandler playerBorderHandler;

    @Getter
    private BorderCooldownManager borderCooldownManager;

    @Getter
    private BorderInteractionListener borderInteractionListener;

    @Getter
    private VehicleBorderHandler vehicleBorderHandler;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        this.saveDefaultConfig();
        this.configManager = new ConfigManager(this);

        this.borderCooldownManager = new BorderCooldownManager();

        this.borderManager = new BorderManager(this);

        this.vehicleBorderHandler = new VehicleBorderHandler(this);

        this.borderInteractionListener = new BorderInteractionListener(this, this.vehicleBorderHandler);

        BorderConfig borderConfig = BorderConfig.fromConfig(this.getConfig());
        this.borderHandler = new CraftBorderHandler(this);
        this.playerBorderHandler = new PlayerBorderHandler(this);

        // Register listeners
        this.getServer().getPluginManager().registerEvents(
                new CraftMoveListener(this, this.borderHandler), this);
        this.getServer().getPluginManager().registerEvents(
                new PlayerMoveListener(this, this.playerBorderHandler), this);
        this.getServer().getPluginManager().registerEvents(
                new BorderInteractionListener(this, this.vehicleBorderHandler), this);
        this.getServer().getPluginManager().registerEvents(
                new CraftPilotListener(this), this);

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

        //Add new commands
        getCommand("borderdenyblockplace").setExecutor(new BorderBlockPlaceCommand(this));
        getCommand("borderdenyenderpearl").setExecutor(new BorderDenyEnderpearlCommand(this));
        getCommand("borderdenyvehicles").setExecutor(new BorderDenyVehiclesCommand(this));
        getCommand("borderplayerleeway").setExecutor(new BorderPlayerLeewayCommand(this));
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