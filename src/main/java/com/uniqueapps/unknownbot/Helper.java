package com.uniqueapps.unknownbot;

import com.mongodb.client.*;
import com.uniqueapps.unknownbot.objects.UserSettings;
import com.uniqueapps.unknownbot.objects.Warn;
import org.bson.Document;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.mongodb.client.model.Filters.eq;

public class Helper {

    // CONSTANTS
    public static final String VERSION = "4.0.0";
    public static final int BASIC_COOLDOWN = 30;
    public static final int DAILY_COOLDOWN = 86400;
    public static final int WEEKLY_COOLDOWN = 604800;
    public static final int MONTHLY_COOLDOWN = 2592000;

    // ENUMS
    public enum RpsResult {
        BOT_WIN, USER_WIN, TIE, ERROR
    }

    // MAPS
    public static Map<String, String> customReplies = new HashMap<>();
    public static Map<Long, Long> balanceMap = new HashMap<>();
    public static Map<Long, Map<Long, Warn>> warnMap = new HashMap<>();

    // ARRAYS
    public static String[] works = {
        "did babysitting for 6 hours and earned",
        "finished a 100-day job and earned",
        "found some money on road and got",
        "sold a modern art picture and earned",
        "caught a robber and was prized with",
        "fixed neighbour's PC and earned",
        "checked his car bonnet and found",
        "won a bet and earned",
        "repaired cars at workshop for a day and earned",
        "won a lucky draw and earned"
    };










