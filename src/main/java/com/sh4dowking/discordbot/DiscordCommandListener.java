package com.sh4dowking.discordbot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordCommandListener extends ListenerAdapter {
    private final DiscordBot plugin;

    public DiscordCommandListener(DiscordBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String eventName = event.getName();
        switch (eventName) {
            case "setjoinmessage" -> {
                    String newMessage = event.getOption("message").getAsString();
                    plugin.setMessage("joinMessage", newMessage);
                    event.reply("Join message updated and saved!").queue();
                }
            case "getjoinmessage" -> {
                String currentMessage = plugin.getConfigKeys().get("joinMessage");
                event.reply("Current join message: " + currentMessage).queue();
            }
            case "setleavemessage" -> {
                    String newMessage = event.getOption("message").getAsString();
                    plugin.setMessage("leaveMessage", newMessage);
                    event.reply("Leave message updated and saved!").queue();
                }
            case "getleavemessage" -> {
                String currentLeaveMessage = plugin.getConfigKeys().get("leaveMessage");
                event.reply("Current leave message: " + currentLeaveMessage).queue();
            }
            default -> event.reply("Unknown command!").setEphemeral(true).queue();
        }
    }
}
