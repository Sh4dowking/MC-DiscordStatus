package com.sh4dowking.discordbot.Discord;

import com.sh4dowking.discordbot.Dictionary;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class DiscordManager{
    private final Dictionary dictionary;
    private JDA jda;
    private Guild guild;
    private DiscordNotifier discordNotifier;


    public DiscordManager(Dictionary dictionary){
        this.dictionary = dictionary;
    }

    private boolean initializeDiscordBot(){
        try {
            String discordToken = dictionary.getString("discordToken");
            this.jda = JDABuilder.createDefault(discordToken).build();
            jda.awaitReady();
            this.discordNotifier = new DiscordNotifier(this, dictionary);
            // Initialize Discord Command Listener for specific Server
            String discordServerID = dictionary.getString("discordServerID");
            this.guild = jda.getGuildById(discordServerID);
            initializeDiscordCommands();
            jda.addEventListener(new DiscordCommandListener(dictionary));

        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    private void initializeDiscordCommands() {
        jda.updateCommands().queue();
        guild.updateCommands().addCommands(
            createSetMessageCommand("setjoinmessage", "Modify the join message template"),
            createGetMessageCommand("getjoinmessage", "Get the current join message template"),
            createSetMessageCommand("setleavemessage", "Modify the leave message template"),
            createGetMessageCommand("getleavemessage", "Get the current leave message template"),
            Commands.slash("togglejoinmessage", "Enable or disable join messages").addOptions(createToggleOption("Enable or disable join messages")),
            Commands.slash("toggleleavemessage", "Enable or disable leave messages").addOptions(createToggleOption("Enable or disable leave messages"))
        ).queue();
    }

    private OptionData createToggleOption(String description) {
        return new OptionData(OptionType.BOOLEAN, "state", description, true);
    }

    private CommandData createSetMessageCommand(String name, String description) {
        return Commands.slash(name, description).addOption(OptionType.STRING, "message", "Use {player} for player name", true);
    }

    private CommandData createGetMessageCommand(String name, String description) {
        return Commands.slash(name, description);
    }

    // Getters
    public boolean discordSetupWasSuccessful() {
        return initializeDiscordBot();
    }

    public JDA getJda() {
        return this.jda;
    }

    public DiscordNotifier getDiscordNotifier() {
        return this.discordNotifier;
    }
}