    // DB REFRESHERS
    public static void refreshReplies() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    Document doc = new Document()
                            .append("name", "reply")
                            .append("key", customReplies.keySet())
                            .append("val", customReplies.values());
                    if (collection.countDocuments(eq("name", "reply")) > 0) {
                        collection.replaceOne(eq("name", "reply"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated replies!";
                };

                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }

    public static void refreshUserSettings() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    List<Document> settings = new ArrayList<>();
                    for (int i = 0; i < Main.userSettingsMap.size(); i++) {
                        UserSettings userSettings = (UserSettings) Main.userSettingsMap.values().toArray()[i];
                        Document setting = new Document()
                                .append("dm", userSettings.isBankDmEnabled())
                                .append("passive", userSettings.isBankPassiveEnabled());
                        settings.add(setting);
                    }
                    Document doc = new Document()
                            .append("name", "usersettings")
                            .append("key", Main.userSettingsMap.keySet())
                            .append("val", settings);
                    if (collection.countDocuments(eq("name", "usersettings")) > 0) {
                        collection.replaceOne(eq("name", "usersettings"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated all user settings!";
                };

                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }

    public static void refreshBalances() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    Document doc = new Document()
                            .append("name", "balance")
                            .append("key", balanceMap.keySet())
                            .append("val", balanceMap.values());
                    if (collection.countDocuments(eq("name", "balance")) > 0) {
                        collection.replaceOne(eq("name", "balance"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated balances!";
                };

                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }

    public static void refreshWorks() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    List<Date> dates = new ArrayList<>();
                    for (Instant i : Main.userWorkedTimes.values()) {
                        dates.add(Date.from(i));
                    }
                    Document doc = new Document()
                            .append("name", "work")
                            .append("key", Main.userWorkedTimes.keySet())
                            .append("val", dates);
                    if (collection.countDocuments(eq("name", "work")) > 0) {
                        collection.replaceOne(eq("name", "work"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated work times!";
                };

                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }

    public static void refreshRobs() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    List<Date> dates = new ArrayList<>();
                    for (Instant i : Main.userRobbedTimes.values()) {
                        dates.add(Date.from(i));
                    }
                    Document doc = new Document()
                            .append("name", "rob")
                            .append("key", Main.userRobbedTimes.keySet())
                            .append("val", dates);
                    if (collection.countDocuments(eq("name", "rob")) > 0) {
                        collection.replaceOne(eq("name", "rob"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated rob times!";
                };

                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }

    public static void refreshDailies() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    List<Date> dates = new ArrayList<>();
                    for (Instant i : Main.userDailyTimes.values()) {
                        dates.add(Date.from(i));
                    }
                    Document doc = new Document()
                            .append("name", "daily")
                            .append("key", Main.userDailyTimes.keySet())
                            .append("val", dates);
                    if (collection.countDocuments(eq("name", "daily")) > 0) {
                        collection.replaceOne(eq("name", "daily"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated daily times!";
                };

                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }

    public static void refreshWeeklies() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    List<Date> dates = new ArrayList<>();
                    for (Instant i : Main.userWeeklyTimes.values()) {
                        dates.add(Date.from(i));
                    }
                    Document doc = new Document()
                            .append("name", "weekly")
                            .append("key", Main.userWeeklyTimes.keySet())
                            .append("val", dates);
                    if (collection.countDocuments(eq("name", "weekly")) > 0) {
                        collection.replaceOne(eq("name", "weekly"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated weekly times!";
                };

                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }

    public static void refreshMonthlies() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    List<Date> dates = new ArrayList<>();
                    for (Instant i : Main.userMonthlyTimes.values()) {
                        dates.add(Date.from(i));
                    }
                    Document doc = new Document()
                            .append("name", "monthly")
                            .append("key", Main.userMonthlyTimes.keySet())
                            .append("val", dates);
                    if (collection.countDocuments(eq("name", "monthly")) > 0) {
                        collection.replaceOne(eq("name", "monthly"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated monthly times!";
                };

                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }

    public static void refreshWarns() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    List<Document> docs = new ArrayList<>();
                    for (Map<Long, Warn> map : warnMap.values()) {
                        List<Document> warns = new ArrayList<>();
                        for (Warn warn : map.values()) {
                            Document document = new Document()
                                    .append("id", warn.getUserId())
                                    .append("warns", warn.getWarns())
                                    .append("causes", warn.getWarnCauses());
                            warns.add(document);
                        }
                        docs.add(new Document().append("key", map.keySet()).append("val", warns));
                    }
                    Document doc = new Document()
                            .append("name", "warn")
                            .append("key", warnMap.keySet())
                            .append("val", docs);
                    if (collection.countDocuments(eq("name", "warn")) > 0) {
                        collection.replaceOne(eq("name", "warn"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated warns!";
                };

                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }










    // BASICS
    public static Color getRandomColor() {
        int red = new Random().nextInt(256);
        int green = new Random().nextInt(256);
        int blue = new Random().nextInt(256);
        return new Color(red, green, blue);
    }

    public static RpsResult getWinStatus(char choiceBot, char choiceUser) {
        if (choiceBot == 'r') {
            if (choiceUser == 'r') {
                return RpsResult.TIE;
            } else if (choiceUser == 'p') {
                return RpsResult.USER_WIN;
            } else if (choiceUser == 's') {
                return RpsResult.BOT_WIN;
            }
        } else if (choiceBot == 'p') {
            if (choiceUser == 'r') {
                return RpsResult.BOT_WIN;
            } else if (choiceUser == 'p') {
                return RpsResult.TIE;
            } else if (choiceUser == 's') {
                return RpsResult.USER_WIN;
            }
        } else if (choiceBot == 's') {
            if (choiceUser == 'r') {
                return RpsResult.USER_WIN;
            } else if (choiceUser == 'p') {
                return RpsResult.BOT_WIN;
            } else if (choiceUser == 's') {
                return RpsResult.TIE;
            }
        }
        return RpsResult.ERROR;
    }

    public static String getChoiceName(char choice) {
        if (choice == 'r') {
            return "Rock";
        } else if (choice == 'p') {
            return "Paper";
        } else if (choice == 's') {
            return "Scissors";
        }
        return null;
    }

    public static int getRandomInteger(int maxInclusive, int minInclusive) {
        if (maxInclusive == minInclusive) return maxInclusive;
        if (maxInclusive < minInclusive) return 0;
        int val = (int) (Math.random() * (maxInclusive + 1));
        while (val < minInclusive) {
            val = (int) (Math.random() * (maxInclusive + 1));
        }
        return val;
    }

    public static String getRandomWork() {
        return works[(int) (Math.random() * works.length)];
    }










    // CURRENCY RELATED
    public static boolean creditBalance(int creditAmount, User user, TextChannel channel) {
        if (!balanceMap.containsKey(user.getId())) {
            balanceMap.put(user.getId(), 0L);
            refreshBalances();
        }
        long oldBal = balanceMap.get(user.getId());
        if (creditAmount > 0) {
            long newBal = oldBal + creditAmount;
            balanceMap.replace(user.getId(), oldBal, newBal);
            if (Main.userSettingsMap.get(user.getId()).isBankDmEnabled()) {
                try {
                    user.openPrivateChannel().get().sendMessage(new EmbedBuilder()
                            .setTitle("Successfully updated account! Details:-")
                            .addField("Opening Balance", ":coin: " + oldBal)
                            .addField("Deposited", ":coin: " + creditAmount)
                            .addField("Closing Balance", ":coin: " + newBal)
                            .setColor(getRandomColor()));
                    System.out.println("User " + user.getDiscriminatedName() + " A/C updated:-\n" +
                            "Before: " + oldBal + ", Credited: " + creditAmount + ", After: " + newBal);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            refreshBalances();
            return true;
        } else {
            channel.sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Value should be more than 0.")
                    .setColor(getRandomColor()));
        }
        return false;
    }

    public static boolean debitBalance(int debitAmount, User user, TextChannel channel) {
        if (!balanceMap.containsKey(user.getId())) {
            balanceMap.put(user.getId(), 0L);
            refreshBalances();
        }
        long oldBal = balanceMap.get(user.getId());
        if (debitAmount > 0) {
            if (debitAmount <= oldBal) {
                long newBal = oldBal - debitAmount;
                balanceMap.replace(user.getId(), oldBal, newBal);
                if (Main.userSettingsMap.get(user.getId()).isBankDmEnabled()) {
                    try {
                        user.openPrivateChannel().get().sendMessage(new EmbedBuilder()
                                .setTitle("Successfully updated account! Details:-")
                                .addField("Opening Balance", ":coin: " + oldBal)
                                .addField("Withdrawn", ":coin: " + debitAmount)
                                .addField("Closing Balance", ":coin: " + newBal)
                                .setColor(getRandomColor()));
                        System.out.println("User " + user.getDiscriminatedName() + " A/C updated:-\n" +
                                "Before: " + oldBal + ", Debited: " + debitAmount + ", After: " + newBal);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                refreshBalances();
                return true;
            } else if (oldBal == 0) {
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You can't withdraw, because you have no money!")
                        .setColor(getRandomColor()));
            } else {
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You can't withdraw more than you have in your bank!")
                        .setColor(getRandomColor()));
            }
        } else {
            channel.sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Value should be more than 0.")
                    .setColor(getRandomColor()));
        }
        return false;
    }
}
