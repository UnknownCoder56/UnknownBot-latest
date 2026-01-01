# UnknownBot

> [!IMPORTANT]
> **⚠️ This project is now ARCHIVED ⚠️**
> 
> This bot has been archived due to the sunset of the Javacord API. 
> 
> **Please use the successor bot:** [Maxis](https://github.com/UnknownCoder56/maxis) - Built with Python and discord.py

<div align="center">
  <img src="https://img.shields.io/badge/Status-Archived-red" alt="Status">
  <img src="https://img.shields.io/badge/Version-4.0.0-blue" alt="Version">
  <img src="https://img.shields.io/badge/Java-17-orange" alt="Java">
  <img src="https://img.shields.io/badge/Discord-Bot-7289da" alt="Discord Bot">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="License">
</div>

UnknownBot is a powerful, multipurpose Discord bot built with Java that provides utility, moderation, and economy features for Discord servers. Created by **UnknownPro 56**, this bot is designed to enhance your Discord server experience with a comprehensive set of commands and features.

## 🌟 Features

### 🔧 Utility Commands
- **User Information**: Get detailed information about users and servers
- **Direct Messaging**: Send DM notifications to users
- **Games**: Play Rock Paper Scissors with the bot
- **Custom Replies**: Set up automated responses to specific messages
- **Text-to-Image**: Convert text into image format
- **Calculator**: Perform basic mathematical operations
- **File Creation**: Create files from text content
- **Date & Time**: Display current date and time
- **Ping**: Check bot latency

### 🛡️ Moderation Commands
- **Message Management**: Clear messages, nuke channels
- **User Management**: Kick, ban, mute/unmute users
- **Warning System**: Warn users, view warnings, clear warnings
- **Comprehensive moderation tools for server administrators

### 💰 Economy System
- **Daily Rewards**: Earn coins with daily, weekly, and monthly bonuses
- **Balance System**: Check your balance and transfer money to other users
- **Leaderboards**: Global and server-specific wealth rankings
- **Shop System**: Buy and use items from the in-game shop
- **Work & Rob**: Earn money through work or by robbing other users
- **Inventory Management**: Track and use purchased items

### ⚡ Slash Commands
- Modern Discord slash command support
- Interactive command interface
- Enhanced user experience with Discord's native command system

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MongoDB database
- Discord Bot Token (get one from [Discord Developer Portal](https://discord.com/developers/applications))

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/UnknownCoder56/UnknownBot-latest.git
   cd UnknownBot-latest
   ```

2. **Create a Discord Application**
   - Go to [Discord Developer Portal](https://discord.com/developers/applications)
   - Create a new application
   - Go to "Bot" section and create a bot
   - Copy the bot token for the `TOKEN` environment variable
   - Enable the following bot permissions:
     - Administrator (recommended for full functionality)
     - Or specific permissions: Send Messages, Read Message History, Use Slash Commands, Manage Messages, Ban Members, Kick Members

3. **Set up environment variables**
   ```bash
   export TOKEN="your_discord_bot_token"
   export CONNSTR="mongodb://localhost:27017/unknownbot"
   # Or for MongoDB Atlas:
   # export CONNSTR="mongodb+srv://user:password@cluster.mongodb.net/unknownbot"
   ```

4. **Build the project**
   ```bash
   mvn clean package
   ```

5. **Run the bot**
   ```bash
   java -jar target/unknownbot-1.0-jar-with-dependencies.jar
   ```

### Docker Deployment

1. **Build the Docker image**
   ```bash
   docker build -t unknownbot .
   ```

2. **Run the container**
   ```bash
   docker run -d -p 8080:8080 -p 12102:12102 \
     -e TOKEN="your_discord_bot_token" \
     -e CONNSTR="your_mongodb_connection_string" \
     unknownbot
   ```

### Heroku Deployment

This project is ready for Heroku deployment with the included `Procfile`. Simply:

1. Create a new Heroku app
2. Set the environment variables `TOKEN` and `CONNSTR`
3. Deploy the repository

## 📖 Command Reference

### Utility Commands
| Command | Description |
|---------|-------------|
| `>hello` | Says hello to you |
| `>userinfo (user)` | Shows user information |
| `>dm (mention) "message"` | Send a DM to a user |
| `>rps (choice)` | Play Rock Paper Scissors (r/p/s or rock/paper/scissors) |
| `>setting (type) (true/false)` | Change user settings (bankdm, passive) |
| `>admes (query)` | Ask anything to the bot |
| `>tti (text)` | Convert text to image |
| `>reply (text),(reply)` | Set up custom replies |
| `>noreply (text)` | Disable custom reply |
| `>ping` | Display bot latency |
| `>dt` | Show current date and time |
| `>replies` | Display all custom replies |
| `>calc (num1),(sign),(num2)` | Perform calculations (+, -, *, /) |
| `>serverinfo` | Show server information |
| `>botinfo` | Show bot information |
| `>help (category)` | Display help for specific category |
| `>makefile (name) (content)` | Create a file from text |

### Moderation Commands
| Command | Description |
|---------|-------------|
| `>nuke` | Clear all messages in a channel |
| `>clear (amount)` | Clear specified number of messages |
| `>kick (mention)` | Kick a user from the server |
| `>ban (mention)` | Ban a user from the server |
| `>unban (mention)` | Unban a user |
| `>mute (mention)` | Mute a user (disable chat and VC) |
| `>unmute (mention)` | Unmute a user |
| `>warn (mention) "reason"` | Warn a user |
| `>getwarns (mention)` | Get all warnings for a user |
| `>nowarns (mention)` | Clear all warnings for a user |

### Economy Commands
| Command | Description |
|---------|-------------|
| `>daily` | Get daily 💰 5000 coins |
| `>weekly` | Get weekly 💰 10000 coins |
| `>monthly` | Get monthly 💰 50000 coins |
| `>bal (mention)` | Check balance (yours or mentioned user) |
| `>glb` | Global leaderboard |
| `>lb` | Server leaderboard |
| `>give (amount) (mention)` | Transfer money to another user |
| `>work` | Work to earn money |
| `>rob (mention)` | Rob another user |
| `>shop` | View the item shop |
| `>buy (item)` | Buy an item from the shop |
| `>use (item)` | Use an item from inventory |
| `>inv` | View your inventory |

## 🔧 Configuration

### Environment Variables
- `TOKEN`: Your Discord bot token
- `CONNSTR`: MongoDB connection string

### Network Configuration
- **Port 8080**: Web interface (bot status, invite links)
- **Port 12102**: Additional bot services

### User Settings
Users can configure their experience with:
- **bankdm**: Receive DM notifications for balance changes
- **passive**: Enable passive mode (prevents being robbed/receiving transfers)

## 🏗️ Development

### Tech Stack
- **Language**: Java 17
- **Build Tool**: Maven
- **Discord Library**: Javacord 3.8.0
- **Database**: MongoDB
- **Web Framework**: Spark Java
- **HTML Parser**: JSoup
- **JSON Processing**: Gson

### Project Structure
```
src/
├── main/
│   ├── java/com/uniqueapps/unknownbot/
│   │   ├── Main.java                 # Application entry point
│   │   ├── CommandsListener.java     # Message event listener
│   │   ├── ComponentsListener.java   # Component interaction listener
│   │   ├── ModalsListener.java       # Modal interaction listener
│   │   ├── Helper.java               # Utility functions
│   │   ├── commands/                 # Command implementations
│   │   └── objects/                  # Data models
│   └── resources/
│       ├── commands.json             # Command definitions
│       ├── currencies.json           # Currency data
│       ├── index.html                # Web interface
│       └── public/                   # Static web assets
```

### Building from Source
```bash
# Clone the repository
git clone https://github.com/UnknownCoder56/UnknownBot-latest.git
cd UnknownBot-latest

# Build the project
mvn clean compile

# Run tests (if available)
mvn test

# Package the application
mvn package

# Run the bot
java -jar target/unknownbot-1.0-jar-with-dependencies.jar
```

### Adding New Commands
1. Add command definition to `src/main/resources/commands.json`
2. Implement the command in the appropriate command class
3. Register the command in the `CommandsListener`
4. For slash commands, add registration in `SlashCommands.java`

## 🌐 Web Interface

UnknownBot includes a web interface accessible at `http://localhost:8080` that provides:
- Bot status information
- Invite link generation
- Bot statistics
- Developer information

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Community

- **Discord Server**: [Join our community](https://discord.gg/t79ZyuHr5K)
- **Developer**: [UnknownPro 56](https://github.com/UnknownCoder56)
- **Organization**: [Magnetars Co.](https://www.magnetars.cf/)
- **Personal Website**: [UniqueApps](https://uniqueapps.godaddysites.com)

## 🔄 Updates

**This project is archived and no longer under active development.** Please see the successor bot [Maxis](https://github.com/UnknownCoder56/maxis) for ongoing updates and improvements.

## 📞 Support

If you need help or have questions:
1. Join our [Discord server](https://discord.gg/t79ZyuHr5K)
2. Open an issue on GitHub
3. Contact the developer through the community channels

---

<div align="center">
  <p>Made with ❤️ by UnknownPro 56</p>
  <p>Part of Magnetars Co.</p>
</div>