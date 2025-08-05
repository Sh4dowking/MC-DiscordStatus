package com.sh4dowking.discordbot.Discord;

import com.sh4dowking.discordbot.Dictionary;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordNotifier {
    private final Dictionary dictionary;
    private final DiscordManager discordManager;
    private final JDA jda;
    private final EmbedManager embedManager;
    private final TextChannel updatesChannel;

    public DiscordNotifier(DiscordManager discordManager, Dictionary dictionary) {
        this.discordManager = discordManager;
        this.jda = discordManager.getJda();
        this.dictionary = dictionary;
        String updatesChannelID = dictionary.getString("updatesChannelID");
        this.updatesChannel = jda.getTextChannelById(updatesChannelID);
        String statusChannelID = dictionary.getString("statusChannelID");
        TextChannel statusChannel = jda.getTextChannelById(statusChannelID);
        String statusMessageID = dictionary.getString("statusMessageID");
        this.embedManager = new EmbedManager(this, statusChannel, statusMessageID);
    }

    public void sendMessage(TextChannel channel, String message){
        channel.sendMessage(message).queue();
    }

    public void updateEmbed() {
        embedManager.updateStatusEmbed();
    }

    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }

    public Dictionary getDictionary() {
        return this.dictionary;
    }

    public void serverCrashed(Throwable throwable) {
        embedManager.sendServerCrashMessage(throwable);
    }
}
