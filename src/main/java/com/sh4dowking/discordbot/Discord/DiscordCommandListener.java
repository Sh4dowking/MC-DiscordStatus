package com.sh4dowking.discordbot.Discord;

import com.util.Dictionary;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
                String newMessage = event.getOption("message").getAsString();
                setMessage(key, newMessage, event);
            }
            case "getjoinmessage", "getleavemessage" -> {
                String key = eventName.equals("getjoinmessage") ? "joinMessage" : "leaveMessage";
                getMessage(key, event);
            }
            case "togglejoinmessage", "toggleleavemessage" -> {
                String key = eventName.equals("togglejoinmessage") ? "sendJoinMessage" : "sendLeaveMessage";
                toggleMessage(key, event);
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
        boolean state = event.getOption("state").getAsBoolean();
        dictionary.setValue(key, state);
        event.reply(key + " is now **" + (state ? "enabled" : "disabled") + "**.").queue();
    }

}
