package com.uniqueapps.unknownbot.objects;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;
import com.mongodb.client.model.Filters;
import com.uniqueapps.unknownbot.Helper;
import com.uniqueapps.unknownbot.Main;

import org.bson.Document;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class Shop {

	static ArrayList<ShopItem> items = new ArrayList<>();
	public static Map<Long, Map<String, Integer>> ownedItems = new HashMap<>();

	public static void initShop() {
		ShopItem juice = new ShopItem("Juice", "Refresh yourself with a cool can of juice.",
				"You drink some juice, and get refreshed.", "juice", ":beverage_box:", 1000, false);
		ShopItem nitro = new ShopItem("Nitro", "Speed up your day, and work. Work and daily cooldown will be over.",
				"You use nitro and gain speed, resulting in your work and day being finished faster.", "nitro",
				":rocket:", 5400, false, Nitro.class);
		ShopItem code = new ShopItem("Hacker Code", "Very special bruteforce attack code. Tested upon top targets.",
				"Use it on your laptop.", "code", ":dvd:", 90000, true);
		ShopItem laptop = new ShopItem("Hacker Laptop",
				"Have a PC with you anytime, anywhere. Pen-testing utilities pre-installed.", "PC has booted.", "laptop",
				":computer:", 60000, true, HackerLaptop.class);
		ShopItem cat = new ShopItem("Pet Cat", "A pet cat, stays with you as a companion when you code.",
				"MEW!!! CODE!!!", "cat", ":cat:", 60000, true);
		ShopItem pass = new ShopItem("Premium Pass", "Flex item, shows up on rich people's profiles.",
				"No use lol. Flex on others.", "pass", ":crown:", 100000, true);
		ShopItem diamond = new ShopItem("Magna Diamond", "Flex item for the very-rich.", "FLEX TIME!", "magna",
				":large_blue_diamond:", 500000, true);

		items.add(juice);
		items.add(nitro);
		items.add(code);
		items.add(laptop);
		items.add(cat);
		items.add(pass);
		items.add(diamond);
	}

	public static void handleCommands(MessageCreateEvent event) {
		String command = event.getMessage().getContent();
		event.getMessageAuthor().asUser().ifPresentOrElse(user -> {
			Long userId = user.getId();
			if (command.startsWith(">shop")) {
				String[] args = command.split(" ");
				if (args.length > 1) {
					String itemChoice = args[1];
					for (ShopItem item : items) {
						if (itemChoice.equalsIgnoreCase(item.command)) {
							event.getChannel().sendMessage(new EmbedBuilder()
									.setTitle("UnknownBot's Shop - Information about " + item.emoji + " " + item.name)
									.setDescription("> Description: " + item.desc + "\n" +
											"> Persistent?: " + (item.isPersistent ? "Yes" : "No") + "\n" +
											"> Cost: :coin: " + item.cost + "\n" +
											"> Amount owned: " + getAmountOwned(userId, item.name) + "\n" +
											"> Command to get: ```" + ">buy " + item.command + "```" + "\n" +
											"> Command to use: ```" + ">use " + item.command + "```")
									.setColor(Helper.getRandomColor()));
							return;
						}
					}
					event.getChannel().sendMessage(new EmbedBuilder()
							.setTitle("Error!")
							.setDescription("No item named " + itemChoice + " was found! Type '>shop' to see the available items.")
							.setColor(Helper.getRandomColor()));
				} else {
					EmbedBuilder embed = new EmbedBuilder().setTitle("UnknownBot's Shop");
					int index = 0;
					for (ShopItem item : items) {
						index++;
						embed.addField(index + ") " + item.emoji + " " + item.name,
								"> Description: " + item.desc + "\n" +
										"> Persistent?: " + (item.isPersistent ? "Yes" : "No") + "\n" +
										"> Cost: :coin: " + item.cost + "\n" +
										"> Amount owned: " + getAmountOwned(userId, item.name) + "\n" +
										"> Command to get: ```" + ">buy " + item.command + "```" + "\n" +
										"> Command to use: ```" + ">use " + item.command + "```");
					}
					embed.setColor(Helper.getRandomColor());
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
							.setColor(Helper.getRandomColor()));
				} else {
					event.getChannel().sendMessage(new EmbedBuilder()
							.setTitle("Error!")
							.setDescription("No item was specified! Type '>shop' to know about buying stuff, or type '>help' to get help about the commands.")
							.setColor(Helper.getRandomColor()));
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
							.setColor(Helper.getRandomColor()));
				} else {
					event.getChannel().sendMessage(new EmbedBuilder()
							.setTitle("Error!")
							.setDescription("No argument was supplied! Type '>shop' to get the commands for items, or type '>help' to get help on commands."));
				}
			}
		}, () -> event.getChannel().sendMessage(new EmbedBuilder()
				.setTitle("Error!")
				.setDescription("You are not a user! You can't use this command.")
				.setColor(Helper.getRandomColor())));
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
		new Thread(() -> {
			try (MongoClient client = MongoClients.create(Main.settings);
					ClientSession session = client.startSession()) {
				TransactionBody<String> txnBody = () -> {
					MongoCollection<Document> collection = client.getDatabase("UnknownDatabase")
							.getCollection("UnknownCollection");
					List<Document> docs = new ArrayList<>();
					for (Map<String, Integer> map : ownedItems.values()) {
						docs.add(new Document().append("key", map.keySet()).append("val", map.values()));
					}
					Document doc = new Document().append("name", "item").append("key", ownedItems.keySet())
							.append("val", docs);
					if (collection.countDocuments(Filters.eq("name", "item")) > 0) {
						collection.replaceOne(Filters.eq("name", "item"), doc);
					} else {
						collection.insertOne(doc);
					}
					return "Updated ownerships!";
				};

				System.out.println(session.withTransaction(txnBody));
			}
		}).start();
	}
}

