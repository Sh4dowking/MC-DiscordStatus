# MC-DiscordStatus
A lightweight Minecraft plugin that allows you to display and monitor your Minecraft Server status directly on Discord through a dedicated Discord Bot.

## Features
- Live online/offline status embed in Discord
- Player join and leave notifications
- Configurable join and leave message templates
- Configurable status title, description and color for online and offline states
- Configurable embed sections:
	- description
	- server icon thumbnail
	- MOTD
	- players online count
	- player list
	- server version
- Slash commands to manage status text, colors and toggles without editing files

## Installation

### 1. Install the plugin jar
1. Download the latest release from [Releases](https://github.com/Sh4dowking/MC-DiscordStatus/releases).
2. Place the `DiscordStatus.jar` file in your server `plugins` folder.
3. Start the server once to generate `plugins/DiscordStatus/config.yml`.

### 2. Create a Discord application and bot
1. Open the Discord Developer Portal: <https://discord.com/developers/applications>
2. Click New Application and enter a name (for example `Status Bot`).
3. You should now be on a page where you can configure your Application, if not click on the Application you just created.
4. Go to the Bot section on the left side.
5. Under Token, click Reset Token (or Copy Token) then paste the token into the value of `discordToken` in `plugins/DiscordStatus/config.yml`.
6. In the same Application on the left side go to OAuth2 > OAuth2 URL Generator.
7. Under Scopes, select:
	- `bot`
	- `applications.commands`
8. Under Bot Permissions, select at least:
	- `View Channels`
	- `Send Messages`
	- `Embed Links`
	- `Read Message History`
	- `Manage Messages` (used when refreshing/replacing status messages)
9. On the very bottom of the page a URL will be generated. Open the generated URL and invite the bot to your server.

### 3. Enable Developer Mode in Discord (for copying IDs)
1. Open Discord Settings.
2. Go to Advanced.
3. Enable Developer Mode.

After Developer Mode is enabled, right-click and use Copy Server ID / Copy Channel ID / Copy Message ID to fill in the rest of the values in `plugins/DiscordStatus/config.yml`.

- `discordServerID`: right-click your server icon > Copy Server ID
- `updatesChannelID`: right-click your updates channel > Copy Channel ID
- `statusChannelID`: right-click your status channel > Copy Channel ID
- `statusMessageID`:
  - optional on first run
  - you can leave it empty and let the plugin create/store it automatically
  - if needed later, right-click the status message > Copy Message ID

Tip: `updatesChannelID` and `statusChannelID` can be the same channel if you prefer.

### 4. Final Steps
1. Configure the rest of the `plugins/DiscordStatus/config.yml` (A default file template can be found below).
2. Restart the Minecraft Server for the changes to take effect.
3. Enjoy the Minecraft Plugin :)

## Configuration

Default `config.yml`:

```yaml
discordToken: "YOUR_BOT_TOKEN"
discordServerID: "YOUR_DISCORD_SERVER_ID"
updatesChannelID: "YOUR_DISCORD_CHANNEL_ID"
statusChannelID: "YOUR_STATUS_CHANNEL_ID"
statusMessageID: "YOUR_STATUS_MESSAGE_ID"

sendJoinMessage: true
sendLeaveMessage: true
joinMessage: "`{player}` joined the Server!"
leaveMessage: "`{player}` left the Server!"

statusMessageTitleOnline: "🟢 Server is Online"
statusMessageDescriptionOnline: "The Minecraft server is currently online and operational."
statusColorOnline: "#00FF00"

statusMessageTitleOffline: "🔴 Server is Offline"
statusMessageDescriptionOffline: "The Minecraft server is currently offline, please check again later."
statusColorOffline: "#FF0000"

showDescription: true
showServerIcon: true
showMotd: true
showPlayersOnline: true
showPlayerList: true
showServerVersion: true
```

Notes:
- `statusColorOnline` and `statusColorOffline` must use `#RRGGBB` format.
- `statusMessageID` can be left empty, the plugin will create/update it automatically.

## Building from Source
If you want to compile the plugin yourself:
```bash
git clone https://github.com/Sh4dowking/MC-DiscordStatus.git
cd MC-DiscordStatus
mvn clean package
```
Note: You need to modify `<outputDirectory>` in `pom.xml` to the Output Directory of your choice.
