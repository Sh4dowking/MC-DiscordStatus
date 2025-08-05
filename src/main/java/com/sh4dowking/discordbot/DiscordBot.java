package com.sh4dowking.discordbot;

import org.bukkit.plugin.java.JavaPlugin;

import com.sh4dowking.discordbot.Discord.DiscordManager;


public class DiscordBot extends JavaPlugin {
    private Dictionary dictionary;
    private DiscordManager discordManager;
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("DiscordBot plugin is enabled!");
        saveDefaultConfig(); // Create config.yml if id doesn't exists
        this.dictionary = new Dictionary(this);
        dictionary.setMaxPlayers(getServer().getMaxPlayers());

        // Validate configuration keys
        if(!dictionary.hasValidConfigKeys()) {
            getLogger().severe("Configuration validation failed. Please check your config.yml.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize Discord Bot
        this.discordManager = new DiscordManager(dictionary);
        if(!discordManager.discordSetupWasSuccessful()) {
            getLogger().severe("Discord bot initialization failed. Please check your Discord token.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Intialize Minecraft Event Listener
        getServer().getPluginManager().registerEvents(new EventListener(this.dictionary, this.discordManager), this);
        dictionary.setServerStatus(true); // Set server status to online
        discordManager.getDiscordNotifier().updateEmbed();
        
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            discordManager.getDiscordNotifier().serverCrashed(throwable);
        });
    }

    @Override
    public void onDisable() {
        dictionary.setServerStatus(false); // Set server status to offline
        discordManager.getDiscordNotifier().updateEmbed(); // Update embed before shutdown
        getLogger().info("DiscordBot plugin is disabled!");
    }
}
