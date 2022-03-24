package com.uniqueapps.UnknownBot;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.uniqueapps.UnknownBot.commands.BasicCommands;
import com.uniqueapps.UnknownBot.commands.CurrencyCommands;
import com.uniqueapps.UnknownBot.commands.ModCommands;
import com.uniqueapps.UnknownBot.objects.Shop;
import com.uniqueapps.UnknownBot.objects.Warn;

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

        Main app = new Main();
        System.out.print("\033\143");
        Spark.port(Integer.parseInt(System.getenv("PORT")));
        
        /*
        Spark.get("/favicon.png", (req, res) -> {
            res.type("image/x-icon");
            return app.getResourceImage("favicon.png");
        });
        */
        Spark.staticFileLocation("/public");

        Spark.get("/", (req, res) -> {
            res.type("text/html");
            return app.getResourceText("index.html");
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

        File work = new File("work.data");
        File rob = new File("rob.data");
        File daily = new File("daily.data");

        try {
            replyArrayFile.createNewFile();
            warnsMapFile.createNewFile();
            balanceMapFile.createNewFile();
            shopFile.createNewFile();

            work.createNewFile();
            rob.createNewFile();
            daily.createNewFile();
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

        //
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(work))) {
            userWorkedTimes = (Map<Long, Instant>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(rob))) {
            userRobbedTimes = (Map<Long, Instant>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(daily))) {
            userDailyTimes = (Map<Long, Instant>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        //

        if (BasicCommands.customReplies == null)
            BasicCommands.customReplies = new HashMap<>();
        if (ModCommands.warnMap == null)
            ModCommands.warnMap = new HashMap<>();
        if (CurrencyCommands.balanceMap == null)
            CurrencyCommands.balanceMap = new HashMap<>();
        if (Shop.ownedItems == null)
            Shop.ownedItems = new HashMap<>();

        if (userWorkedTimes == null)
            userWorkedTimes = new HashMap<>();
        if (userRobbedTimes == null)
            userRobbedTimes = new HashMap<>();
        if (userDailyTimes == null)
            userDailyTimes = new HashMap<>();
    }

    private String getResourceText(String resourceName) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName)));
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

    /*
    private File getResourceImage(String resourceName) {
        try {
            BufferedImage img = ImageIO.read(getClass().getClassLoader().getResourceAsStream(resourceName));
            ImageIO.write(img, "png", new File("favicon.pg"));
            return new File("favicon.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    */
}
