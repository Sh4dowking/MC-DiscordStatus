package com.sh4dowking.discordbot;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class DiscordBot extends JavaPlugin {
    private JDA jda;
    Guild guild;
    private HashMap<String, String> configKeys;
    
    @Override
    public void onEnable() {
        saveDefaultConfig(); // Create config.yml if id doesn't exists
        initializeConfigKeys();

        if (!validateConfigKeys()) {
            getLogger().severe("Configuration keys are missing or invalid!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize Discord Bot
        try {
            jda = JDABuilder.createDefault(configKeys.get("discordToken")).build();
            getLogger().info("Discord bot is starting...");
            jda.awaitReady();
            getLogger().info("Discord bot is online!");
        } catch (InterruptedException e) {
            getLogger().log(Level.SEVERE, "Failed to start Discord bot: {0}", e.getMessage());
        }

        // Intialize Event Listener
        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        // Initialize Discord Command Listener for specific Server
        guild = jda.getGuildById(configKeys.get("discordServerID"));
        initializeDiscordCommands();
        jda.addEventListener(new DiscordCommandListener(this));
    }

    private void initializeDiscordCommands(){
        jda.updateCommands().queue();
        guild.updateCommands().addCommands(
            Commands.slash("setjoinmessage", "Modify the join message template")
                .addOption(OptionType.STRING, "message", "Use {player} for player name", true),
            Commands.slash("getjoinmessage", "Get the current join message template"),    
            Commands.slash("setleavemessage", "Modify the leave message template")
                .addOption(OptionType.STRING, "message", "Use {player} for player name", true),
            Commands.slash("getleavemessage", "Get the current leave message template")
        ).queue();
            
    }

    private void initializeConfigKeys() {
        configKeys = new HashMap<>();
        configKeys.put("discordToken", "");
        configKeys.put("updatesChannelID", "");
        configKeys.put("joinMessage", "");
        configKeys.put("leaveMessage", "");
        configKeys.put("discordServerID", "");
    }

    private boolean validateConfigKeys() {
        FileConfiguration configFile = getConfig();
        for (String key : configKeys.keySet()) {
            if (!configFile.contains(key) || configFile.getString(key) == null) {
                getLogger().log(Level.SEVERE, "Missing or empty configuration key: {0}", key);
                return false;
            }
            configKeys.put(key, configFile.getString(key));
        }
        return true;
    }


    @Override
    public void onDisable() {
        getLogger().info("MyPlugin has been disabled!");
    }

    // Setters
    public void setMessage(String messageValue, String messageKey){
        getConfig().set(messageValue, messageKey);
        configKeys.put(messageValue, messageKey);
        saveConfig();
    }

    // Getters
    public JDA getJda() {
        return jda;
    }

    public HashMap <String, String> getConfigKeys() {
        return configKeys;
    }
}
