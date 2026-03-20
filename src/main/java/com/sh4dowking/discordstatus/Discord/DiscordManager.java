package com.sh4dowking.discordstatus.Discord;

import com.util.Dictionary;

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
            if (this.guild == null) {
                return false;
            }
            initializeDiscordCommands();
            jda.addEventListener(new DiscordCommandListener(dictionary));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
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
            Commands.slash("toggleleavemessage", "Enable or disable leave messages").addOptions(createToggleOption("Enable or disable leave messages")),
            createSetStringCommand("setstatustitleon", "Set the online status embed title"),
            createGetMessageCommand("getstatustitleon", "Get the online status embed title"),
            createSetStringCommand("setstatusdescon", "Set the online status embed description"),
            createGetMessageCommand("getstatusdescon", "Get the online status embed description"),
            createSetStringCommand("setstatuscoloron", "Set the online status embed color (#RRGGBB)"),
            createGetMessageCommand("getstatuscoloron", "Get the online status embed color"),
            createSetStringCommand("setstatustitleoff", "Set the offline status embed title"),
            createGetMessageCommand("getstatustitleoff", "Get the offline status embed title"),
            createSetStringCommand("setstatusdescoff", "Set the offline status embed description"),
            createGetMessageCommand("getstatusdescoff", "Get the offline status embed description"),
            createSetStringCommand("setstatuscoloroff", "Set the offline status embed color (#RRGGBB)"),
            createGetMessageCommand("getstatuscoloroff", "Get the offline status embed color"),
            Commands.slash("toggleshowdesc", "Enable or disable status descriptions").addOptions(createToggleOption("Enable or disable status descriptions")),
            Commands.slash("toggleshowicon", "Enable or disable the server icon thumbnail").addOptions(createToggleOption("Enable or disable the server icon thumbnail")),
            Commands.slash("toggleshowmotd", "Enable or disable the Message of the Day field").addOptions(createToggleOption("Enable or disable the Message of the Day field")),
            Commands.slash("toggleshowonline", "Enable or disable the Players Online field").addOptions(createToggleOption("Enable or disable the Players Online field")),
            Commands.slash("toggleshowplist", "Enable or disable the Player List field").addOptions(createToggleOption("Enable or disable the Player List field")),
            Commands.slash("toggleshowversion", "Enable or disable the Server Version field").addOptions(createToggleOption("Enable or disable the Server Version field")),
            Commands.slash("togglestatus", "Refreshes the Status Embed")
        ).queue();
    }

    private OptionData createToggleOption(String description) {
        return new OptionData(OptionType.BOOLEAN, "state", description, true);
    }

    private CommandData createSetMessageCommand(String name, String description) {
        return Commands.slash(name, description).addOption(OptionType.STRING, "message", "Use {player} for player name", true);
    }

    private CommandData createSetStringCommand(String name, String description) {
        return Commands.slash(name, description).addOption(OptionType.STRING, "message", "New text value", true);
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

    public void shutdown() {
        if (jda != null) {
            jda.shutdownNow();
        }
    }
}