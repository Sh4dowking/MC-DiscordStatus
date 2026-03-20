package com.sh4dowking.discordstatus;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import com.sh4dowking.discordstatus.Discord.DiscordManager;
import com.util.Dictionary;


public class DiscordStatus extends JavaPlugin {
    private Dictionary dictionary;
    private DiscordManager discordManager;
    
    @Override
    public void onEnable() {
        Logger logger = getLogger();
        Server server = getServer();

        // Plugin startup logic
        logger.info("DiscordStatus plugin is enabled!");
        saveDefaultConfig(); // Create config.yml if it doesn't exists
        this.dictionary = new Dictionary(this);

        // Validate configuration keys
        if(!dictionary.hasValidConfigKeys()) {
            logger.severe("Configuration validation failed. Please check your config.yml.");
            server.getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize Discord Bot
        this.discordManager = new DiscordManager(dictionary);
        dictionary.setDiscordManager(discordManager);
        if(!discordManager.discordSetupWasSuccessful()) {
            logger.severe("Discord bot initialization failed. Please check your Discord token.");
            server.getPluginManager().disablePlugin(this);
            return;
        }
        
        // Intialize Minecraft Event Listener
        server.getPluginManager().registerEvents(new EventListener(this.dictionary, this.discordManager), this);
        dictionary.setServerStatus(true);
        discordManager.getDiscordNotifier().refreshEmbed();

        // Server Crash Handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            discordManager.getDiscordNotifier().serverCrashed(throwable);
        });
    }

    @Override
    public void onDisable() {
        if (dictionary != null) {
            dictionary.setServerStatus(false);
        }
        if (discordManager != null) {
            if (discordManager.getDiscordNotifier() != null) {
                discordManager.getDiscordNotifier().updateEmbed();
            }
            discordManager.shutdown();
        }
        getLogger().info("DiscordStatus plugin is disabled!");
    }
}
