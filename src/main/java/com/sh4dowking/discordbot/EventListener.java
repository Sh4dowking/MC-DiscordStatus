package com.sh4dowking.discordbot;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sh4dowking.discordbot.Discord.DiscordManager;
import com.sh4dowking.discordbot.Discord.DiscordNotifier;
import com.util.Dictionary;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class EventListener implements Listener {
    private final JDA jda;
    private final TextChannel updatesChannel;
    private final HashSet<Player> players;
    private final Dictionary dictionary;
    private final DiscordNotifier discordNotifier;

    public EventListener(Dictionary dictionary, DiscordManager discordManager) {
        this.dictionary = dictionary;
        this.discordNotifier = discordManager.getDiscordNotifier();
        this.jda = discordManager.getJda();
        this.players = new HashSet<>();
        this.updatesChannel = jda.getTextChannelById(dictionary.getString("updatesChannelID"));
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        dictionary.addPlayer(player);
        handlePlayerEvent(
            player,
            true, // joining
            "sendJoinMessage",
            "joinMessage",
            true
        );
        handleEmbed();
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        dictionary.removePlayer(player);
        handlePlayerEvent(
            player,
            false, // leaving
            "sendLeaveMessage",
            "leaveMessage",
            true 
        );
        handleEmbed();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String discordMessage = "`" + player.getName() + "` " + message;
        discordNotifier.sendMessage(updatesChannel, discordMessage);
    }

    private void handlePlayerEvent(Player player, boolean joining, String toggleKey, String messageKey, boolean updateEmbed) {
        if(joining){
            players.add(player);
        } else {
            players.remove(player);
        }
        if(!dictionary.getBoolean(toggleKey)) return;
        String message = dictionary.getString(messageKey).replace("{player}", player.getName());
        discordNotifier.sendMessage(updatesChannel,message);
    }

    private void handleEmbed() {
        discordNotifier.updateEmbed();   
    }

}