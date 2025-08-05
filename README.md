# MC-DiscordStatus
A lightweight Minecraft plugin that allows you to display and monitor your Minecraft Server status directly on Discord through a dedicated Discord Bot.

## ✨ Features
- TODO

## 📦 Installation
1. Download the latest release from the [Releases](https://github.com/Sh4dowking/MC-DiscordStatus/releases) page.  
2. Place the `.jar` file into your Minecraft Server’s `plugins` folder.
3. Start the Minecraft Server.
4. Open `plugins/MC-DiscordStatus/config.yml` and edit it with your bot and server details (See Configuration below) 
5. Restart the Minecraft server.

## ⚙️ Configuration
Example `config.yml`:
```yaml
discordToken: "YOUR_BOT_TOKEN"
discordServerID: "YOUR_DISCORD_SERVER_ID"
updatesChannelID: "DISCORD_CHANNEL_ID"
```
For further configuration and setup help refer to the official [documentation](sh4dowking.github.io/docs/discordstatus/) website.

## 🛠️ Building from Source
If you want to compile the plugin yourself:
```bash
git clone https://github.com/Sh4dowking/MC-DiscordStatus.git
cd MC-DiscordStatus
mvn clean package
```
Note: You need to modify `<outputDirectory>` in `pom.xml` to the Output Directory of your choice.
