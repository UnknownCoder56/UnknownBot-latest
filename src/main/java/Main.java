import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;

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
                .setToken(args[0])
                .setIntents(Intent.DIRECT_MESSAGES, Intent.GUILD_BANS, Intent.GUILD_MEMBERS, Intent.GUILDS,
                        Intent.DIRECT_MESSAGE_REACTIONS, Intent.DIRECT_MESSAGE_TYPING, Intent.GUILD_MESSAGES,
                        Intent.GUILD_PRESENCES)
                .login().join();

        api.addListener(new CommandsListener());
        api.updateActivity(ActivityType.WATCHING, " >help | UniqueApps Co.");
        System.out.println(api.createBotInvite());
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
