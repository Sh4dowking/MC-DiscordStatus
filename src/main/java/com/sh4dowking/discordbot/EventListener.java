package com.sh4dowking.discordbot;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class EventListener implements Listener {
    private final DiscordBot plugin;
    private final JDA jda;
    private final TextChannel updatesChannel;
    private final HashSet<Player> players;
    private final Dictionary dictionary;
    private final DiscordNotifier discordNotifier;

    public EventListener(DiscordBot plugin, DiscordManager discordManager) {
        this.plugin = plugin;
        this.dictionary = plugin.getDictionary();
        this.discordNotifier = discordManager.getDiscordNotifier();
        this.jda = discordManager.getJda();
        this.players = new HashSet<>();
        this.updatesChannel = jda.getTextChannelById(dictionary.getString("updatesChannelID"));
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        handlePlayerEvent(
            event.getPlayer(),
            true, // joining
            "sendJoinMessage",
            "joinMessage",
            true
        );
        handleEmbed(true);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        handlePlayerEvent(
            event.getPlayer(),
            false, // leaving
            "sendLeaveMessage",
            "leaveMessage",
            true 
        );
        handleEmbed(false);
    }

    private void handlePlayerEvent(Player player, boolean joining, String toggleKey, String messageKey, boolean updateEmbed) {
        if(!dictionary.getBoolean(toggleKey)) return;

        if(joining){
            players.add(player);
        } else {
            players.remove(player);
        }
        String message = dictionary.getString(messageKey).replace("{player}", player.getName());
        discordNotifier.sendMessage(updatesChannel,message);
    }

    private void handleEmbed(boolean joining){
        discordNotifier.updateEmbed();   
    }

}