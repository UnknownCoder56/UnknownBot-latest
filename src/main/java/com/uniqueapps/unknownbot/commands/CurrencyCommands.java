package com.uniqueapps.unknownbot.commands;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.uniqueapps.unknownbot.Helper;
import com.uniqueapps.unknownbot.Main;
import com.uniqueapps.unknownbot.objects.Shop;
import com.uniqueapps.unknownbot.objects.SortByBalance;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class CurrencyCommands {

    public static void balance(MessageCreateEvent event) {
        if (event.getMessage().getMentionedUsers().size() > 0) {
            if (event.getMessageAuthor().asUser().isPresent()) {
                User balUser = event.getMessage().getMentionedUsers().get(0);
                if (!Helper.balanceMap.containsKey(balUser.getId())) {
                    Helper.balanceMap.put(balUser.getId(), 0L);
                    Helper.refreshBalances();
                }
                long bal = Helper.balanceMap.get(balUser.getId());
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle(balUser.getDiscriminatedName() + "'s balance:-")
                        .addField("Bank", ":coin: " + bal)
                        .setColor(Helper.getRandomColor()));
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Only users can access this command! Maybe you are a bot.")
                        .setColor(Helper.getRandomColor()));
            }
        } else {
            if (event.getMessageAuthor().asUser().isPresent()) {
                if (!Helper.balanceMap.containsKey(event.getMessageAuthor().asUser().get().getId())) {
                    Helper.balanceMap.put(event.getMessageAuthor().asUser().get().getId(), 0L);
                    Helper.refreshBalances();
                }
                long bal = Helper.balanceMap.get(event.getMessageAuthor().asUser().get().getId());
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle(event.getMessageAuthor().getDisplayName() + "'s balance:-")
                        .addField("Bank", ":coin: " + bal)
                        .setColor(Helper.getRandomColor()));
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Only users can access this command! Maybe you are a bot.")
                        .setColor(Helper.getRandomColor()));
            }
        }
    }

    public static void daily(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            Long userId = event.getMessageAuthor().asUser().get().getId();
            if (Main.userDailyTimes.containsKey(userId)) {
                if (Duration.between(Main.userDailyTimes.get(userId), event.getMessage().getCreationTimestamp())
                        .toSeconds() >= (Helper.DAILY_COOLDOWN)) {
                    Main.userDailyTimes.put(userId, event.getMessage().getCreationTimestamp());
                    int earn = 5000;
                    if (Helper.creditBalance(earn, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle(event.getMessageAuthor().getDisplayName() + "'s Daily Earnings")
                                .setDescription(event.getMessageAuthor().getDisplayName()
                                        + " got their daily earnings: :coin: " + earn)
                                .setColor(Helper.getRandomColor()));
                    }
                    Helper.refreshDailies();
                } else {
                    int leftSeconds = (int) (Helper.DAILY_COOLDOWN - Duration
                            .between(Main.userDailyTimes.get(userId), event.getMessage().getCreationTimestamp())
                            .toSeconds());
                    int p1 = leftSeconds % 60;
                    int p2 = leftSeconds / 60;
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
                if (Helper.creditBalance(earn, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle(event.getMessageAuthor().getDisplayName() + "'s Daily Earnings")
                            .setDescription(event.getMessageAuthor().getDisplayName()
                                    + " got their daily earnings: :coin: " + earn)
                            .setColor(Helper.getRandomColor()));
                }
                Helper.refreshDailies();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You are not a user! Maybe you are a bot.")
                    .setColor(Helper.getRandomColor()));
        }
    }

    public static void weekly(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            Long userId = event.getMessageAuthor().asUser().get().getId();
            if (Main.userWeeklyTimes.containsKey(userId)) {
                if (Duration.between(Main.userWeeklyTimes.get(userId), event.getMessage().getCreationTimestamp())
                        .toSeconds() >= (Helper.WEEKLY_COOLDOWN)) {
                    Main.userDailyTimes.put(userId, event.getMessage().getCreationTimestamp());
                    int earn = 10000;
                    if (Helper.creditBalance(earn, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle(event.getMessageAuthor().getDisplayName() + "'s Weekly Earnings")
                                .setDescription(event.getMessageAuthor().getDisplayName()
                                        + " got their weekly earnings: :coin: " + earn)
                                .setColor(Helper.getRandomColor()));
                    }
                    Helper.refreshWeeklies();
                } else {
                    int leftSeconds = (int) (Helper.WEEKLY_COOLDOWN - Duration.between(Main.userWeeklyTimes.get(userId), event.getMessage().getCreationTimestamp()).toSeconds());
                    long days = leftSeconds / (24 * 3600);
                    leftSeconds = leftSeconds % (24 * 3600);
                    int hours = leftSeconds / 3600;
                    int minutes = (leftSeconds % 3600) / 60;
                    int seconds = leftSeconds % 60;
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You are currently on cooldown! You may use this command again after " + days + " days, " + hours
                            + " hours, " + minutes + " minutes and " + seconds + " seconds."));
                    System.out.println("Cooldown block faced!");
                }
            } else {
                Main.userWeeklyTimes.put(userId, event.getMessage().getCreationTimestamp());
                int earn = 10000;
                if (Helper.creditBalance(earn, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle(event.getMessageAuthor().getDisplayName() + "'s Weekly Earnings")
                            .setDescription(event.getMessageAuthor().getDisplayName()
                                    + " got their weekly earnings: :coin: " + earn)
                            .setColor(Helper.getRandomColor()));
                }
                Helper.refreshWeeklies();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You are not a user! Maybe you are a bot.")
                    .setColor(Helper.getRandomColor()));
        }
    }

    public static void monthly(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            Long userId = event.getMessageAuthor().asUser().get().getId();
            if (Main.userMonthlyTimes.containsKey(userId)) {
                if (Duration.between(Main.userMonthlyTimes.get(userId), event.getMessage().getCreationTimestamp())
                        .toSeconds() >= (Helper.MONTHLY_COOLDOWN)) {
                    Main.userMonthlyTimes.put(userId, event.getMessage().getCreationTimestamp());
                    int earn = 50000;
                    if (Helper.creditBalance(earn, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle(event.getMessageAuthor().getDisplayName() + "'s Monthly Earnings")
                                .setDescription(event.getMessageAuthor().getDisplayName()
                                        + " got their monthly earnings: :coin: " + earn)
                                .setColor(Helper.getRandomColor()));
                    }
                    Helper.refreshMonthlies();
                } else {
                    int leftSeconds = (int) (Helper.MONTHLY_COOLDOWN - Duration.between(Main.userMonthlyTimes.get(userId), event.getMessage().getCreationTimestamp()).toSeconds());
                    long days = leftSeconds / (24 * 3600);
                    leftSeconds = leftSeconds % (24 * 3600);
                    int hours = leftSeconds / 3600;
                    int minutes = (leftSeconds % 3600) / 60;
                    int seconds = leftSeconds % 60;
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You are currently on cooldown! You may use this command again after " + days + " days, " + hours
                            + " hours, " + minutes + " minutes and " + seconds + " seconds."));
                    System.out.println("Cooldown block faced!");
                }
            } else {
                Main.userMonthlyTimes.put(userId, event.getMessage().getCreationTimestamp());
                int earn = 50000;
                if (Helper.creditBalance(earn, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle(event.getMessageAuthor().getDisplayName() + "'s Monthly Earnings")
                            .setDescription(event.getMessageAuthor().getDisplayName()
                                    + " got their monthly earnings: :coin: " + earn)
                            .setColor(Helper.getRandomColor()));
                }
                Helper.refreshMonthlies();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You are not a user! Maybe you are a bot.")
                    .setColor(Helper.getRandomColor()));
        }
    }

    public static void work(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            Long userId = event.getMessageAuthor().asUser().get().getId();
            if (Main.userWorkedTimes.containsKey(userId)) {
                if (Duration.between(Main.userWorkedTimes.get(userId), event.getMessage().getCreationTimestamp())
                        .toSeconds() >= Helper.BASIC_COOLDOWN) {
                    Main.userWorkedTimes.put(userId, event.getMessage().getCreationTimestamp());
                    String work = Helper.getRandomWork();
                    int earn = Helper.getRandomInteger(500, 100);
                    if (Helper.creditBalance(earn, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle(event.getMessageAuthor().getDisplayName() + " Worked")
                                .setDescription(event.getMessageAuthor().getDisplayName() + " " + work +
                                        " :coin: " + earn)
                                .setColor(Helper.getRandomColor()));
                    }
                    Helper.refreshWorks();
                } else {
                    int left = (int) (Helper.BASIC_COOLDOWN - Duration
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
                String work = Helper.getRandomWork();
                int earn = Helper.getRandomInteger(500, 100);
                if (Helper.creditBalance(earn, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle(event.getMessageAuthor().getDisplayName() + " Worked")
                            .setDescription(event.getMessageAuthor().getDisplayName() + " " + work +
                                    " :coin: " + earn)
                            .setColor(Helper.getRandomColor()));
                }
                Helper.refreshWorks();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You are not a user! Maybe you are a bot.")
                    .setColor(Helper.getRandomColor()));
        }
    }

    public static void rob(MessageCreateEvent event) {
        Long commanderId = event.getMessageAuthor().asUser().get().getId();
        if (Main.userRobbedTimes.containsKey(commanderId)) {
            if (Duration.between(Main.userRobbedTimes.get(commanderId), event.getMessage().getCreationTimestamp())
                    .toSeconds() >= Helper.BASIC_COOLDOWN) {
                Main.userRobbedTimes.put(commanderId, event.getMessage().getCreationTimestamp());
                Helper.refreshRobs();
                User robUser = event.getMessage().getMentionedUsers().get(0);
                if (event.getServer().isPresent()) {
                    if (event.getMessageAuthor().asUser().isPresent()) {
                        if (!robUser.isBot()) {
                            if (!Main.userSettingsMap.get(commanderId).isBankPassiveEnabled()) {
                                if (!Main.userSettingsMap.get(robUser.getId()).isBankPassiveEnabled()) {
                                    int robValue = Helper.getRandomInteger(5000, 1000);
                                    if (Helper.balanceMap.containsKey(robUser.getId())) {
                                        if (robUser.getId() != event.getMessageAuthor().asUser().get().getId()) {
                                            if (Helper.balanceMap.get(robUser.getId()) > 1000) {
                                                while (Helper.balanceMap.get(robUser.getId()) < robValue) {
                                                    robValue = Helper.getRandomInteger(5000, 1000);
                                                }
                                                if (Helper.debitBalance(robValue, robUser, event.getChannel())) {
                                                    if (Helper.creditBalance(robValue, event.getMessageAuthor().asUser().get(),
                                                            event.getChannel())) {
                                                        event.getChannel().sendMessage(new EmbedBuilder()
                                                                .setTitle("Success!")
                                                                .setDescription(event.getMessageAuthor().getDisplayName()
                                                                        + " successfully robbed "
                                                                        + robUser.getDisplayName(event.getServer().get())
                                                                        + ", and earned :coin: " + robValue + ".")
                                                                .setColor(Helper.getRandomColor()));
                                                        Helper.refreshBalances();
                                                    }
                                                }
                                            } else {
                                                event.getChannel().sendMessage(new EmbedBuilder()
                                                        .setTitle("Error!")
                                                        .setDescription("That user does not have enough money to rob!")
                                                        .setColor(Helper.getRandomColor()));
                                            }
                                        } else {
                                            event.getChannel().sendMessage(new EmbedBuilder()
                                                    .setTitle("Error!")
                                                    .setDescription("You can't rob yourself!")
                                                    .setColor(Helper.getRandomColor()));
                                        }
                                    } else {
                                        event.getChannel().sendMessage(new EmbedBuilder()
                                                .setTitle("Error!")
                                                .setDescription("That user does not have enough money to rob!")
                                                .setColor(Helper.getRandomColor()));
                                    }
                                } else {
                                    event.getChannel().sendMessage(new EmbedBuilder()
                                            .setTitle("Error!")
                                            .setDescription("That user is in passive mode! Try someone else.")
                                            .setColor(Helper.getRandomColor()));
                                }
                            } else {
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Error!")
                                        .setDescription("You are in passive mode! You can't rob anyone.")
                                        .setColor(Helper.getRandomColor()));
                            }
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Error!")
                                    .setDescription("You can't rob bots!")
                                    .setColor(Helper.getRandomColor()));
                        }
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("You are not a user! Maybe you are a bot.")
                                .setColor(Helper.getRandomColor()));
                    }
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("This command only works in servers!")
                            .setColor(Helper.getRandomColor()));
                }
            } else {
                int left = (int) (Helper.BASIC_COOLDOWN - Duration
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
            Helper.refreshRobs();
            User robUser = event.getMessage().getMentionedUsers().get(0);
            if (event.getServer().isPresent()) {
                if (event.getMessageAuthor().asUser().isPresent()) {
                    if (!robUser.isBot()) {
                        if (!Main.userSettingsMap.get(robUser.getId()).isBankPassiveEnabled()) {
                            if (!Main.userSettingsMap.get(robUser.getId()).isBankPassiveEnabled()) {
                                int robValue = Helper.getRandomInteger(5000, 1000);
                                if (Helper.balanceMap.containsKey(robUser.getId())) {
                                    if (robUser.getId() != event.getMessageAuthor().asUser().get().getId()) {
                                        if (Helper.balanceMap.get(robUser.getId()) > 1000) {
                                            while (Helper.balanceMap.get(robUser.getId()) < robValue) {
                                                robValue = Helper.getRandomInteger(5000, 1000);
                                            }
                                            if (Helper.debitBalance(robValue, robUser, event.getChannel())) {
                                                if (Helper.creditBalance(robValue, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                                                    event.getChannel().sendMessage(new EmbedBuilder()
                                                            .setTitle("Success!")
                                                            .setDescription(event.getMessageAuthor().getDisplayName()
                                                                    + " successfully robbed "
                                                                    + robUser.getDisplayName(event.getServer().get())
                                                                    + ", and earned :coin: " + robValue + ".")
                                                            .setColor(Helper.getRandomColor()));
                                                    Helper.refreshBalances();
                                                }
                                            }
                                        } else {
                                            event.getChannel().sendMessage(new EmbedBuilder()
                                                    .setTitle("Error!")
                                                    .setDescription("That user does not have enough money to rob!")
                                                    .setColor(Helper.getRandomColor()));
                                        }
                                    } else {
                                        event.getChannel().sendMessage(new EmbedBuilder()
                                                .setTitle("Error!")
                                                .setDescription("You can't rob yourself!")
                                                .setColor(Helper.getRandomColor()));
                                    }
                                } else {
                                    event.getChannel().sendMessage(new EmbedBuilder()
                                            .setTitle("Error!")
                                            .setDescription("That user does not have enough money to rob!")
                                            .setColor(Helper.getRandomColor()));
                                }
                            } else {
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Error!")
                                        .setDescription("That user is in passive mode! Try someone else.")
                                        .setColor(Helper.getRandomColor()));
                            }
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Error!")
                                    .setDescription("You are in passive mode! You can't rob anyone.")
                                    .setColor(Helper.getRandomColor()));
                        }
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("You can't rob bots!")
                                .setColor(Helper.getRandomColor()));
                    }
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You are not a user! Maybe you are a bot.")
                            .setColor(Helper.getRandomColor()));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("This command only works in servers!")
                        .setColor(Helper.getRandomColor()));
            }
        }
    }

    public static void give(MessageCreateEvent event) {
        User giveUser = event.getMessage().getMentionedUsers().get(0);
        String[] args = event.getMessage().getContent().split(" ");
        int giveValue = Integer.parseInt(args[1]);
        if (event.getServer().isPresent()) {
            if (event.getMessageAuthor().asUser().isPresent()) {
                long id = event.getMessageAuthor().asUser().get().getId();
                if (!giveUser.isBot()) {
                    if (!Main.userSettingsMap.get(id).isBankPassiveEnabled()) {
                        if (!Main.userSettingsMap.get(giveUser.getId()).isBankPassiveEnabled()) {
                            if (!(giveValue > Helper.balanceMap.get(event.getMessageAuthor().asUser().get().getId()))) {
                                if (giveUser.getId() != event.getMessageAuthor().asUser().get().getId()) {
                                    if (!Helper.balanceMap.containsKey(giveUser.getId())) {
                                        Helper.balanceMap.put(giveUser.getId(), 0L);
                                        Helper.refreshBalances();
                                    }
                                    if (Helper.debitBalance(giveValue, event.getMessageAuthor().asUser().get(), event.getChannel())) {
                                        if (Helper.creditBalance(giveValue, giveUser, event.getChannel())) {
                                            event.getChannel().sendMessage(new EmbedBuilder()
                                                    .setTitle("Success!")
                                                    .setDescription(
                                                            event.getMessageAuthor().getDisplayName() + " successfully gave "
                                                                    + giveUser.getDisplayName(event.getServer().get())
                                                                    + " :coin: " + giveValue + ".")
                                                    .setColor(Helper.getRandomColor()));
                                            Helper.refreshBalances();
                                        }
                                    }
                                } else {
                                    event.getChannel().sendMessage(new EmbedBuilder()
                                            .setTitle("Error!")
                                            .setDescription("You can't give money to yourself!")
                                            .setColor(Helper.getRandomColor()));
                                }
                            } else {
                                event.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Error!")
                                        .setDescription("You can't give more money than you have in your account!")
                                        .setColor(Helper.getRandomColor()));
                            }
                        } else {
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Error!")
                                    .setDescription("That user is in passive mode. You can't give money to them.")
                                    .setColor(Helper.getRandomColor()));
                        }
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("You are in passive mode! You can't give money to anyone.")
                                .setColor(Helper.getRandomColor()));
                    }
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You can't give money to bots!")
                            .setColor(Helper.getRandomColor()));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You are not a user! Maybe you are a bot.")
                        .setColor(Helper.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("This command only works in servers!")
                    .setColor(Helper.getRandomColor()));
        }
    }

    public static void leaderboard(MessageCreateEvent event) {
        if (event.getServer().isPresent()) {
            System.out.println("Cache ok?: " + event.getServer().get().hasAllMembersInCache());
            Collection<User> users = event.getServer().get().getMembers();
            ArrayList<User> arrangedUsers = new ArrayList<>();
            for (User user : users) {
                if (!user.isBot()) {
                    if (Helper.balanceMap.containsKey(user.getId())) {
                        arrangedUsers.add(arrangedUsers.size(), user);
                    }
                }
            }
            SortByBalance sortByBalance = new SortByBalance(Helper.balanceMap);
            arrangedUsers.sort(Collections.reverseOrder(sortByBalance));
            arrangedUsers.removeIf(user -> arrangedUsers.indexOf(user) > 4);
            System.out.println(arrangedUsers);
            StringBuilder formattedTopUsers = new StringBuilder();
            int win = 0;
            for (User user : arrangedUsers) {
                win++;
                formattedTopUsers
                        .append(win)
                        .append(") ")
                        .append(user.getDisplayName(event.getServer().get())).append(" (:coin: ")
                        .append(Helper.balanceMap.get(user.getId())).append(")")
                        .append("\n");
            }
            if (arrangedUsers.size() > 0) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Top " + arrangedUsers.size() + " richest user(s) in "
                                + event.getServer().get().getName() + ":-")
                        .setDescription(formattedTopUsers.toString())
                        .setColor(Helper.getRandomColor()));
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("No one has more than :coin: 0 in this server!")
                        .setColor(Helper.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("This command only works in servers!")
                    .setColor(Helper.getRandomColor()));
        }
    }

    public static void globalLeaderboard(MessageCreateEvent event) {
        ArrayList<User> users = new ArrayList<>();
        for (Long id : Helper.balanceMap.keySet()) {
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
                if (Helper.balanceMap.containsKey(user.getId())) {
                    arrangedUsers.add(arrangedUsers.size(), user);
                }
            }
        }
        SortByBalance sortByBalance = new SortByBalance(Helper.balanceMap);
        arrangedUsers.sort(Collections.reverseOrder(sortByBalance));
        arrangedUsers.removeIf(user -> arrangedUsers.indexOf(user) > 4);
        System.out.println(arrangedUsers);
        StringBuilder formattedTopUsers = new StringBuilder();
        int win = 0;
        for (User user : arrangedUsers) {
            win++;
            formattedTopUsers
                    .append(win)
                    .append(") ")
                    .append(user.getDiscriminatedName()).append(" (:coin: ").append(Helper.balanceMap.get(user.getId()))
                    .append(")")
                    .append("\n");
        }
        if (arrangedUsers.size() > 0) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Top " + arrangedUsers.size() + " richest user(s) of UnknownBot:-")
                    .setDescription(formattedTopUsers.toString())
                    .setColor(Helper.getRandomColor()));
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("No one has more than :coin: 0 in our database!")
                    .setColor(Helper.getRandomColor()));
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
                        itemText.append(index).append(") ").append(name).append(" (Count: ").append(userItems.get(name)).append(")\n");
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
}