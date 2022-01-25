import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
            return getResourceContents("index.html");
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

    private static String getResourceContents(String resourceName) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Main.class.getClass().getClassLoader().getResourceAsStream(resourceName)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
