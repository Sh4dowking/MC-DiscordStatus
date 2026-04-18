package com.sh4dowking.discordstatus.Discord;

import com.util.Dictionary;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class DiscordCommandListener extends ListenerAdapter {
    private final Dictionary dictionary;
    private final DiscordManager discordManager;

    public DiscordCommandListener(Dictionary dictionary) {
        this.dictionary = dictionary;
        this.discordManager = dictionary.getDiscordManager();
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String eventName = event.getName();
        switch (eventName) {
            case "setjoinmessage", "setleavemessage" -> {
                String key = eventName.equals("setjoinmessage") ? "joinMessage" : "leaveMessage";
                String newMessage = getRequiredStringOption(event, "message");
                if (newMessage == null) return;
                setMessage(key, newMessage, event);
            }
            case "setstatustitleon", "setstatusdescon", "setstatustitleoff", "setstatusdescoff", 
                 "setphrasemotd", "setphraseonline", "setphraseplist", "setphrasenoplayers", "setphraseversion", "setphrasestatus" -> {
                String key = getStatusStringKey(eventName);
                String newMessage = getRequiredStringOption(event, "message");
                if (newMessage == null) return;
                setMessage(key, newMessage, event);
                discordManager.getDiscordNotifier().updateEmbed();
            }
            case "setstatuscoloron", "setstatuscoloroff" -> {
                String key = getStatusColorKey(eventName);
                String colorValue = getRequiredStringOption(event, "message");
                if (colorValue == null) return;
                setStatusColor(key, colorValue, event);
            }
            case "getjoinmessage", "getleavemessage" -> {
                String key = eventName.equals("getjoinmessage") ? "joinMessage" : "leaveMessage";
                getMessage(key, event);
            }
            case "getstatustitleon", "getstatusdescon", "getstatustitleoff", "getstatusdescoff",
                 "getphrasemotd", "getphraseonline", "getphraseplist", "getphrasenoplayers", "getphraseversion", "getphrasestatus" -> {
                String key = getStatusStringKey(eventName);
                getMessage(key, event);
            }
            case "getstatuscoloron", "getstatuscoloroff" -> {
                String key = getStatusColorKey(eventName);
                getMessage(key, event);
            }
            case "togglejoinmessage", "toggleleavemessage" -> {
                String key = eventName.equals("togglejoinmessage") ? "sendJoinMessage" : "sendLeaveMessage";
                toggleMessage(key, event);
            }
            case "toggleshowdesc", "toggleshowicon", "toggleshowmotd", "toggleshowonline", "toggleshowplist", "toggleshowversion" -> {
                String key = getShowToggleKey(eventName);
                toggleShowSetting(key, event);
            }
            case "togglestatus" -> {
                dictionary.configureServerIcon();
                discordManager.getDiscordNotifier().refreshEmbed();
                event.reply("Status embed refreshed!").queue();
            }
            default -> event.reply("Unknown command!").setEphemeral(true).queue();
        }
    }

    private void setMessage(String key, String message, SlashCommandInteractionEvent event) {
        dictionary.setValue(key, message);
        event.reply(key + " updated and saved!").queue();
    }

    private void getMessage(String key, SlashCommandInteractionEvent event) {
        String current = dictionary.getString(key);
        event.reply("Current " + key + ": " + current).queue();
    }

    private void toggleMessage(String key, SlashCommandInteractionEvent event) {
        Boolean state = getRequiredBooleanOption(event, "state");
        if (state == null) return;
        dictionary.setValue(key, state);
        event.reply(key + " is now **" + (state ? "enabled" : "disabled") + "**.").queue();
    }

    private void toggleShowSetting(String key, SlashCommandInteractionEvent event) {
        Boolean state = getRequiredBooleanOption(event, "state");
        if (state == null) return;
        dictionary.setValue(key, state);
        if("showServerIcon".equals(key)) {
            discordManager.getDiscordNotifier().refreshEmbed();
        } else {
            discordManager.getDiscordNotifier().updateEmbed();
        }
        event.reply(key + " is now **" + (state ? "enabled" : "disabled") + "**. Status embed updated.").queue();
    }

    private void setStatusColor(String key, String colorValue, SlashCommandInteractionEvent event) {
        String normalized = colorValue.trim().toUpperCase();
        if (!isValidHexColor(normalized)) {
            event.reply("Invalid color format. Use #RRGGBB, for example #00FF00.").setEphemeral(true).queue();
            return;
        }

        dictionary.setValue(key, normalized);
        discordManager.getDiscordNotifier().updateEmbed();
        event.reply(key + " updated to " + normalized + ". Status embed updated.").queue();
    }

    private boolean isValidHexColor(String value) {
        return value.matches("^#[0-9A-F]{6}$");
    }

    private String getStatusStringKey(String eventName) {
        return switch (eventName) {
            case "setstatustitleon", "getstatustitleon" -> "statusMessageTitleOnline";
            case "setstatusdescon", "getstatusdescon" -> "statusMessageDescriptionOnline";
            case "setstatustitleoff", "getstatustitleoff" -> "statusMessageTitleOffline";
            case "setstatusdescoff", "getstatusdescoff" -> "statusMessageDescriptionOffline";
            case "setphrasemotd", "getphrasemotd" -> "phraseMessageOfTheDay";
            case "setphraseonline", "getphraseonline" -> "phrasePlayersOnline";
            case "setphraseplist", "getphraseplist" -> "phrasePlayerList";
            case "setphrasenoplayers", "getphrasenoplayers" -> "phraseNoPlayersOnline";
            case "setphraseversion", "getphraseversion" -> "phraseServerVersion";
            case "setphrasestatus", "getphrasestatus" -> "phraseStatusUpdated";
            default -> "";
        };
    }

    private String getStatusColorKey(String eventName) {
        return switch (eventName) {
            case "setstatuscoloron", "getstatuscoloron" -> "statusColorOnline";
            case "setstatuscoloroff", "getstatuscoloroff" -> "statusColorOffline";
            default -> "";
        };
    }

    private String getShowToggleKey(String eventName) {
        return switch (eventName) {
            case "toggleshowdesc" -> "showDescription";
            case "toggleshowicon" -> "showServerIcon";
            case "toggleshowmotd" -> "showMotd";
            case "toggleshowonline" -> "showPlayersOnline";
            case "toggleshowplist" -> "showPlayerList";
            case "toggleshowversion" -> "showServerVersion";
            default -> "";
        };
    }

    private String getRequiredStringOption(SlashCommandInteractionEvent event, String optionName) {
        OptionMapping option = event.getOption(optionName);
        if (option == null) {
            event.reply("Missing required option: " + optionName).setEphemeral(true).queue();
            return null;
        }
        return option.getAsString();
    }

    private Boolean getRequiredBooleanOption(SlashCommandInteractionEvent event, String optionName) {
        OptionMapping option = event.getOption(optionName);
        if (option == null) {
            event.reply("Missing required option: " + optionName).setEphemeral(true).queue();
            return null;
        }
        return option.getAsBoolean();
    }

}
