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
    private final JDA jda;
    private final HashMap<String, String> configKeys;
    private final TextChannel updatesChannel;
    private final HashSet<Player> players;


    public EventListener(DiscordBot plugin) {
        this.jda = plugin.getJda();
        this.configKeys = plugin.getConfigKeys();
        this.updatesChannel = jda.getTextChannelById(configKeys.get("updatesChannelID"));
        this.players = new HashSet<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        players.add(player);
        String joinMessage = configKeys.get("joinMessage").replace("{player}", player.getName());
        updatesChannel.sendMessage(joinMessage).queue();
        
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        players.remove(player);
        String leaveMessage = configKeys.get("leaveMessage").replace("{player}", player.getName());
        updatesChannel.sendMessage(leaveMessage).queue();
    }
}