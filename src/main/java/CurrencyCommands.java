import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CurrencyCommands {

    public static Map<Long, Long> balanceMap = new HashMap<>();
    public static String[] works = {"did babysitting for 6 hours and earned", "finished a 100-day job and earned",
            "found some money on road and got", "sold a modern art picture and earned", "caught a robber and was prized with",
            "fixed neighbour's PC and earned", "checked his car bonnet and found", "won a bet and earned",
            "repaired cars at workshop for a day and earned", "won a lucky draw and earned"};
    public static Map<Long, Integer> workCounters = new HashMap<>();

    public static void balance(MessageCreateEvent event) {
        if (event.getMessage().getMentionedUsers().size() > 0) {
            if (event.getMessageAuthor().asUser().isPresent()) {
                User balUser = event.getMessage().getMentionedUsers().get(0);
                if (!balanceMap.containsKey(balUser.getId())) {
                    balanceMap.put(balUser.getId(), 0L);
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

    public static void work(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            String work = CurrencyCommands.getRandomWork();
            int earn = CurrencyCommands.getRandomInteger(500, 100);
            if (CurrencyCommands.creditBalance(earn, event.getMessageAuthor().asUser().get(), event)) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle(event.getMessageAuthor().getDisplayName() + " Worked")
                        .setDescription(event.getMessageAuthor().getDisplayName() + " " + work +
                                " :coin: " + earn)
                        .setColor(BasicCommands.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You are not a user! Maybe you are a bot.")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void rob(MessageCreateEvent event) {
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
                                                .setDescription(event.getMessageAuthor().getDisplayName() + " successfully robbed " + robUser.getDisplayName(event.getServer().get()) + ", and earned :coin: " + robValue + ".")
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
                            }
                            if (debitBalance(giveValue, event.getMessageAuthor().asUser().get(), event)) {
                                if (creditBalance(giveValue, giveUser, event)) {
                                    event.getChannel().sendMessage(new EmbedBuilder()
                                            .setTitle("Success!")
                                            .setDescription(event.getMessageAuthor().getDisplayName() + " successfully gave " + giveUser.getDisplayName(event.getServer().get()) + " :coin: " + giveValue + ".")
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
                        .append(user.getDisplayName(event.getServer().get())).append(" (:coin: ").append(balanceMap.get(user.getId())).append(")")
                        .append("\n");
            }
            if (arrangedUsers.size() > 0) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Top " + arrangedUsers.size() + " richest user(s) in " + event.getServer().get().getName() + ":-")
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
                    .append(user.getDiscriminatedName()).append(" (:coin: ").append(balanceMap.get(user.getId())).append(")")
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

    public static boolean creditBalance(int creditAmount, User user, MessageCreateEvent event) {
        if (!balanceMap.containsKey(user.getId())) {
            balanceMap.put(user.getId(), 0L);
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
        File balanceFile = new File("balanceMap.data");
        try {
            balanceFile.delete();
            balanceFile.createNewFile();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(balanceFile))) {
                objectOutputStream.writeObject(balanceMap);
                System.out.println("Balance file updated!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

class SortByBalance implements Comparator<User> {

    Map<Long, Long> bals;

    public SortByBalance(Map<Long, Long> bals) {
        this.bals = bals;
    }

    @Override
    public int compare(User o1, User o2) {
        return (int) (bals.get(o1.getId()) - bals.get(o2.getId()));
    }
}
