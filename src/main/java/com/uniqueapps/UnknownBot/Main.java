package com.uniqueapps.UnknownBot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;
import com.uniqueapps.UnknownBot.commands.BasicCommands;
import com.uniqueapps.UnknownBot.commands.CurrencyCommands;
import com.uniqueapps.UnknownBot.objects.Shop;

import org.bson.Document;
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
    public static MongoClientSettings settings;

    public static void main(String[] args) {
        
        ConnectionString connectionString = new ConnectionString(System.getenv("CONNSTR"));
        settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
            
        initData();
        Shop.initShop();
        api = new DiscordApiBuilder()
                .setToken(System.getenv("TOKEN"))
                .setIntents(Intent.DIRECT_MESSAGES, Intent.GUILD_BANS, Intent.GUILD_MEMBERS, Intent.GUILDS,
                        Intent.DIRECT_MESSAGE_REACTIONS, Intent.DIRECT_MESSAGE_TYPING, Intent.GUILD_MESSAGES,
                        Intent.GUILD_PRESENCES)
                .login().join();

        Main app = new Main();
        System.out.print("\033\143");
        Spark.port(Integer.parseInt(System.getenv("PORT")));
        Spark.staticFileLocation("/");

        Spark.get("/", (req, res) -> {
            res.type("text/html");
            return app.getResourceText("index.html");
        });

        System.out.println("UnknownBot listening on http://localhost:" + Spark.port() + "/");

        api.addListener(new CommandsListener());
        api.updateActivity(ActivityType.WATCHING, " >help | UniqueApps Co.");
        System.out.println("Invite link for UnknownBot: " + api.createBotInvite());
    }

    public static void initData() {
        try (MongoClient client = MongoClients.create(settings); ClientSession session = client.startSession()) {
            TransactionBody<String> txnBody = () -> {
                MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                for (Document doc : collection.find()) {
                    if (doc.get("name").equals("reply")) {
                        BasicCommands.customReplies = new HashMap<>();
                        var keys = doc.getList("key", String.class);
                        var vals = doc.getList("val", String.class);
                        for (int i = 0; i < keys.size(); i++) {
                            BasicCommands.customReplies.put(keys.get(i), vals.get(i));
                        }
                    } else if (doc.get("name").equals("warn")) {
                        //TODO implement warn retrieval system
                    } else if (doc.get("name").equals("balance")) {
                        CurrencyCommands.balanceMap = new HashMap<>();
                        var keys = doc.getList("key", Long.class);
                        var vals = doc.getList("val", Long.class);
                        for (int i = 0; i < keys.size(); i++) {
                            CurrencyCommands.balanceMap.put(keys.get(i), vals.get(i));
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
                            for (int x = 0; x < keys.size(); x++) {
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
                    } else {
                        System.out.println("Unknown document found! Skipping");
                    }
                }
                return "Retrieved all data.";
            };

            System.out.println(session.withTransaction(txnBody));
        }
    }

    private String getResourceText(String resourceName) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
