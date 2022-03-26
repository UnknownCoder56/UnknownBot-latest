package com.uniqueapps.UnknownBot.objects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.uniqueapps.UnknownBot.Main;
import com.uniqueapps.UnknownBot.commands.BasicCommands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class Shop {

    static ArrayList<ShopItem> items = new ArrayList<>();
    public static Map<Long, Map<String, Integer>> ownedItems = new HashMap<>();

    public static void initShop() {
        ShopItem juice = new ShopItem("Juice", "Refresh yourself with a cool an of juice.", "You drink some juice, and get refreshed.", "juice", 10000);
        ShopItem nitro = new ShopItem("Nitro", "Speed up your work. Work cooldown will be halved.", "You use nitro and gain speed, resulting in your work being done faster.", "nitro", 20000, "nitro");
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
                    if (itemChoice.equalsIgnoreCase(item.command)) {
                        item.buyItem(event);
                        return;
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
                }
                event.getChannel().sendMessage(embed);
            }
        } else if (command.startsWith(">use")) {
            String[] args = command.split(" ");
            if (args.length > 1) {
                String itemChoice = args[1];
                for (ShopItem item : items) {
                    if (itemChoice.equalsIgnoreCase(item.command)) {
                        item.useItem(event);
                        return;
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
        } else if (command.startsWith(">buy")) {
            String[] args = command.split(" ");
            if (args.length > 1) {
                String itemChoice = args[1];
                for (ShopItem item : items) {
                    if (itemChoice.equalsIgnoreCase(item.command)) {
                        item.buyItem(event);
                        return;
                    }
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("No item named " + itemChoice + " was found! Type '>shop' to see the available items.")
                    .setColor(BasicCommands.getRandomColor()));
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("No argument was supplied! Type '>shop' to get the commands for items, or type '>help' to get help on commands."));
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

class NitroExec implements Runnable {

    MessageCreateEvent event;

    public NitroExec(MessageCreateEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        event.getMessageAuthor().asUser().ifPresentOrElse((user) -> {
            if (Main.userWorkedTimes.containsKey(user.getId())) {
                long reduce = (30 - Duration.between(Main.userWorkedTimes.get(user.getId()), event.getMessage().getCreationTimestamp()).toSeconds()) / 2;
                Main.userWorkedTimes.replace(user.getId(), Main.userWorkedTimes.get(user.getId()), Main.userWorkedTimes.get(user.getId()).minus(reduce, ChronoUnit.SECONDS));
            }
        }, () -> {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You are not a user! Maybe you are a bot.")
                    .setColor(BasicCommands.getRandomColor()));
        });
    }
}
