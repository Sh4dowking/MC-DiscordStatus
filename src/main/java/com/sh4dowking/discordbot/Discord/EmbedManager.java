package com.sh4dowking.discordbot;
import java.awt.Color;
import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class EmbedManager {
    private final TextChannel channel;
    private final String statusMessageID;
    private final Dictionary dictionary;
    private final DiscordNotifier discordNotifier;


    EmbedManager(DiscordNotifier discordNotifier, TextChannel channel, String statusMessageID) {
        this.discordNotifier = discordNotifier;
        this.dictionary = discordNotifier.getDictionary();
        this.channel = channel;
        this.statusMessageID = statusMessageID;
    }

    public void updateStatusEmbed(){
        EmbedBuilder embed = new EmbedBuilder();
        createEmbedMessage(embed);

        if(statusMessageID.equals("")) {
            sendNewStatusMessage(channel, embed);
        } else {
            channel.retrieveMessageById(statusMessageID).queue(
                message -> {
                    message.editMessageEmbeds(embed.build()).queue();
                },
                failure -> {
                    sendNewStatusMessage(channel, embed);
                }
            );
        }
    }

    private void sendNewStatusMessage(TextChannel channel, EmbedBuilder embed) {
        channel.sendMessageEmbeds(embed.build()).queue(sentMessage -> {
            dictionary.setValue("statusMessageID", sentMessage.getId());
        });
    }

    public void createEmbedMessage(EmbedBuilder embed) {
        embed.setTitle("Dummy Title");
        embed.setDescription("This is a sample embed message for the Minecraft server status.");
        embed.setColor(Color.BLUE);
        embed.addField("Testing 1", "wow", true);
        embed.addField("Testing 2", "heloo", true);
        embed.addField("Testing 3", "testing", false);
        embed.setFooter("Status Updated");
        embed.setTimestamp(Instant.now());
    }
}   