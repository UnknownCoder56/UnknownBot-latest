import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class CurrencyCommands {

    public static Map<Long, Long> balanceMap = new HashMap<>();
    public static String[] works = {"did babysitting for 6 hours and earned", "finished a 100-day job and earned",
            "found some money on road and got", "sold a modern art picture and earned", "caught a robber and was prized with",
            "fixed neighbour's PC and earned", "checked his car bonnet and found", "won a bet and earned",
            "repaired cars at workshop for a day and earned", "won a lucky draw and earned"};

    public static void balance(MessageCreateEvent event) {
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

    public static void work(MessageCreateEvent event) {
        String work = getRandomWork();
        int earn = getRandomMoney();
        if (creditBalance(earn, event)) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle(event.getMessageAuthor().getDisplayName() + " Worked")
                    .setDescription(event.getMessageAuthor().getDisplayName() + " " + work +
                    " :coin: " + earn));
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
                        for (int pos = balanceMap.size() - 1; pos == 0; pos--) {
                            if (balanceMap.get(arrangedUsers.get(pos).getId()) < balanceMap.get(user.getId())) {
                                arrangedUsers.remove(user);
                                arrangedUsers.add(arrangedUsers.indexOf(arrangedUsers.get(pos)), user);
                            }
                        }
                    }
                }
            }
            arrangedUsers.removeIf(user -> arrangedUsers.indexOf(user) > 5);
            System.out.println(arrangedUsers);
            StringBuilder formattedTopUsers = new StringBuilder();
            int win = 0;
            for (User user : arrangedUsers) {
                win++;
                formattedTopUsers
                        .append(win)
                        .append(") ")
                        .append(user.getDisplayName(event.getServer().get()))
                        .append(" (:coin: " + balanceMap.get(user.getId()) + ")")
                        .append("\n");
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Top 5 richest users in " + event.getServer().get().getName() + ":-")
                    .setDescription(formattedTopUsers.toString()));
        }
    }

    public static boolean creditBalance(int amount, MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            if (!balanceMap.containsKey(event.getMessageAuthor().asUser().get().getId())) {
                balanceMap.put(event.getMessageAuthor().asUser().get().getId(), 0L);
            }
            long oldBal = balanceMap.get(event.getMessageAuthor().asUser().get().getId());
            long credit = amount;
            if (credit > 0) {
                long newBal = oldBal + credit;
                balanceMap.replace(event.getMessageAuthor().asUser().get().getId(), oldBal, newBal);
                try {
                    event.getMessageAuthor().asUser().get().openPrivateChannel().get().sendMessage(new EmbedBuilder()
                            .setTitle("Successfully updated account! Details:-")
                            .addField("Opening Balance", ":coin: " + oldBal)
                            .addField("Deposited", ":coin: " + credit)
                            .addField("Closing Balance", ":coin: " + newBal)
                            .setColor(BasicCommands.getRandomColor()));
                    System.out.println("User " + event.getMessageAuthor().getDisplayName() + " A/C updated:-\n" +
                            "Before: " + oldBal + ", Credited: " + credit + ", After: " + newBal);
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
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Only users can access this command! Maybe you are a bot.")
                    .setColor(BasicCommands.getRandomColor()));
        }
        return false;
    }

    public static boolean debitBalance(int amount, MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            if (!balanceMap.containsKey(event.getMessageAuthor().asUser().get().getId())) {
                balanceMap.put(event.getMessageAuthor().asUser().get().getId(), 0L);
            }
            long oldBal = balanceMap.get(event.getMessageAuthor().asUser().get().getId());
            long debit = amount;
            if (debit > 0) {
                if (debit <= oldBal) {
                    long newBal = oldBal - debit;
                    balanceMap.replace(event.getMessageAuthor().asUser().get().getId(), oldBal, newBal);
                    try {
                        event.getMessageAuthor().asUser().get().openPrivateChannel().get().sendMessage(new EmbedBuilder()
                                .setTitle("Successfully updated account! Details:-")
                                .addField("Opening Balance", ":coin: " + oldBal)
                                .addField("Withdrawn", ":coin: " + debit)
                                .addField("Closing Balance", ":coin: " + newBal)
                                .setColor(BasicCommands.getRandomColor()));
                        System.out.println("User " + event.getMessageAuthor().getDisplayName() + " A/C updated:-\n" +
                                "Before: " + oldBal + ", Debited: " + debit + ", After: " + newBal);
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
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Only users can access this command! Maybe you are a bot.")
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

    public static int getRandomMoney() {
        return (int) (Math.random() * 501);
    }

    public static String getRandomWork() {
        return works[(int) (Math.random() * works.length)];
    }
}
