import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;

import spark.Spark;

public class Main {

    public static DiscordApi api;
    public static Map<Long, Instant> userWorkedTimes = new HashMap<>();
    public static Map<Long, Instant> userRobbedTimes = new HashMap<>();
    public static Map<Long, Instant> userDailyTimes = new HashMap<>();
    public static String website = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'>\n" +
            "<style>\n" +
            "body {\n" +
            "    font-family: 'Montserrat';font-size: 20px;\n" +
            "}\n" +
            "</style>\n" +
            "<title>UnknownBot</title>\n" +
            "</head>\n" +
            "<body style=\"background-color:powderblue\">\n" +
            "<h1 align=center style=\"color:red\">Welcome to UnknownBot's site!</h1>\n" +
            "<p align=center style=\"color:darkgreen\">Bot status: Online (OK)</p>\n" +
            "<p align=center style=\"color:darkgreen\">Bot version: 3.2.0</p>\n" +
            "<p align=center style=\"color:darkgreen\">Bot developer: UnknownPro 56</p>\n" +
            "<p align=center>\n" +
            "<a href=\"https://discord.com/oauth2/authorize?client_id=891518158790361138&scope=bot&permissions=0\" align=center style=\"color:blue\">Invite it to your server!</a>\n"
            +
            "</p>\n" +
            "<p align=center>\n" +
            "<a href=\"https://github.com/UnknownCoder56\" align=center style=\"color:blue\">My GitHub profile</a>\n" +
            "</p>\n" +
            "<p align=center>\n" +
            "<a href=\"https://discord.gg/t79ZyuHr5K\" align=center style=\"color:blue\">My Discord Server</a?\n" +
            "</p>\n" +
            "</body>\n" +
            "</html>";

    public static void main(String[] args) {

        initData();
        Shop.initShop();
        api = new DiscordApiBuilder()
                .setToken(System.getenv("TOKEN"))
                .setIntents(Intent.DIRECT_MESSAGES, Intent.GUILD_BANS, Intent.GUILD_MEMBERS, Intent.GUILDS,
                        Intent.DIRECT_MESSAGE_REACTIONS, Intent.DIRECT_MESSAGE_TYPING, Intent.GUILD_MESSAGES,
                        Intent.GUILD_PRESENCES)
                .login().join();

        System.out.print("\033\143");
        Spark.port(6565);
        Spark.get("/", (req, res) -> {
            res.type("text/html");
            return website;
        });
        System.out.println("UnknownBot listening on http://localhost:" + Spark.port() + "/");

        api.addListener(new CommandsListener());
        api.updateActivity(ActivityType.WATCHING, " >help | UniqueApps Co.");
        System.out.println("Invite link for UnknownBot: " + api.createBotInvite());
    }

    @SuppressWarnings("unchecked")
    public static void initData() {
        File replyArrayFile = new File("replyArray.data");
        File warnsMapFile = new File("warnsMap.data");
        File balanceMapFile = new File("balanceMap.data");
        File shopFile = new File("shopFile.data");

        try {
            replyArrayFile.createNewFile();
            warnsMapFile.createNewFile();
            balanceMapFile.createNewFile();
            shopFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(replyArrayFile))) {
            BasicCommands.customReplies = (Map<String, String>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(warnsMapFile))) {
            ModCommands.warnMap = (Map<Long, Map<Long, Warn>>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(balanceMapFile))) {
            CurrencyCommands.balanceMap = (Map<Long, Long>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(shopFile))) {
            Shop.ownedItems = (Map<Long, Map<String, Integer>>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        if (BasicCommands.customReplies == null)
            BasicCommands.customReplies = new HashMap<>();
        if (ModCommands.warnMap == null)
            ModCommands.warnMap = new HashMap<>();
        if (CurrencyCommands.balanceMap == null)
            CurrencyCommands.balanceMap = new HashMap<>();
        if (Shop.ownedItems == null)
            Shop.ownedItems = new HashMap<>();
    }
}
