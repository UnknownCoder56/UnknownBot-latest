package com.uniqueapps.UnknownBot.objects;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import com.uniqueapps.UnknownBot.commands.BasicCommands;
import com.uniqueapps.UnknownBot.commands.CurrencyCommands;

public class ShopItem {

    String name;
    String desc;
    String useMessage;
    String command;
    String emoji;
    Class<?> funcClass;
    int cost;
    boolean isFunctional;

    public ShopItem(String name, String desc, String useMessage, String command, String emoji, int cost) {
        this.name = name;
        this.desc = desc;
        this.useMessage = useMessage;
        this.command = command;
        this.emoji = emoji;
        this.cost = cost;
        funcClass = null;
        isFunctional = false;
    }

    public ShopItem(String name, String desc, String useMessage, String command, String emoji, int cost, Class<?> funcClass) {
        this.name = name;
        this.desc = desc;
        this.useMessage = useMessage;
        this.command = command;
        this.emoji = emoji;
        this.cost = cost;
        this.funcClass = funcClass;
        isFunctional = true;
    }

    public void useItem(MessageCreateEvent event) {
        Long userId = event.getMessageAuthor().asUser().orElseThrow().getId();
        if (Shop.ownedItems.containsKey(userId)) {
            if (Shop.ownedItems.get(userId).containsKey(name)) {
                if (!isFunctional) {
                    if (Shop.ownedItems.get(userId).get(name) > 0) {
                        int owned = Shop.ownedItems.get(userId).get(name);
                        Shop.ownedItems.get(userId).replace(name, owned, owned - 1);
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle(event.getMessageAuthor().getDisplayName() + " used " + emoji + " " + name)
                                .setDescription(useMessage + "\nYou now have " + (owned - 1) + " " + emoji + " " + name + "(s).")
                                .setColor(BasicCommands.getRandomColor()));
                        Shop.refreshOwnerships();
                        return;
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("You don't have this item! Buy it from the shop to use it.")
                                .setColor(BasicCommands.getRandomColor()));
                    }
                    return;
                } else {
                    if (Shop.ownedItems.get(userId).get(name) > 0) {
                        int owned = Shop.ownedItems.get(userId).get(name);
                        Shop.ownedItems.get(userId).replace(name, owned, owned - 1);
                        try {
                            new Thread((Runnable) funcClass.getDeclaredConstructor(MessageCreateEvent.class).newInstance(event)).start();
                        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                                 InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle(event.getMessageAuthor().getDisplayName() + " used " + emoji + " " + name)
                                .setDescription(useMessage + "\nYou now have " + (owned - 1) + " " + emoji + " " + name + "(s).")
                                .setColor(BasicCommands.getRandomColor()));
                        Shop.refreshOwnerships();
                    } else {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("You don't have this item! Buy it from the shop to use it.")
                                .setColor(BasicCommands.getRandomColor()));
                    }
                    return;
                }
            }
        }
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Error!")
                .setDescription("You don't have this item! Buy it from the shop to use it.")
                .setColor(BasicCommands.getRandomColor()));
    }

    public void buyItem(MessageCreateEvent event) {
        Long userId = event.getMessageAuthor().asUser().orElseThrow().getId();
        if (!Shop.ownedItems.containsKey(userId)) {
            Shop.ownedItems.put(userId, new HashMap<>());
        }
        if (CurrencyCommands.debitBalance(cost, event.getMessageAuthor().asUser().orElseThrow(), event)) {
            if (Shop.ownedItems.get(userId).containsKey(name)) {
                int owned = Shop.ownedItems.get(userId).get(name);
                Shop.ownedItems.get(userId).replace(name, owned, owned + 1);
            } else {
                Shop.ownedItems.get(userId).put(name, 1);
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success!")
                    .setDescription(
                            event.getMessageAuthor().getDisplayName() + " purchased 1 " + emoji + " " + name + ".\nNow you have "
                                    + Shop.ownedItems.get(userId).get(name) + " " + emoji + " " + name + "(s).")
                    .setColor(BasicCommands.getRandomColor()));
            Shop.refreshOwnerships();
        }
    }
}
