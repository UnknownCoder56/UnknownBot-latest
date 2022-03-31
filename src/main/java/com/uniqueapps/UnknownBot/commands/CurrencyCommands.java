package com.uniqueapps.UnknownBot.commands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;
import com.mongodb.client.model.Filters;
import com.uniqueapps.UnknownBot.Main;
import com.uniqueapps.UnknownBot.objects.Shop;
import com.uniqueapps.UnknownBot.objects.SortByBalance;

import org.bson.Document;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class CurrencyCommands {

    public static Map<Long, Long> balanceMap = new HashMap<>();
    public static String[] works = { "did babysitting for 6 hours and earned", "finished a 100-day job and earned",
            "found some money on road and got", "sold a modern art picture and earned",
            "caught a robber and was prized with",
            "fixed neighbour's PC and earned", "checked his car bonnet and found", "won a bet and earned",
            "repaired cars at workshop for a day and earned", "won a lucky draw and earned" };
    static int coolDown = 30;
    static int dailyCoolDown = 86400;

    public static void balance(MessageCreateEvent event) {
        if (event.getMessage().getMentionedUsers().size() > 0) {
            if (event.getMessageAuthor().asUser().isPresent()) {
                User balUser = event.getMessage().getMentionedUsers().get(0);
                if (!balanceMap.containsKey(balUser.getId())) {
                    balanceMap.put(balUser.getId(), 0L);
                    refreshBalances();
                }
                long bal = balanceMap.get(balUser.getId());
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle(balUser.getDiscriminatedName() + "'s balance:-")
                        .addField("Bank", ":coin: " + bal)
                        .setColor(BasicCommands.getRandomColor()));
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Only users can access this command! Maybe you are a bot.")
                        .setColor(BasicCommands.getRandomColor()));
            }
        } else {
            if (event.getMessageAuthor().asUser().isPresent()) {
                if (!balanceMap.containsKey(event.getMessageAuthor().asUser().get().getId())) {
                    balanceMap.put(event.getMessageAuthor().asUser().get().getId(), 0L);
                    refreshBalances();
                }
                long bal = balanceMap.get(event.getMessageAuthor().asUser().get().getId());
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle(event.getMessageAuthor().getDisplayName() + "'s balance:-")
                        .addField("Bank", ":coin: " + bal)
                        .setColor(BasicCommands.getRandomColor()));
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Only users can access this command! Maybe you are a bot.")
                        .setColor(BasicCommands.getRandomColor()));
            }
        }
    }

    public static void daily(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            Long userId = event.getMessageAuthor().asUser().get().getId();
            if (Main.userDailyTimes.containsKey(userId)) {
                if (Duration.between(Main.userDailyTimes.get(userId), event.getMessage().getCreationTimestamp())
                        .toSeconds() >= (dailyCoolDown)) {
                    Main.userDailyTimes.put(userId, event.getMessage().getCreationTimestamp());
                    int earn = 5000;
                    if (CurrencyCommands.creditBalance(earn, event.getMessageAuthor().asUser().get(), event)) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle(event.getMessageAuthor().getDisplayName() + "'s Daily Earnings")
                                .setDescription(event.getMessageAuthor().getDisplayName()
                                        + " got their daily earnings: :coin: " + earn)
                                .setColor(BasicCommands.getRandomColor()));
                    }
                    refreshDailies();
                } else {
                    int leftSeconds = (int) (dailyCoolDown - Duration
                            .between(Main.userDailyTimes.get(userId), event.getMessage().getCreationTimestamp())
                            .toSeconds());
                    int seconds = leftSeconds;
                    int p1 = seconds % 60;
                    int p2 = seconds / 60;
                    int p3 = p2 % 60;
                    p2 = p2 / 60;
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You are currently on cooldown! You may use this command again after " + p2
                                    + " hours, " + p3 + " minutes and " + p1 + " seconds."));
                    System.out.println("Cooldown block faced!");
                }
            } else {
                Main.userDailyTimes.put(userId, event.getMessage().getCreationTimestamp());
                int earn = 5000;
                if (CurrencyCommands.creditBalance(earn, event.getMessageAuthor().asUser().get(), event)) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle(event.getMessageAuthor().getDisplayName() + "'s Daily Earnings")
                            .setDescription(event.getMessageAuthor().getDisplayName()
                                    + " got their daily earnings: :coin: " + earn)
                            .setColor(BasicCommands.getRandomColor()));
                }
                refreshDailies();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You are not a user! Maybe you are a bot.")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void work(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            Long userId = event.getMessageAuthor().asUser().get().getId();
            if (Main.userWorkedTimes.containsKey(userId)) {
                if (Duration.between(Main.userWorkedTimes.get(userId), event.getMessage().getCreationTimestamp())
                        .toSeconds() >= coolDown) {
                    Main.userWorkedTimes.put(userId, event.getMessage().getCreationTimestamp());
                    String work = CurrencyCommands.getRandomWork();
                    int earn = CurrencyCommands.getRandomInteger(500, 100);
                    if (CurrencyCommands.creditBalance(earn, event.getMessageAuthor().asUser().get(), event)) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle(event.getMessageAuthor().getDisplayName() + " Worked")
                                .setDescription(event.getMessageAuthor().getDisplayName() + " " + work +
                                        " :coin: " + earn)
                                .setColor(BasicCommands.getRandomColor()));
                    }
                    refreshWorks();
                } else {
                    int left = (int) (coolDown - Duration
                            .between(Main.userWorkedTimes.get(userId), event.getMessage().getCreationTimestamp())
                            .toSeconds());
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You are currently on cooldown! You may use this command again after "
                                    + left + " seconds."));
                    System.out.println("Cooldown block faced!");
                }
            } else {
                Main.userWorkedTimes.put(userId, event.getMessage().getCreationTimestamp());
                String work = CurrencyCommands.getRandomWork();
                int earn = CurrencyCommands.getRandomInteger(500, 100);
                if (CurrencyCommands.creditBalance(earn, event.getMessageAuthor().asUser().get(), event)) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle(event.getMessageAuthor().getDisplayName() + " Worked")
                            .setDescription(event.getMessageAuthor().getDisplayName() + " " + work +
                                    " :coin: " + earn)
                            .setColor(BasicCommands.getRandomColor()));
                }
                refreshWorks();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You are not a user! Maybe you are a bot.")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void rob(MessageCreateEvent event) {
        Long commanderId = event.getMessageAuthor().asUser().get().getId();
        if (Main.userRobbedTimes.containsKey(commanderId)) {
            if (Duration.between(Main.userRobbedTimes.get(commanderId), event.getMessage().getCreationTimestamp())
                    .toSeconds() >= coolDown) {
                Main.userRobbedTimes.put(commanderId, event.getMessage().getCreationTimestamp());
                refreshRobs();
                User robUser = event.getMessage().getMentionedUsers().get(0);
                if (event.getServer().isPresent()) {
                    if (event.getMessageAuthor().asUser().isPresent()) {
                        if (!robUser.isBot()) {
                            int robValue = getRandomInteger(5000, 1000);
                            if (balanceMap.containsKey(robUser.getId())) {
                                if (robUser.getId() != event.getMessageAuthor().asUser().get().getId()) {
                                    if (balanceMap.get(robUser.getId()) > 1000) {
                                        while (balanceMap.get(robUser.getId()) < robValue) {
                                            robValue = getRandomInteger(5000, 1000);
                                        }
                                        if (debitBalance(robValue, robUser, event)) {
                                            if (creditBalance(robValue, event.getMessageAuthor().asUser().get(),
                                                    event)) {
                                                event.getChannel().sendMessage(new EmbedBuilder()
                                                        .setTitle("Success!")
                                                        .setDescription(event.getMessageAuthor().getDisplayName()
                                                                + " successfully robbed "
                                                                + robUser.getDisplayName(event.getServer().get())
                                                                + ", and earned :coin: " + robValue + ".")
                                                        .setColor(BasicCommands.getRandomColor()));
                                                refreshBalances();
                                            }
                                        }
                                    } else {
                                        event.getChannel().sendMessage(new EmbedBuilder()
                                                .setTitle("Error!")
                                                .setDescription("That user does not have enough money to rob!")
                                                .setColor(BasicCommands.getRandomColor()));
                                    }
                                } else {
                                    event.getChannel().sendMessage(new EmbedBuilder()
                                            .setTitle("Error!")
                                            .setDescription("You can't rob yourself!")
                                            .setColor(BasicCommands.getRandomColor()));
                                }
                            } else {
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Error!")
                                        .setDescription("That user does not have enough money to rob!")
                                        .setColor(BasicCommands.getRandomColor()));
                            }
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Error!")
                                    .setDescription("You can't rob bots!")
                                    .setColor(BasicCommands.getRandomColor()));
                        }
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("You are not a user! Maybe you are a bot.")
                                .setColor(BasicCommands.getRandomColor()));
                    }
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("This command only works in servers!")
                            .setColor(BasicCommands.getRandomColor()));
                }
            } else {
                int left = (int) (coolDown - Duration
                        .between(Main.userRobbedTimes.get(commanderId), event.getMessage().getCreationTimestamp())
                        .toSeconds());
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You are currently on cooldown! You may use this command again after " + left
                                + " seconds."));
                System.out.println("Cooldown block faced!");
            }
        } else {
            Main.userRobbedTimes.put(commanderId, event.getMessage().getCreationTimestamp());
            refreshRobs();
            User robUser = event.getMessage().getMentionedUsers().get(0);
            if (event.getServer().isPresent()) {
                if (event.getMessageAuthor().asUser().isPresent()) {
                    if (!robUser.isBot()) {
                        int robValue = getRandomInteger(5000, 1000);
                        if (balanceMap.containsKey(robUser.getId())) {
                            if (robUser.getId() != event.getMessageAuthor().asUser().get().getId()) {
                                if (balanceMap.get(robUser.getId()) > 1000) {
                                    while (balanceMap.get(robUser.getId()) < robValue) {
                                        robValue = getRandomInteger(5000, 1000);
                                    }
                                    if (debitBalance(robValue, robUser, event)) {
                                        if (creditBalance(robValue, event.getMessageAuthor().asUser().get(), event)) {
                                            event.getChannel().sendMessage(new EmbedBuilder()
                                                    .setTitle("Success!")
                                                    .setDescription(event.getMessageAuthor().getDisplayName()
                                                            + " successfully robbed "
                                                            + robUser.getDisplayName(event.getServer().get())
                                                            + ", and earned :coin: " + robValue + ".")
                                                    .setColor(BasicCommands.getRandomColor()));
                                            refreshBalances();
                                        }
                                    }
                                } else {
                                    event.getChannel().sendMessage(new EmbedBuilder()
                                            .setTitle("Error!")
                                            .setDescription("That user does not have enough money to rob!")
                                            .setColor(BasicCommands.getRandomColor()));
                                }
                            } else {
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Error!")
                                        .setDescription("You can't rob yourself!")
                                        .setColor(BasicCommands.getRandomColor()));
                            }
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Error!")
                                    .setDescription("That user does not have enough money to rob!")
                                    .setColor(BasicCommands.getRandomColor()));
                        }
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("You can't rob bots!")
                                .setColor(BasicCommands.getRandomColor()));
                    }
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You are not a user! Maybe you are a bot.")
                            .setColor(BasicCommands.getRandomColor()));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("This command only works in servers!")
                        .setColor(BasicCommands.getRandomColor()));
            }
        }
    }

    public static void give(MessageCreateEvent event) {
        User giveUser = event.getMessage().getMentionedUsers().get(0);
        String[] args = event.getMessage().getContent().split(" ");
        int giveValue = Integer.parseInt(args[1]);
        if (event.getServer().isPresent()) {
            if (event.getMessageAuthor().asUser().isPresent()) {
                if (!giveUser.isBot()) {
                    if (!(giveValue > balanceMap.get(event.getMessageAuthor().asUser().get().getId()))) {
                        if (giveUser.getId() != event.getMessageAuthor().asUser().get().getId()) {
                            if (!balanceMap.containsKey(giveUser.getId())) {
                                balanceMap.put(giveUser.getId(), 0L);
                                refreshBalances();
                            }
                            if (debitBalance(giveValue, event.getMessageAuthor().asUser().get(), event)) {
                                if (creditBalance(giveValue, giveUser, event)) {
                                    event.getChannel().sendMessage(new EmbedBuilder()
                                            .setTitle("Success!")
                                            .setDescription(
                                                    event.getMessageAuthor().getDisplayName() + " successfully gave "
                                                            + giveUser.getDisplayName(event.getServer().get())
                                                            + " :coin: " + giveValue + ".")
                                            .setColor(BasicCommands.getRandomColor()));
                                    refreshBalances();
                                }
                            }
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Error!")
                                    .setDescription("You can't give money to yourself!")
                                    .setColor(BasicCommands.getRandomColor()));
                        }
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("You can't give more money than you have in your account!")
                                .setColor(BasicCommands.getRandomColor()));
                    }
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You can't give money to bots!")
                            .setColor(BasicCommands.getRandomColor()));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You are not a user! Maybe you are a bot.")
                        .setColor(BasicCommands.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("This command only works in servers!")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void leaderboard(MessageCreateEvent event) {
        if (event.getServer().isPresent()) {
            System.out.println("Cache ok?: " + event.getServer().get().hasAllMembersInCache());
            Collection<User> users = event.getServer().get().getMembers();
            ArrayList<User> arrangedUsers = new ArrayList<>();
            for (User user : users) {
                if (!user.isBot()) {
                    if (balanceMap.containsKey(user.getId())) {
                        arrangedUsers.add(arrangedUsers.size(), user);
                    }
                }
            }
            SortByBalance sortByBalance = new SortByBalance(balanceMap);
            arrangedUsers.sort(Collections.reverseOrder(sortByBalance));
            arrangedUsers.removeIf(user -> arrangedUsers.indexOf(user) > 5);
            System.out.println(arrangedUsers);
            StringBuilder formattedTopUsers = new StringBuilder();
            int win = 0;
            for (User user : arrangedUsers) {
                win++;
                formattedTopUsers
                        .append(win)
                        .append(") ")
                        .append(user.getDisplayName(event.getServer().get())).append(" (:coin: ")
                        .append(balanceMap.get(user.getId())).append(")")
                        .append("\n");
            }
            if (arrangedUsers.size() > 0) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Top " + arrangedUsers.size() + " richest user(s) in "
                                + event.getServer().get().getName() + ":-")
                        .setDescription(formattedTopUsers.toString())
                        .setColor(BasicCommands.getRandomColor()));
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("No one has more than :coin: 0 in this server!")
                        .setColor(BasicCommands.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("This command only works in servers!")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void globalLeaderboard(MessageCreateEvent event) {
        ArrayList<User> users = new ArrayList<>();
        for (Long id : balanceMap.keySet()) {
            try {
                if (!Main.api.getUserById(id).get().isBot()) {
                    users.add(Main.api.getUserById(id).get());
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        ArrayList<User> arrangedUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.isBot()) {
                if (balanceMap.containsKey(user.getId())) {
                    arrangedUsers.add(arrangedUsers.size(), user);
                }
            }
        }
        SortByBalance sortByBalance = new SortByBalance(balanceMap);
        arrangedUsers.sort(Collections.reverseOrder(sortByBalance));
        arrangedUsers.removeIf(user -> arrangedUsers.indexOf(user) > 5);
        System.out.println(arrangedUsers);
        StringBuilder formattedTopUsers = new StringBuilder();
        int win = 0;
        for (User user : arrangedUsers) {
            win++;
            formattedTopUsers
                    .append(win)
                    .append(") ")
                    .append(user.getDiscriminatedName()).append(" (:coin: ").append(balanceMap.get(user.getId()))
                    .append(")")
                    .append("\n");
        }
        if (arrangedUsers.size() > 0) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Top " + arrangedUsers.size() + " richest user(s) of UnknownBot:-")
                    .setDescription(formattedTopUsers.toString())
                    .setColor(BasicCommands.getRandomColor()));
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("No one has more than :coin: 0 in our database!")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void inv(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            Long userId = event.getMessageAuthor().asUser().get().getId();
            StringBuilder itemText = new StringBuilder();
            if (Shop.ownedItems.containsKey(userId)) {
                Map<String, Integer> userItems = Shop.ownedItems.get(userId);
                int index = 0;
                for (String name : userItems.keySet()) {
                    if (userItems.get(name) != 0) {
                        index++;
                        itemText.append(index + ") " + name + " (Count: " + userItems.get(name) + ")\n");
                    }
                }
                if (itemText.toString().isEmpty()) {
                    itemText.append("There are no items in your inventory!");
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Items in your inventory:-")
                        .setDescription(itemText.toString()));
            } else {
                itemText.append("There are no items in your inventory!");
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Items in your inventory:-")
                        .setDescription(itemText.toString()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You are not a user! Maybe you are a bot."));
        }
    }

    public static boolean creditBalance(int creditAmount, User user, MessageCreateEvent event) {
        if (!balanceMap.containsKey(user.getId())) {
            balanceMap.put(user.getId(), 0L);
            refreshBalances();
        }
        long oldBal = balanceMap.get(user.getId());
        if (creditAmount > 0) {
            long newBal = oldBal + creditAmount;
            balanceMap.replace(user.getId(), oldBal, newBal);
            try {
                user.openPrivateChannel().get().sendMessage(new EmbedBuilder()
                        .setTitle("Successfully updated account! Details:-")
                        .addField("Opening Balance", ":coin: " + oldBal)
                        .addField("Deposited", ":coin: " + creditAmount)
                        .addField("Closing Balance", ":coin: " + newBal)
                        .setColor(BasicCommands.getRandomColor()));
                System.out.println("User " + user.getDiscriminatedName() + " A/C updated:-\n" +
                        "Before: " + oldBal + ", Credited: " + creditAmount + ", After: " + newBal);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            refreshBalances();
            return true;
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Value should be more than 0.")
                    .setColor(BasicCommands.getRandomColor()));
        }
        return false;
    }

    public static boolean debitBalance(int debitAmount, User user, MessageCreateEvent event) {
        if (!balanceMap.containsKey(user.getId())) {
            balanceMap.put(user.getId(), 0L);
            refreshBalances();
        }
        long oldBal = balanceMap.get(user.getId());
        if (debitAmount > 0) {
            if (debitAmount <= oldBal) {
                long newBal = oldBal - debitAmount;
                balanceMap.replace(user.getId(), oldBal, newBal);
                try {
                    user.openPrivateChannel().get().sendMessage(new EmbedBuilder()
                            .setTitle("Successfully updated account! Details:-")
                            .addField("Opening Balance", ":coin: " + oldBal)
                            .addField("Withdrawn", ":coin: " + debitAmount)
                            .addField("Closing Balance", ":coin: " + newBal)
                            .setColor(BasicCommands.getRandomColor()));
                    System.out.println("User " + event.getMessageAuthor().getDisplayName() + " A/C updated:-\n" +
                            "Before: " + oldBal + ", Debited: " + debitAmount + ", After: " + newBal);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                refreshBalances();
                return true;
            } else if (oldBal == 0) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You can't withdraw, because you have no money!")
                        .setColor(BasicCommands.getRandomColor()));
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You can't withdraw more than you have in your bank!")
                        .setColor(BasicCommands.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Value should be more than 0.")
                    .setColor(BasicCommands.getRandomColor()));
        }
        return false;
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
                    collection.replaceOne(Filters.eq("name", "balance"), doc);
                    return "Updated replies!";
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
                    Document doc = new Document()
                            .append("name", "work")
                            .append("key", Main.userWorkedTimes.keySet())
                            .append("val", Main.userWorkedTimes.values());
                    collection.replaceOne(Filters.eq("name", "work"), doc);
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
                    Document doc = new Document()
                            .append("name", "rob")
                            .append("key", Main.userRobbedTimes.keySet())
                            .append("val", Main.userRobbedTimes.values());
                    collection.replaceOne(Filters.eq("name", "rob"), doc);
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
                    Document doc = new Document()
                            .append("name", "daily")
                            .append("key", Main.userDailyTimes.keySet())
                            .append("val", Main.userDailyTimes.values());
                    collection.replaceOne(Filters.eq("name", "daily"), doc);
                    return "Updated work times!";
                };
    
                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }

    public static int getRandomInteger(int max, int min) {
        int val = (int) (Math.random() * (max + 1));
        while (val < min) {
            val = (int) (Math.random() * (max + 1));
        }
        return val;
    }

    public static String getRandomWork() {
        return works[(int) (Math.random() * works.length)];
    }
}