class Nitro implements Runnable {

	MessageCreateEvent event;

	public Nitro(MessageCreateEvent event) {
		this.event = event;
	}

	@Override
	public void run() {
		event.getMessageAuthor().asUser().ifPresentOrElse(user -> {
			if (Main.userWorkedTimes.containsKey(user.getId())) {
				long workReduce = (30 - Duration
						.between(Main.userWorkedTimes.get(user.getId()), event.getMessage().getCreationTimestamp())
						.toSeconds());
				Main.userWorkedTimes.replace(user.getId(), Main.userWorkedTimes.get(user.getId()),
						Main.userWorkedTimes.get(user.getId()).minus(workReduce, ChronoUnit.SECONDS));
				Helper.refreshWorks();
			}
			if (Main.userDailyTimes.containsKey(user.getId())) {
				long dailyReduce = (86400 - Duration
						.between(Main.userDailyTimes.get(user.getId()), event.getMessage().getCreationTimestamp())
						.toSeconds());
				Main.userDailyTimes.replace(user.getId(), Main.userDailyTimes.get(user.getId()),
						Main.userDailyTimes.get(user.getId()).minus(dailyReduce, ChronoUnit.SECONDS));
				Helper.refreshDailies();
			}
		}, () -> event.getChannel().sendMessage(new EmbedBuilder().setTitle("Error!")
				.setDescription("You are not a user! Maybe you are a bot.").setColor(Helper.getRandomColor())));
	}
}

class HackerLaptop implements Runnable {

	MessageCreateEvent event;

	public HackerLaptop(MessageCreateEvent event) {
		this.event = event;
	}

	@Override
	public void run() {
		event.getMessageAuthor().asUser().ifPresentOrElse(user -> {
			long userId = user.getId();
			new MessageBuilder()
					.addEmbed(new EmbedBuilder()
							.setTitle(event.getMessageAuthor().getDisplayName() + "'s PC")
							.setDescription("Press a button below to perform the desired action.")
							.addField("OS", "UnknownOS")
							.addField("Time (UTC or GMT)", LocalDateTime.now(ZoneId.of("UTC")).format(new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a").toFormatter()).toUpperCase())
							.addField("UnknownVPN Status", "Connected")
							.setTimestampToNow())
					.addComponents(ActionRow.of(
							Button.danger("laptop_code_" + userId, "Run Hacker Code"),
							Button.danger("laptop_off_" + userId, "Shut Down")
					))
					.send(event.getChannel());
		}, () -> event.getChannel().sendMessage(new EmbedBuilder().setTitle("Error!")
				.setDescription("You are not a user! Maybe you are a bot.").setColor(Helper.getRandomColor())));
	}
}