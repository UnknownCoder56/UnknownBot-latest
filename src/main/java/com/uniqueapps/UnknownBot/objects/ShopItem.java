package com.uniqueapps.UnknownBot.objects;

import java.util.HashMap;

import com.uniqueapps.UnknownBot.commands.BasicCommands;
import com.uniqueapps.UnknownBot.commands.CurrencyCommands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class ShopItem {

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
