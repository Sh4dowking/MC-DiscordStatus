package com.sh4dowking.discordbot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordBot extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("MyPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MyPlugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hello")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage("Hello, " + player.getName() + "!");
            } else {
                sender.sendMessage("Hello from the console!");
            }
            return true;
        }
        return false;
    }
}
