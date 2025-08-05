package com.sh4dowking.discordbot;

import org.bukkit.plugin.java.JavaPlugin;

import com.sh4dowking.discordbot.Discord.DiscordManager;
import com.util.Dictionary;


public class DiscordBot extends JavaPlugin {
    private Dictionary dictionary;
    private DiscordManager discordManager;
    
    @Override
    public void onEnable() {
        // Plugin startup logic
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
        this.discordManager = new DiscordManager(dictionary);
        if(!discordManager.discordSetupWasSuccessful()) {
            getLogger().severe("Discord bot initialization failed. Please check your Discord token.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Intialize Minecraft Event Listener
        getServer().getPluginManager().registerEvents(new EventListener(this.dictionary, this.discordManager), this);
        dictionary.setServerStatus(true);
        discordManager.getDiscordNotifier().updateEmbed();

        // Server Crash Handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            discordManager.getDiscordNotifier().serverCrashed(throwable);
        });
    }

    @Override
    public void onDisable() {
        // Set server status to offline
        dictionary.setServerStatus(false);
        discordManager.getDiscordNotifier().updateEmbed();
        getLogger().info("DiscordBot plugin is disabled!");
    }
}
