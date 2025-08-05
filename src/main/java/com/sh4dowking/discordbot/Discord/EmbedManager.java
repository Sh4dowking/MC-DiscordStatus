package com.sh4dowking.discordbot.Discord;
import java.awt.Color;
import java.time.Instant;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.util.Dictionary;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

public class EmbedManager {
    private final TextChannel channel;
    private String statusMessageID;
    private final Dictionary dictionary;
    private final EmbedBuilder embed;

    EmbedManager(DiscordNotifier discordNotifier, TextChannel channel, String statusMessageID) {
        this.dictionary = discordNotifier.getDictionary();
        this.channel = channel;
        this.embed = new EmbedBuilder();
    }

    public void refreshStatusEmbed() {
        createEmbed();
        if (statusMessageID.equals("")) {
            sendNewStatusMessage();
        } else {
            channel.retrieveMessageById(statusMessageID).queue(
                message -> {
                    // Delete the old message and send new one
                    message.delete().queue(
                        success -> sendNewStatusMessage(),
                        failure -> sendNewStatusMessage()
                    );
                },
                failure -> {
                    sendNewStatusMessage();
                }
            );
        }
    }

    public void updateStatusEmbed() {
        createEmbed();
        if(statusMessageID.equals("")) {
            sendNewStatusMessage();
        } else {
            channel.retrieveMessageById(statusMessageID).queue(
                message -> {
                    message.editMessageEmbeds(embed.build()).queue();
                },
                failure -> {
                    sendNewStatusMessage();
                }
            );
        }
    }

    private void createEmbed(){
        createEmbedMessage();
        statusMessageID = dictionary.getString("statusMessageID");
    }

    private void sendNewStatusMessage() {
        channel.sendMessageEmbeds(embed.build())
            .addFiles(FileUpload.fromData(dictionary.getServerIconFile(), "server-icon.png"))
            .queue(sentMessage -> {
                dictionary.setValue("statusMessageID", sentMessage.getId());
            });
    }

    private void createEmbedMessage() {
        embed.clear();
        embed.setThumbnail("attachment://server-icon.png");
        embed.addField("Message of the Day", dictionary.getMotd(), false);
        if(dictionary.isServerOnline()) {
            embed.setColor(Color.GREEN);
            embed.setTitle("🟢 Server is Online");
            embed.setDescription("The Minecraft server is currently online and operational.");
            HashSet<Player> players = dictionary.getOnlinePlayers();
            int maxPlayers = dictionary.getMaxPlayers();
            embed.addField("Players Online", String.valueOf(players.size())+"/"+String.valueOf(maxPlayers), false);
            createOnlinePlayerList();
        } else {
            embed.setColor(Color.RED);
            embed.setTitle("🔴 Server is Offline");
            embed.setDescription("The Minecraft server is currently offline. Please check again later.");
        }
        embed.addField("Server Version", "`"+dictionary.getServerVersion()+"`", false);
        embed.setFooter("Status Updated");
        embed.setTimestamp(Instant.now());
    }
    
    private void createOnlinePlayerList(){
        HashSet<Player> players = dictionary.getOnlinePlayers();
        HashSet<String> playerNames = new HashSet<>();
        for(Player p:players) {
        	playerNames.add(p.getName());
        }
        if (playerNames.isEmpty()) {
            embed.addField("Player List", "No players online.", false);
        } else {
            String playerList = playerNames.stream()
                .limit(20)
                .map(name -> "• `" + name + "`")
                .collect(Collectors.joining("\n"));

            if (playerNames.size() > 20) {
                playerList += "\n...and " + (playerNames.size() - 20) + " more";
            }
            embed.addField("Player List", playerList, false);
        }
    }

    public void sendServerCrashMessage(Throwable throwable) {
        embed.setColor(Color.RED);
        embed.setTitle("❌ Server Crashed");
        embed.setDescription("The Minecraft server has crashed. Please investigate the issue.");
        embed.addField("Error", throwable.getClass().getName() + ": " + throwable.getMessage(), false);
    }
}   