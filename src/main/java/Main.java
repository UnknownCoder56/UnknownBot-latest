import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import spark.Spark;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static DiscordApi api;
    public static void main(String[] args) {

        initData();
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
            return "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<title>UnknownBot</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "<h1 align=\"center\" style=\"font-family:Montserrat\">UnknownBot is online!</h1>\n" +
                    "<p style=\"font-family:Montserrat\">Invite it to your server: " + api.createBotInvite() + "</p>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>";
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

        try {
            replyArrayFile.createNewFile();
            warnsMapFile.createNewFile();
            balanceMapFile.createNewFile();
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
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(balanceMapFile))) {
            CurrencyCommands.balanceMap = (Map<Long, Long>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (BasicCommands.customReplies == null) BasicCommands.customReplies = new HashMap<>();
        if (ModCommands.warnMap == null) ModCommands.warnMap = new HashMap<>();
        if (CurrencyCommands.balanceMap == null) CurrencyCommands.balanceMap = new HashMap<>();
    }
}
