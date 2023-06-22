package com.uniqueapps.UnknownBot.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.*;
import com.uniqueapps.UnknownBot.Main;
import com.uniqueapps.UnknownBot.objects.UserSettings;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class BasicCommands {

    public static Map<String, String> customReplies = new HashMap<>();
    public static final String version = "3.8.0";
    final static int botWin = 0;
    final static int userWin = 1;
    final static int tie = 2;
    final static int error = 3;

    public static void admes(MessageCreateEvent event) {
        try {
            Message msg = event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Bot is thinking... :thinking:")).get();
            AsyncCommands.Admes admes = new AsyncCommands.Admes(event, msg);
            CompletableFuture<AsyncCommands.Admes> completableFuture = CompletableFuture
                    .supplyAsync(() -> admes);

            completableFuture
                    .thenApplyAsync(admes1 -> {
                        admes1.run();
                        return admes1;
                    });
        } catch (StringIndexOutOfBoundsException ex) {
            event.getChannel().sendMessage(new EmbedBuilder()
            		.setTitle("Please ask something after typing '>admes' and " +
                    "put space between command and asking.")
            		.setColor(getRandomColor()));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void makefile(MessageCreateEvent event) {
        AsyncCommands.Makefile makefile = new AsyncCommands.Makefile(event);
        CompletableFuture<AsyncCommands.Makefile> completableFuture = CompletableFuture
                .supplyAsync(() -> makefile);

        completableFuture
                .thenApplyAsync(makefile1 -> {
                    makefile1.run();
                    return makefile1;
                });
    }

    public static void calc(MessageCreateEvent event) {
    	String reply;
        String[] text = event.getMessage().getContent().split(",");
        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(text));
        try {
            String num = argsList.get(0).substring(6);
            argsList.set(0, num);
            System.out.println("Args: " + argsList);
            try {
                float num1 = Float.parseFloat(argsList.get(0));
                float num2 = Float.parseFloat(argsList.get(2));
                float result;
                switch (argsList.get(1)) {
                    case ("+") -> {
                        result = num1 + num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                    }
                    case ("-") -> {
                        result = num1 - num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                    }
                    case ("*") -> {
                        result = num1 * num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                    }
                    case ("/") -> {
                        result = num1 / num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                    }
                    default -> reply = "Not a valid operation symbol. Valid ones are +, -, * and /.";
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle(reply)
                        .setColor(getRandomColor()));
            } catch (NumberFormatException ex) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Please provide a number, as '>calc (num1),(+ or - or * or /),(num2)'.")
                        .setColor(getRandomColor()));
            } catch (IndexOutOfBoundsException ex) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Incorrect number of arguments given. Correct syntax - >calc (num1),(+ or - or * or /),(num2)")
                        .setColor(getRandomColor()));
            } catch (Exception ex) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Unknown Error Occurred! Please report this to the developer (UnknownPro56).\n" +
                                "Error: " + ex.getMessage())
                        .setColor(getRandomColor()));
            }
        } catch (IndexOutOfBoundsException ex) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Incorrect number of arguments given. Correct syntax - >calc (num1),(+ or - or * or /),(num2)")
                    .setColor(getRandomColor()));
        }
    }

    public static void setCustomReply(MessageCreateEvent event) {
        try {
            String text = event.getMessage().getContent();
            String[] args = text.split(",");
            ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
            argsList.set(0, argsList.get(0).replace(">reply ", ""));
            System.out.println("Args: " + argsList);
            if (!argsList.get(0).isEmpty() && !argsList.get(1).isEmpty()) {
                customReplies.put(argsList.get(0), argsList.get(1));
                event.getChannel().sendMessage(new EmbedBuilder()
                		.setTitle("Success!")
                		.setDescription("Successfully set custom reply! Bot will now reply with '"
                		+ customReplies.get(argsList.get(0)) + "' when any message contains '" + argsList.get(0) + "'.")
                		.setColor(getRandomColor()));
            	refreshReplies();
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                		.setTitle("Error!")
                		.setDescription("No arguments given! Correct syntax - >reply (text),(reply) _No spaces between commas and arguments!_")
                		.setColor(getRandomColor()));
            }
        } catch (IndexOutOfBoundsException ex) {
            event.getChannel().sendMessage(new EmbedBuilder()
            		.setTitle("Error!")
            		.setDescription("Incorrect arguments given. Correct syntax - >reply (text),(reply) _No spaces between commas and arguments!_")
            		.setColor(getRandomColor()));
        }
    }

    public static void customReply(MessageCreateEvent event, String reply) {
        event.getChannel().sendMessage(reply);
    }

    public static void noReply(MessageCreateEvent event) {
        for (String text : customReplies.keySet()) {
            if (event.getMessage().getContent().contains(text)) {
                customReplies.remove(text);
                refreshReplies();
                event.getChannel().sendMessage(new EmbedBuilder()
                		.setTitle("Success!")
                		.setDescription("Successfully disabled custom reply " + text + "!")
                		.setColor(getRandomColor()));
                return;
            }
        }
        try {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("No reply named '" + event.getMessage().getContent().substring(9) + "' was found!")
                    .setColor(getRandomColor()));
        } catch (IndexOutOfBoundsException ex) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Incorrect arguments given. Correct syntax - >noreply (reply name)")
                    .setColor(getRandomColor()));
        }
    }

    public static void ping(MessageCreateEvent event) {
        try {
			event.getChannel().sendMessage(new EmbedBuilder()
					.setTitle("Pong!")
					.setDescription("Latency is " + TimeUnit.NANOSECONDS.toMillis(Main.api.measureRestLatency().get().getNano()) + " ms")
					.setColor(getRandomColor()));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }

    public static void hello(MessageCreateEvent event) {
        event.getChannel().sendMessage(new EmbedBuilder()
        		.setTitle("Hello!")
        		.setDescription("Hello There, " + event.getMessageAuthor().getName() + "! UnknownBot"
        				+ " here at your service. Type '>help' for more information on supported commands.")
        		.setColor(getRandomColor()));
    }

    public static void dt(MessageCreateEvent event) {
        event.getChannel().sendMessage(new EmbedBuilder()
        		.setTitle("Current Time:-")
        		.addField("UTC or GMT", LocalDateTime.now(ZoneId.of("UTC")).format(
        				new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a").toFormatter()).toUpperCase())
        		.setColor(getRandomColor()));
    }

    public static void help(MessageCreateEvent event) {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/commands.json")))) {
            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray utilityCommands = object.getAsJsonArray("utility");
            JsonArray moderationCommands = object.getAsJsonArray("moderation");
            JsonArray economyCommands = object.getAsJsonArray("economy");

            String[] args = event.getMessage().getContent().split(" ");
            String category = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "";
            String[] categories = {"utility", "moderation", "economy"};
            if (Objects.equals(category, categories[0])) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("UnknownBot's Utility commands:-")
                        .setColor(getRandomColor());

                utilityCommands.forEach(jsonElement -> {
                    JsonObject command = jsonElement.getAsJsonObject();
                    embedBuilder.addField(command.get("name").getAsString(), command.get("desc").getAsString(), true);
                });

                event.getChannel().sendMessage(embedBuilder);
            } else if (Objects.equals(category, categories[1])) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("UnknownBot's Moderation commands:-")
                        .setColor(getRandomColor());

                moderationCommands.forEach(jsonElement -> {
                    JsonObject command = jsonElement.getAsJsonObject();
                    embedBuilder.addField(command.get("name").getAsString(), command.get("desc").getAsString(), true);
                });

                event.getChannel().sendMessage(embedBuilder);
            } else if (Objects.equals(category, categories[2])) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("UnknownBot's Economy commands:-")
                        .setColor(getRandomColor());

                economyCommands.forEach(jsonElement -> {
                    JsonObject command = jsonElement.getAsJsonObject();
                    embedBuilder.addField(command.get("name").getAsString(), command.get("desc").getAsString(), true);
                });

                event.getChannel().sendMessage(embedBuilder);
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("UnknownBot's help docs")
                        .setDescription("""
                                UnknownBot is a multipurpose bot, currently under active development.
                                To get help about commands, type ">help (category)", where categories include:-

                                1) Utility ```>help utility```
                                2) Moderation ```>help moderation```
                                3) Economy ```>help economy```

                                UnknownBot also includes slash commands, type / and select UnknownBot's logo to get a list of them.

                                For more information on the bot, how to use it, or queries about, contact us on our official discord server:-
                                https://discord.gg/t79ZyuHr5K/
                                Or visit UnknownBot's website:-
                                https://user783667580106702848.pepich.de/""")
                        .setColor(getRandomColor()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void botInfo(MessageCreateEvent event) {
        event.getChannel().sendMessage(InfoEmbeds.botInfo(event.getServer()));
    }

    public static void replies(MessageCreateEvent event) {
        StringBuilder repl = new StringBuilder();
        for (String reply : customReplies.keySet()) {
            repl.append(reply).append(": ").append(customReplies.get(reply)).append("\n");
        }
        if (!repl.toString().equals("")) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Currently set custom replies:-")
                    .setDescription(repl.toString())
                    .setColor(getRandomColor()));
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Currently set custom replies:-")
                    .setDescription("No custom replies have been set up yet!")
                    .setColor(getRandomColor()));
        }
    }

    public static void dm(MessageCreateEvent event) {
        User user = event.getMessage().getMentionedUsers().get(0);
        String message = StringUtils.substringBetween(event.getMessage().getContent(), "\"", "\"");
        try {
            PrivateChannel channel = user.openPrivateChannel().get();

            event.getServer().ifPresentOrElse((server) -> channel.sendMessage(new EmbedBuilder()
                            .setTitle("Alert! Message from " + event.getMessageAuthor().getName() + " at " +
                                    server.getName() + " :-")
                            .setDescription(message)
                            .setColor(getRandomColor())),
                    () -> channel.sendMessage(new EmbedBuilder()
                            .setTitle("Alert! Message from " + event.getMessageAuthor().getName() + ":-")
                            .setDescription(message)
                            .setColor(getRandomColor())));

            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success!")
                    .setDescription("Successfully DM-ed message to user.")
                    .setColor(getRandomColor()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("DM to user failed (Possible reason: User's DMs are closed).")
                    .setColor(getRandomColor()));
        }
    }

    public static void rps(MessageCreateEvent event) {
        String[] args = event.getMessage().getContent().split(" ");
        String actualChoice = args[1];
        char choice = args[1].toLowerCase().charAt(0);
        int intChoice = CurrencyCommands.getRandomInteger(2, 0);
        char[] botChoices = {'r', 'p', 's'};
        char botChoice = botChoices[intChoice];
        int winStatus = getWinStatus(botChoice, choice);
        
        if (winStatus == userWin) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("You win!")
                    .setDescription("You chose " + getChoiceName(choice) + " and bot chose " + getChoiceName(botChoice) + ".")
                    .setColor(getRandomColor()));
        } else if (winStatus == botWin) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Bot wins!")
                    .setDescription("You chose " + getChoiceName(choice) + " and bot chose " + getChoiceName(botChoice) + ".")
                    .setColor(getRandomColor()));
        } else if (winStatus == tie) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Tie!")
                    .setDescription("You chose " + getChoiceName(choice) + " and bot chose " + getChoiceName(botChoice) + ".")
                    .setColor(getRandomColor()));
        } else if (winStatus == error) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You chose " + actualChoice + " and bot chose " + getChoiceName(botChoice) + ".")
                    .setColor(getRandomColor()));
        }
    }

    public static void texttoimg(MessageCreateEvent event) {
        String text;
        try {
            text = event.getMessageContent().substring(5);
        } catch (IndexOutOfBoundsException e) {
            event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Error!")
                .setDescription("No arguments were supplied! To get help about this command, type '>help'.")
                .setColor(getRandomColor()));
            return;
        }
        if (!(text.charAt(0) == ' ')) text = " " + text;
        if (!(text.charAt(text.length() - 1) == ' ')) text += " ";
        
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 48);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();

        event.getChannel().sendMessage(new EmbedBuilder()
            .setTitle("Success!")
            .setDescription("Here is your image.")
            .setImage(img)
            .setColor(getRandomColor()));
    }

    public static void changeUserSettings(MessageCreateEvent event) {
        String[] args = event.getMessage().getContent().split(" ");
        String setting = args[1].toLowerCase(Locale.ROOT);
        String settingValue = args[2].toLowerCase(Locale.ROOT);
        event.getMessageAuthor().asUser().ifPresentOrElse(user -> {
            long id = user.getId();
            if (Objects.equals(settingValue, "true")) {
                UserSettings settings = Main.userSettingsMap.get(id);
                switch (setting) {
                    case "bankdm" -> {
                        settings.setBankDmEnabled(true);
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Enabled bank transaction DMs! Now you WILL be DMed about all your bank transactions.")
                                .setColor(getRandomColor()));
                    }
                    case "passive" -> {
                        settings.setBankPassiveEnabled(true);
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Enabled passive mode! Now NEITHER anyone can rob you, NOR you can rob anyone else.\n" +
                                        "You also CANNOT give money to someone else.")
                                .setColor(getRandomColor()));
                    }
                    default -> {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("Setting type " + args[1] + " not found!")
                                .setColor(getRandomColor()));
                        return;
                    }
                }
                Main.userSettingsMap.replace(id, settings);
                refreshUserSettings();
            } else if (Objects.equals(settingValue, "false")) {
                UserSettings settings = Main.userSettingsMap.get(id);
                switch (setting) {
                    case "bankdm" -> {
                        settings.setBankDmEnabled(false);
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Disabled bank transaction DMs! Now you WON'T be DMed about any of your bank transactions.")
                                .setColor(getRandomColor()));
                    }
                    case "passive" -> {
                        settings.setBankPassiveEnabled(false);
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Disabled passive mode! Now anyone CAN rob you, and you CAN rob anyone else.\n" +
                                        "You also CAN give money to someone else.")
                                .setColor(getRandomColor()));
                    }
                    default -> {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("Setting type " + args[1] + " not found!")
                                .setColor(getRandomColor()));
                        return;
                    }
                }
                Main.userSettingsMap.replace(id, settings);
                refreshUserSettings();
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Incorrect arguments given! Correct syntax: '>setting (type) (true or false)'.\n" +
                                "Example: >setting bankdm false")
                        .setColor(getRandomColor()));
            }
        }, () -> event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Error!")
                .setDescription("You are not a user! You can't use this command.")
                .setColor(getRandomColor())));
    }

    public static void userInfo(MessageCreateEvent event) {
        User user = event.getMessage().getMentionedUsers().size() > 0 ? event.getMessage().getMentionedUsers().get(0) : null;
        event.getChannel().sendMessage(InfoEmbeds.userInfo(event.getMessageAuthor().asUser(), event.getServer(), user));
    }

    public static void serverInfo(MessageCreateEvent event) {
        event.getChannel().sendMessage(InfoEmbeds.serverInfo(event.getServer()));
    }

    // Helper methods
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
    
    public static Color getRandomColor() {
        int red = new Random().nextInt(256);
        int green = new Random().nextInt(256);
        int blue = new Random().nextInt(256);
        return new Color(red, green, blue);
    }

    public static int getWinStatus(char choiceBot, char choiceUser) {
        if (choiceBot == 'r') {
            if (choiceUser == 'r') {
                return tie;
            } else if (choiceUser == 'p') {
                return userWin;
            } else if (choiceUser == 's') {
                return botWin;
            }
        } else if (choiceBot == 'p') {
            if (choiceUser == 'r') {
                return botWin;
            } else if (choiceUser == 'p') {
                return tie;
            } else if (choiceUser == 's') {
                return userWin;
            }
        } else if (choiceBot == 's') {
            if (choiceUser == 'r') {
                return userWin;
            } else if (choiceUser == 'p') {
                return botWin;
            } else if (choiceUser == 's') {
                return tie;
            }
        }
        return error;
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
}

