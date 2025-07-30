package com.sh4dowking.discordbot;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public class DiscordBot extends JavaPlugin {
    private Dictionary dictionary;
    private DiscordManager discordManager;
    
    @Override
    public void onEnable() {
        getLogger().info("DiscordBot plugin is enabled!");
        saveDefaultConfig(); // Create config.yml if id doesn't exists
        this.dictionary = new Dictionary(this);

        // Validate configuration keys
        if(!dictionary.hasValidConfigKeys()) {
            getLogger().severe("Configuration validation failed. Please check your config.yml.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize Discord Bot
        this.discordManager = new DiscordManager(this);
        if(!discordManager.discordSetupWasSuccessful()) {
            getLogger().severe("Discord bot initialization failed. Please check your Discord token.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Intialize Minecraft Event Listener
        getServer().getPluginManager().registerEvents(new EventListener(this, this.discordManager), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("DiscordBot plugin is disabled!");
    }

    // Getters
    public Dictionary getDictionary() {
        return dictionary;
    }
}
