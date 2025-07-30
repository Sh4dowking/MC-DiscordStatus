package com.sh4dowking.discordbot;
import java.util.*;
import org.bukkit.configuration.file.FileConfiguration;

public class Dictionary {
    private final DiscordBot plugin;
    private HashMap<String, Object> configKeys;

    public Dictionary(DiscordBot plugin) {
        this.plugin = plugin;
        this.configKeys = new HashMap<>();
        initializeConfigKeys();
    }

    private void initializeConfigKeys() {
        configKeys = new HashMap<>();
        HashSet<String> defaultKeys = new HashSet<>(Arrays.asList(
            "discordToken", "discordServerID", "updatesChannelID", "joinMessage", 
            "leaveMessage", "statusChannelID", "statusMessageID", "sendJoinMessage", 
            "sendLeaveMessage"
        ));
        for(String key : defaultKeys) {
            configKeys.put(key, "");
        }
    }

    private boolean getValidConfigKeys() {
        FileConfiguration configFile = plugin.getConfig();
        HashSet<String> exceptionKeys = new HashSet<>(Arrays.asList("embedMessageID"));
        for(String key : configKeys.keySet()) {
            if(exceptionKeys.contains(key)) {
                configKeys.put(key,"");
                continue;
            } else if (!configFile.contains(key) || configFile.getString(key) == null) {
                return false;
            }
            configKeys.put(key, configFile.getString(key));
        }
        return true;
    }

    // Setters
    public void setValue(String key, Object value) {
        configKeys.put(key, value);
        plugin.getConfig().set(key, value);
        plugin.saveConfig();
    }

    // Getters
    public String getString(String key) {
        Object val = configKeys.get(key);
        if (val instanceof String) {
            return (String) val;
        } else if (val != null) {
            return val.toString();
        }
        return null;
    }

    public boolean getBoolean(String key) {
        Object val = configKeys.get(key);
        if (val instanceof Boolean) {
            return (Boolean) val;
        } else if (val instanceof String) {
            return Boolean.parseBoolean((String) val);
        }
        return false; // default fallback
    }

    public boolean hasValidConfigKeys() {
        return getValidConfigKeys();
    }
}