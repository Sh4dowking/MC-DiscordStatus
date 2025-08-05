package com.sh4dowking.discordbot.Discord;
import java.awt.Color;
import java.time.Instant;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.sh4dowking.discordbot.Dictionary;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class EmbedManager {
    private final TextChannel channel;
    private final String statusMessageID;
    private final Dictionary dictionary;
    private final EmbedBuilder embed;

    EmbedManager(DiscordNotifier discordNotifier, TextChannel channel, String statusMessageID) {
        this.dictionary = discordNotifier.getDictionary();
        this.channel = channel;
        this.statusMessageID = statusMessageID;
        this.embed = new EmbedBuilder();
    }

    public void updateStatusEmbed() {
        createEmbedMessage();

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

    private void sendNewStatusMessage() {
        channel.sendMessageEmbeds(embed.build()).queue(sentMessage -> {
            dictionary.setValue("statusMessageID", sentMessage.getId());
        });
    }

    private void createEmbedMessage() {
        embed.clear();
        if(dictionary.isServerOnline()) {
            serverIsOnline();
        } else {
            serverIsOffline();
        }
        embed.setFooter("Status Updated");
        embed.setTimestamp(Instant.now());
    }

    private void serverIsOnline() {
        embed.setColor(Color.GREEN);
        embed.setTitle("🟢 Server is Online");
        embed.setDescription("The Minecraft server is currently online and operational.");
        HashSet<Player> players = dictionary.getOnlinePlayers();
        int maxPlayers = dictionary.getMaxPlayers();
        embed.addField("Players Online", String.valueOf(players.size()), false);
        embed.addField("Max Players", String.valueOf(maxPlayers), false);
        createOnlinePlayerList();
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

    private void serverIsOffline() {
        embed.setColor(Color.RED);
        embed.setTitle("🔴 Server is Offline");
        embed.setDescription("The Minecraft server is currently offline. Please check back later.");
    }

    public void sendServerCrashMessage(Throwable throwable) {
        embed.setColor(Color.RED);
        embed.setTitle("❌ Server Crashed");
        embed.setDescription("The Minecraft server has crashed. Please investigate the issue.");
        embed.addField("Error", throwable.getClass().getName() + ": " + throwable.getMessage(), false);
    }
}   