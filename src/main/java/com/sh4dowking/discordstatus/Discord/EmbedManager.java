package com.sh4dowking.discordstatus.Discord;
import java.awt.Color;
import java.io.File;
import java.time.Instant;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.util.Dictionary;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

public class EmbedManager {
    private final TextChannel channel;
    private String statusMessageID;
    private final Dictionary dictionary;
    private final EmbedBuilder embed;
    private String lastIconPath;
    private long lastIconModified;

    EmbedManager(DiscordNotifier discordNotifier, TextChannel channel, String statusMessageID) {
        this.dictionary = discordNotifier.getDictionary();
        this.channel = channel;
        this.statusMessageID = statusMessageID == null ? "" : statusMessageID;
        this.embed = new EmbedBuilder();
        cacheCurrentIconSignature();
    }

    public void refreshStatusEmbed() {
        if (channel == null) {
            return;
        }
        createEmbed();
        if (statusMessageID == null || statusMessageID.isBlank()) {
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
        if (channel == null) {
            return;
        }
        createEmbed();
        if (dictionary.getBoolean("showServerIcon") && hasServerIconChanged()) {
            refreshStatusEmbed();
            return;
        }

        if(statusMessageID == null || statusMessageID.isBlank()) {
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

    public void updateStatusEmbedBlocking() {
        if (channel == null) {
            return;
        }

        createEmbed();
        if (dictionary.getBoolean("showServerIcon") && hasServerIconChanged()) {
            refreshStatusEmbedBlocking();
            return;
        }

        if (statusMessageID == null || statusMessageID.isBlank()) {
            sendNewStatusMessageBlocking();
            return;
        }

        try {
            channel.retrieveMessageById(statusMessageID).complete().editMessageEmbeds(embed.build()).complete();
        } catch (Exception ignored) {
            sendNewStatusMessageBlocking();
        }
    }

    private void createEmbed(){
        dictionary.configureServerIcon();
        createEmbedMessage();
        String configuredStatusMessageID = dictionary.getString("statusMessageID");
        statusMessageID = configuredStatusMessageID == null ? "" : configuredStatusMessageID;
    }

    private void sendNewStatusMessage() {
        File iconFile = dictionary.getServerIconFile();
        if(dictionary.getBoolean("showServerIcon") && iconFile != null && iconFile.exists()) {
            channel.sendMessageEmbeds(embed.build())
                .addFiles(FileUpload.fromData(iconFile, "server-icon.png"))
                .queue(sentMessage -> {
                    dictionary.setValue("statusMessageID", sentMessage.getId());
                    cacheCurrentIconSignature();
                });
            return;
        }

        channel.sendMessageEmbeds(embed.build()).queue(sentMessage -> {
            dictionary.setValue("statusMessageID", sentMessage.getId());
            cacheCurrentIconSignature();
        });
    }

    private void sendNewStatusMessageBlocking() {
        File iconFile = dictionary.getServerIconFile();
        Message sentMessage;

        if (dictionary.getBoolean("showServerIcon") && iconFile != null && iconFile.exists()) {
            sentMessage = channel.sendMessageEmbeds(embed.build())
                .addFiles(FileUpload.fromData(iconFile, "server-icon.png"))
                .complete();
        } else {
            sentMessage = channel.sendMessageEmbeds(embed.build()).complete();
        }

        dictionary.setValue("statusMessageID", sentMessage.getId());
        cacheCurrentIconSignature();
    }

    private void refreshStatusEmbedBlocking() {
        if (statusMessageID == null || statusMessageID.isBlank()) {
            sendNewStatusMessageBlocking();
            return;
        }

        try {
            channel.retrieveMessageById(statusMessageID).complete().delete().complete();
        } catch (Exception ignored) {
            // Ignore and still create the replacement status message.
        }
        sendNewStatusMessageBlocking();
    }

    private void createEmbedMessage() {
        embed.clear();
        File iconFile = dictionary.getServerIconFile();
        if(dictionary.getBoolean("showServerIcon") && iconFile != null && iconFile.exists()) {
            embed.setThumbnail("attachment://server-icon.png");
        }
        if(dictionary.getBoolean("showMotd")) {
            embed.addField("Message of the Day", dictionary.getMotd(), false);
        }
        if(dictionary.isServerOnline()) {
            embed.setColor(getConfiguredColor("statusColorOnline", Color.GREEN));
            embed.setTitle(dictionary.getString("statusMessageTitleOnline"));
            if(dictionary.getBoolean("showDescription")) {
                embed.setDescription(dictionary.getString("statusMessageDescriptionOnline"));
            }
            HashSet<Player> players = dictionary.getOnlinePlayers();
            int maxPlayers = dictionary.getMaxPlayers();
            if(dictionary.getBoolean("showPlayersOnline")) {
                embed.addField("Players Online", String.valueOf(players.size())+"/"+String.valueOf(maxPlayers), false);
            }
            if(dictionary.getBoolean("showPlayerList")) {
                createOnlinePlayerList();
            }
        } else {
            embed.setColor(getConfiguredColor("statusColorOffline", Color.RED));
            embed.setTitle(dictionary.getString("statusMessageTitleOffline"));
            if(dictionary.getBoolean("showDescription")) {
                embed.setDescription(dictionary.getString("statusMessageDescriptionOffline"));
            }
        }
        if(dictionary.getBoolean("showServerVersion")) {
            embed.addField("Server Version", "`"+dictionary.getServerVersion()+"`", false);
        }
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

    private Color getConfiguredColor(String key, Color fallback) {
        String colorValue = dictionary.getString(key);
        if (colorValue == null || colorValue.isBlank()) {
            return fallback;
        }

        try {
            return Color.decode(colorValue);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private boolean hasServerIconChanged() {
        File iconFile = dictionary.getServerIconFile();
        if (iconFile == null) {
            return false;
        }

        String currentPath = iconFile.getAbsolutePath();
        long currentModified = iconFile.lastModified();
        boolean changed = !currentPath.equals(lastIconPath) || currentModified != lastIconModified;
        if (changed) {
            lastIconPath = currentPath;
            lastIconModified = currentModified;
        }
        return changed;
    }

    private void cacheCurrentIconSignature() {
        File iconFile = dictionary.getServerIconFile();
        if (iconFile == null) {
            lastIconPath = "";
            lastIconModified = -1L;
            return;
        }

        lastIconPath = iconFile.getAbsolutePath();
        lastIconModified = iconFile.lastModified();
    }
}   