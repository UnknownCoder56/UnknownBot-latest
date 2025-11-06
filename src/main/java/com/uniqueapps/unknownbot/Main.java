package com.uniqueapps.unknownbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.bson.Document;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;
import com.uniqueapps.unknownbot.commands.AsyncCommands;
import com.uniqueapps.unknownbot.commands.SlashCommands;
import com.uniqueapps.unknownbot.objects.Shop;
import com.uniqueapps.unknownbot.objects.UserSettings;
import com.uniqueapps.unknownbot.objects.Warn;

import spark.Spark;

public class Main {

    public static DiscordApi api;
    public static Map<Long, Instant> userWorkedTimes = new HashMap<>();
    public static Map<Long, Instant> userRobbedTimes = new HashMap<>();
    public static Map<Long, Instant> userDailyTimes = new HashMap<>();
    public static Map<Long, Instant> userWeeklyTimes = new HashMap<>();
    public static Map<Long, Instant> userMonthlyTimes = new HashMap<>();
    public static Map<Long, UserSettings> userSettingsMap = new HashMap<>();
    public static String settings = System.getenv("CONNSTR");

    public static void main(String[] args) {
        FallbackLoggerConfiguration.setDebug(true);
        try (var files = Files.list(Path.of("./"))) {
            files.filter(path -> !path.toFile().getName().endsWith(".jar") && !path.toFile().getName().equals("start.sh")).forEach(path -> {
                try {
                    System.out.println("Deleting temporary file: " + path.getFileName());
                    Files.delete(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        initData();
        Shop.initShop();
        AsyncCommands.Admes.initServer();

        api = new DiscordApiBuilder()
                .setToken(System.getenv("TOKEN"))
                .setIntents(Intent.DIRECT_MESSAGES, Intent.GUILD_BANS, Intent.GUILD_MEMBERS, Intent.GUILDS,
                        Intent.DIRECT_MESSAGE_REACTIONS, Intent.DIRECT_MESSAGE_TYPING, Intent.GUILD_MESSAGES,
                        Intent.GUILD_PRESENCES, Intent.MESSAGE_CONTENT)
                .login().join();

        org.jsoup.nodes.Document doc = prepareBotSite();

        String portEnv = System.getenv("PORT");
        if (portEnv != null && !Objects.equals(portEnv, "")) {
            if (!portEnv.equals("0")) {
                Spark.port(Integer.parseInt(portEnv));
            }
        } else {
            Spark.port(8080);
        }
        Spark.staticFileLocation("/public/");

        Spark.get("/", (req, res) -> {
            res.type("text/html");
            return doc.html();
        });

        System.out.println("UnknownBot listening on http://localhost:" + Spark.port() + "/");

        initUserSettings(api.getServers());

        api.addMessageCreateListener(new CommandsListener());
        api.addSlashCommandCreateListener(new SlashCommands());
        api.addMessageComponentCreateListener(new ComponentsListener());
        api.addModalSubmitListener(new ModalsListener());
        api.updateActivity(ActivityType.WATCHING, " >help");
        System.out.println("Invite link for UnknownBot: " + api.createBotInvite(Permissions.fromBitmask(PermissionType.ADMINISTRATOR.getValue())));
    }

    public static void initData() {
        try (MongoClient client = MongoClients.create(settings); ClientSession session = client.startSession()) {
            TransactionBody<String> txnBody = () -> {
                MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                for (Document doc : collection.find()) {
                    if (doc.get("name").equals("reply")) {
                        Helper.customReplies = new HashMap<>();
                        var keys = doc.getList("key", String.class);
                        var vals = doc.getList("val", String.class);
                        for (int i = 0; i < keys.size(); i++) {
                            Helper.customReplies.put(keys.get(i), vals.get(i));
                        }
                    } else if (doc.get("name").equals("warn")) {
                        Helper.warnMap = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Document.class);
                        for (int i = 0; i < keys.size(); i++) {
                            var doc1 = vals.get(i);
                            var keys1 = doc1.getList("key", Long.class);
                            var vals1 = doc1.getList("val", Document.class);
                            ArrayList<Warn> warns = new ArrayList<>();
                            for (int z = 0; z < keys1.size(); z++) {
                                int warnsInt = vals1.get(z).getInteger("warns");
                                long id = vals1.get(z).getLong("id");
                                var causes = vals1.get(z).getList("causes", String.class);
                                Warn warn = new Warn();
                                warn.setUserId(id);
                                warn.setWarns(warnsInt);
                                warn.setWarnCauses(causes);
                                warns.add(warn);
                            }
                            Map<Long, Warn> map = new HashMap<>();
                            for (int x = 0; x < keys.size(); x++) {
                                map.put(keys1.get(x), warns.get(x));
                            }
                            Helper.warnMap.put(keys.get(i), map);
                        }
                    } else if (doc.get("name").equals("balance")) {
                        Helper.balanceMap = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Long.class);
                        for (int i = 0; i < keys.size(); i++) {
                            Helper.balanceMap.put(keys.get(i), vals.get(i));
                        }
                    } else if (doc.get("name").equals("item")) {
                        Shop.ownedItems = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Document.class);
                        for (int i = 0; i < keys.size(); i++) {
                            var doc1 = vals.get(i);
                            var keys1 = doc1.getList("key", String.class);
                            var vals1 = doc1.getList("val", Integer.class);
                            Map<String, Integer> map = new HashMap<>();
                            for (int x = 0; x < keys1.size(); x++) {
                                map.put(keys1.get(x), vals1.get(x));
                            }
                            Shop.ownedItems.put(keys.get(i), map);
                        }
                    } else if (doc.get("name").equals("work")) {
                        userWorkedTimes = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Date.class);
                        for (int i = 0; i < keys.size(); i++) {
                            userWorkedTimes.put(keys.get(i), vals.get(i).toInstant());
                        }
                    } else if (doc.get("name").equals("rob")) {
                        userRobbedTimes = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Date.class);
                        for (int i = 0; i < keys.size(); i++) {
                            userRobbedTimes.put(keys.get(i), vals.get(i).toInstant());
                        }
                    } else if (doc.get("name").equals("daily")) {
                        userDailyTimes = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Date.class);
                        for (int i = 0; i < keys.size(); i++) {
                            userDailyTimes.put(keys.get(i), vals.get(i).toInstant());
                        }
                    } else if (doc.get("name").equals("weekly")) {
                        userWeeklyTimes = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Date.class);
                        for (int i = 0; i < keys.size(); i++) {
                            userWeeklyTimes.put(keys.get(i), vals.get(i).toInstant());
                        }
                    } else if (doc.get("name").equals("monthly")) {
                        userMonthlyTimes = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Date.class);
                        for (int i = 0; i < keys.size(); i++) {
                            userMonthlyTimes.put(keys.get(i), vals.get(i).toInstant());
                        }
                    } else {
                        System.out.println("Unknown document '" + (doc.get("name") != null ? doc.get("name") : "") + "' found! Skipping");
                    }
                }
                return "Retrieved all data.";
            };

            System.out.println(session.withTransaction(txnBody));
        }
    }

    public static void initUserSettings(Collection<Server> servers) {
        try (MongoClient client = MongoClients.create(settings); ClientSession session = client.startSession()) {
            TransactionBody<String> txnBody = () -> {
                MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                for (Document doc : collection.find()) {
                    if (doc.get("name").equals("usersettings")) {
                        userSettingsMap = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Document.class);
                        for (int i = 0; i < keys.size(); i++) {
                            var dm = vals.get(i).getBoolean("dm");
                            var passive = vals.get(i).getBoolean("passive");
                            UserSettings userSettings = new UserSettings(dm, passive);
                            userSettingsMap.put(keys.get(i), userSettings);
                        }
                    }
                }
                for (Server server : servers) {
                    for (User user : server.getMembers()) {
                        if (!user.isBot()) {
                            if (!userSettingsMap.containsKey(user.getId())) {
                                userSettingsMap.put(user.getId(), new UserSettings());
                            }
                        }
                    }
                }
                return "Retrieved all user settings.";
            };

            System.out.println(session.withTransaction(txnBody));
        }
    }

    private static org.jsoup.nodes.Document prepareBotSite() {
        org.jsoup.nodes.Document doc = Jsoup.parse(Main.getBotSiteHtmlCode());

        // Update invite link
        Element inviteLinkElement = doc.select("a").first();
        if (inviteLinkElement != null) {
            inviteLinkElement.attr("href", api.createBotInvite(Permissions.fromBitmask(PermissionType.ADMINISTRATOR.getValue())));
        } else {
            throw new NoSuchElementException("Element \"a\" not found!");
        }

        return doc;
    }

    private static String getBotSiteHtmlCode() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("index.html"))))) {    
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return content.toString();
    }
}
