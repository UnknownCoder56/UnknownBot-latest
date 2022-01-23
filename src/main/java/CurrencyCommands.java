import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
    static int dailyCoolDown = 24;

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

    public static void daily(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            Long userId = event.getMessageAuthor().asUser().get().getId();
            if (Main.userDailyTimes.containsKey(userId)) {
                if (Duration.between(Main.userDailyTimes.get(userId), event.getMessage().getCreationTimestamp())
                        .toSeconds() >= (dailyCoolDown * 3600)) {
                    Main.userDailyTimes.put(userId, event.getMessage().getCreationTimestamp());
                    int earn = 5000;
                    if (CurrencyCommands.creditBalance(earn, event.getMessageAuthor().asUser().get(), event)) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle(event.getMessageAuthor().getDisplayName() + "'s Daily Earnings")
                                .setDescription(event.getMessageAuthor().getDisplayName()
                                        + " got their daily earnings: :coin: " + earn)
                                .setColor(BasicCommands.getRandomColor()));
                    }
                } else {
                    int leftSeconds = (int) (dailyCoolDown - Duration
                            .between(Main.userWorkedTimes.get(userId), event.getMessage().getCreationTimestamp())
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

class Shop {

    static ArrayList<ShopItem> items = new ArrayList<>();
    static Map<Long, Map<String, Integer>> ownedItems = new HashMap<>();

    public static void initShop() {
        ShopItem juice = new ShopItem("Juice", "Refresh yourself with a cool an of juice.", "You drink some juice, and get refreshed.", "juice", 10000);
        ShopItem nitro = new ShopItem("Nitro", "Speed up your work. Work cooldown will be halved.", "You use nitro and gain speed, resulting in your work being done faster.", "nitro", 20000);
        ShopItem laptop = new ShopItem("Hacker Laptop", "Write code anytime, anywhere. Pen testing utilities pre-installed.", "HACKED EVERYTHING", "laptop", 30000);
        ShopItem code = new ShopItem("Hacker Code", "Very special bruteforce attack code. Tested upon top targets.", "HACKED PENTAGON", "code", 50000);
        ShopItem cat = new ShopItem("Pet Cat", "A pet cat, stays with you as a companion when you code.", "MEW!!! CODE!!!", "cat", 50000);
        ShopItem pass = new ShopItem("Premium Pass", "Flex item, shows up on rich people's profiles.", "No use lol. Flex on others.", "pass", 100000);
        ShopItem diamond = new ShopItem("Magna Diamond", "Flex item for the very-rich.", "FLEX TIME!", "magna", 500000);

        items.add(juice);
        items.add(nitro);
        items.add(laptop);
        items.add(code);
        items.add(cat);
        items.add(pass);
        items.add(diamond);
    }

    public static void handleCommands(MessageCreateEvent event) {
        String command = event.getMessage().getContent();
        if (command.startsWith(">shop")) {
            String[] args = command.split(" ");
            if (args.length > 1) {
                String itemChoice = args[1];
                for (ShopItem item : items) {
                    if (itemChoice.equalsIgnoreCase(item.itemName.split(" ")[0]) || itemChoice.equalsIgnoreCase(item.itemName.split(" ")[1])) {
                        item.buyItem(event);
                        break;
                    }
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("No item named " + itemChoice + " was found! Type '>shop' to see the available items.")
                    .setColor(BasicCommands.getRandomColor()));
            } else {
                EmbedBuilder embed = new EmbedBuilder().setTitle("UnknownBot's Shop");
                Long userId = event.getMessageAuthor().asUser().get().getId();
                int index = 0;
                for (ShopItem item : items) {
                    index++;
                    embed.addField(index + ")" + item.itemName, 
                        "Description: " + item.itemDesc + "\n" + 
                        "Cost: " + item.itemCost + "\n" +
                        "Amount owned: " + getAmountOwned(userId, item.itemName) + "\n" + 
                        "Command to get: ```" + ">shop " + item.command + "```" + "\n" +
                        "Command to use: ```" + ">use " + item.command + "```");
                    embed.setColor(BasicCommands.getRandomColor());
                    event.getChannel().sendMessage(embed);
                }
            }
        } else if (command.startsWith(">use")) {
            String[] args = command.split(" ");
            if (args.length > 1) {
                String itemChoice = args[1];
                for (ShopItem item : items) {
                    if (itemChoice.equalsIgnoreCase(item.itemName.split(" ")[0]) || itemChoice.equalsIgnoreCase(item.itemName.split(" ")[1])) {
                        item.useItem(event);
                        break;
                    }
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("No item named " + itemChoice + " was found! Type '>shop' to see the available items.")
                    .setColor(BasicCommands.getRandomColor()));
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("No item was specified! Type '>shop' to buy stuff, or type '>help' to get help about the commands.")
                    .setColor(BasicCommands.getRandomColor()));
            } 
        }
    }

    public static int getAmountOwned(Long userId, String itemName) {
        if (ownedItems.containsKey(userId)) {
            if (ownedItems.get(userId).containsKey(itemName)) {
                return ownedItems.get(userId).get(itemName);
            }
        }
        return 0;
    }

    public static void refreshOwnerships() {
        File shopFile = new File("shopFile.data");
        try {
            shopFile.delete();
            shopFile.createNewFile();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(shopFile))) {
                objectOutputStream.writeObject(ownedItems);
                System.out.println("Shop file updated!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ShopItem {

    String itemName;
    String itemDesc;
    String useMessage;
    String command;
    int itemCost;

    public ShopItem(String name, String desc, String useM, String command, int cost) {
        itemName = name;
        itemDesc = desc;
        itemCost = cost;
        useMessage = useM;
        this.command = command;
    }

    public void useItem(MessageCreateEvent event) {
        Long userId = event.getMessageAuthor().asUser().get().getId();
        if (Shop.ownedItems.containsKey(userId)) {
            if (Shop.ownedItems.get(userId).containsKey(itemName)) {
                if (Shop.ownedItems.get(userId).get(itemName) > 0) {
                    int owned = Shop.ownedItems.get(userId).get(itemName);
                    Shop.ownedItems.get(userId).replace(itemName, owned, owned - 1);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle(event.getMessageAuthor().getDisplayName() + " used " + itemName)
                            .setDescription(useMessage + "\nYou now have " + (owned - 1) + " " + itemName + "(s).")
                            .setColor(BasicCommands.getRandomColor()));
                    Shop.refreshOwnerships();
                    return;
                }
            }
        }
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Error!")
                .setDescription("You don't have this item, so you can't use it!")
                .setColor(BasicCommands.getRandomColor()));
    }

    public void buyItem(MessageCreateEvent event) {
        Long userId = event.getMessageAuthor().asUser().get().getId();
        if (!Shop.ownedItems.containsKey(userId)) {
            Shop.ownedItems.put(userId, new HashMap<String, Integer>());
        }
        if (CurrencyCommands.debitBalance(itemCost, event.getMessageAuthor().asUser().get(), event)) {
            if (Shop.ownedItems.get(userId).containsKey(itemName)) {
                int owned = Shop.ownedItems.get(userId).get(itemName);
                Shop.ownedItems.get(userId).replace(itemName, owned, owned + 1);
            } else {
                Shop.ownedItems.get(userId).put(itemName, 1);
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success!")
                    .setDescription(
                            event.getMessageAuthor().getDisplayName() + " purchased 1 " + itemName + ".\nNow you have "
                                    + Shop.ownedItems.get(userId).get(itemName) + " " + itemName + "(s).")
                    .setColor(BasicCommands.getRandomColor()));
            Shop.refreshOwnerships();
        }
    }
}
