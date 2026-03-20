package com.sh4dowking.discordbot;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sh4dowking.discordbot.Discord.DiscordManager;
import com.sh4dowking.discordbot.Discord.DiscordNotifier;
import com.util.Dictionary;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class EventListener implements Listener {
    private final TextChannel updatesChannel;
    private final Dictionary dictionary;
    private final DiscordNotifier discordNotifier;

    public EventListener(Dictionary dictionary, DiscordManager discordManager) {
        this.dictionary = dictionary;
        this.discordNotifier = discordManager.getDiscordNotifier();
        JDA jda = discordManager.getJda();
        this.updatesChannel = jda.getTextChannelById(dictionary.getString("updatesChannelID"));
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        dictionary.addPlayer(player);
        handlePlayerEvent(
            player,
            "sendJoinMessage",
            "joinMessage"
        );
        handleEmbed();
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        dictionary.removePlayer(player);
        handlePlayerEvent(
            player,
            "sendLeaveMessage",
            "leaveMessage"
        );
        handleEmbed();
    }

    private void handlePlayerEvent(Player player, String toggleKey, String messageKey) {
        if(!dictionary.getBoolean(toggleKey)) return;
        String message = dictionary.getString(messageKey).replace("{player}", player.getName());
        discordNotifier.sendMessage(updatesChannel,message);
    }

    private void handleEmbed() {
        discordNotifier.updateEmbed();   
    }
}