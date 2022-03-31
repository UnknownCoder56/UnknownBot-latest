package com.uniqueapps.UnknownBot.commands;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.TransactionBody;
import com.uniqueapps.UnknownBot.Main;
import com.uniqueapps.UnknownBot.objects.Help;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class BasicCommands {

    public static Map<String, String> customReplies = new HashMap<>();
    public static final String version = "3.4.5";
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

    public static void gsearch(MessageCreateEvent event) {
        AsyncCommands.Gsearch gsearch = new AsyncCommands.Gsearch(event);
        CompletableFuture<AsyncCommands.Gsearch> completableFuture = CompletableFuture
                .supplyAsync(() -> gsearch);

        completableFuture
                .thenApplyAsync(gsearch1 -> {
                    gsearch1.run();
                    return gsearch1;
                });
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
                    case ("+"):
                        result = num1 + num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                        break;
                    case ("-"):
                        result = num1 - num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                        break;
                    case ("*"):
                        result = num1 * num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                        break;
                    case ("/"):
                        result = num1 / num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                        break;
                    default:
                        reply = "Not a valid operation symbol. Valid ones are +, -, * and /.";
                        break;
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

    public static void clearMessages(MessageCreateEvent event) {
        AsyncCommands.Clear clear = new AsyncCommands.Clear(event);
        CompletableFuture<AsyncCommands.Clear> completableFuture = CompletableFuture
                .supplyAsync(() -> clear);

        completableFuture
                .thenApplyAsync(clear1 -> {
                    clear1.run();
                    return clear1;
                });
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
        		.setTitle("Current Time (UTC)")
        		.setDescription(LocalDateTime.now(ZoneId.of("UTC")).format(
        				new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a").toFormatter()).toUpperCase())
        		.setColor(getRandomColor()));
    }

    public static void help(MessageCreateEvent event) {
        Help help = new Help();
        String[] args = event.getMessage().getContent().split(" ");
        String category = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "";
        String[] categories = {"utility", "moderation", "economy"};
        if (Objects.equals(category, categories[0])) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("UnknownBot's Utility commands:-")
                    .setColor(getRandomColor());

            for (String key : help.utilityCommands.keySet()) {
                embedBuilder.addField(key, help.utilityCommands.get(key), true);
            }

            event.getChannel().sendMessage(embedBuilder);
        } else if (Objects.equals(category, categories[1])) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("UnknownBot's Moderation commands:-")
                    .setColor(getRandomColor());
    
            for (String key : help.moderationCommands.keySet()) {
                embedBuilder.addField(key, help.moderationCommands.get(key), true);
            }
    
            event.getChannel().sendMessage(embedBuilder);
        } else if (Objects.equals(category, categories[2])) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("UnknownBot's Economy commands:-")
                    .setColor(getRandomColor());
    
            for (String key : help.economyCommands.keySet()) {
                embedBuilder.addField(key, help.economyCommands.get(key), true);
            }
    
            event.getChannel().sendMessage(embedBuilder);
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("UnknownBot's Help docs")
                    .setDescription("UnknownBot is a multipurpose bot, currently under active development.\n" +
                                    "To get help about commands, type \">help (category)\", where categories include:-\n" +
                                    "\n" +
                                    "1) Utility ```>help utility```\n" +
                                    "2) Moderation ```>help moderation```\n" +
                                    "3) Economy ```>help economy```\n" +
                                    "\n" +
                                    "For more information on the bot, how to use it, or queries about, contact us on our official discord server:-\n" +
                                    "https://discord.gg/t79ZyuHr5K")
                    .setColor(getRandomColor()));
        }
    }

    public static void botinfo(MessageCreateEvent event) {
		try {
            EmbedBuilder embed;
            StringBuilder stringBuilder = new StringBuilder();

            if (event.getServer().isPresent()) {
                if (!event.getServer().get().getRoles(Main.api.getYourself()).isEmpty()) {
                    for (Role role : event.getServer().get().getRoles(Main.api.getYourself())) {
                        stringBuilder.append(role.getMentionTag()).append("\n");
                    }
                } else {
                    stringBuilder.append("None");
                }
            } else {
                stringBuilder.append("None");
            }

			embed = new EmbedBuilder()
			        .setTitle("UnknownBot Status:-")
			        .addField("Server count", String.valueOf(Main.api.getServers().size()), true)
			        .addField("User count", String.valueOf(Main.api.getCachedUsers().size()), true)
			        .addField("Ping", "\nRest ping: " + TimeUnit.NANOSECONDS.toMillis(Main.api.measureRestLatency().get().getNano()) + " ms" +
			                "\nGateway ping: " + TimeUnit.NANOSECONDS.toMillis(Main.api.getLatestGatewayLatency().getNano()) + " ms", true)
                    .addField("Roles", stringBuilder.toString(), true)
			        .addField("Invite Link", Main.api.createBotInvite(Permissions.fromBitmask(PermissionType.ADMINISTRATOR.getValue())), true)
			        .addField("Version", version, true)
			        .addField("Bot type", "Utility, Moderation and Economy Bot", true)
			        .addField("Developer", "\uD835\uDE50\uD835\uDE63\uD835\uDE60\uD835\uDE63\uD835\uDE64\uD835\uDE6C\uD835\uDE63\uD835\uDE4B\uD835\uDE67\uD835\uDE64 56#9802", true)
			        .setColor(getRandomColor());
			
			event.getChannel().sendMessage(embed);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
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
            if (event.getServer().isPresent()) {
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("Alert! Message from " + event.getMessageAuthor().getName() + " at " +
                                event.getServer().get().getName() + " :-")
                        .setDescription(message));
            } else {
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("Alert! Message from " + event.getMessageAuthor().getName() + ":-")
                        .setDescription(message));
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success!")
                    .setDescription("Successfully DM-ed message to user."));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("DM to user failed (Main reason: User's DMs are closed)."));
        }
    }

    public static void nuke(MessageCreateEvent event) {
        AsyncCommands.Nuke nuke = new AsyncCommands.Nuke(event);
        CompletableFuture<AsyncCommands.Nuke> completableFuture = CompletableFuture
                .supplyAsync(() -> nuke);

        completableFuture
                .thenApplyAsync(nuke1 -> {
                    nuke1.run();
                    return nuke1;
                });
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
                .setDescription("You chose " + getChoiceName(choice) + " and bot chose " + getChoiceName(botChoice) + "."));
        } else if (winStatus == botWin) {
            event.getChannel().sendMessage(new EmbedBuilder()
            .setTitle("Bot wins!")
            .setDescription("You chose " + getChoiceName(choice) + " and bot chose " + getChoiceName(botChoice) + "."));
        } else if (winStatus == tie) {
            event.getChannel().sendMessage(new EmbedBuilder()
            .setTitle("Tie!")
            .setDescription("You chose " + getChoiceName(choice) + " and bot chose " + getChoiceName(botChoice) + "."));
        } else if (winStatus == error) {
            event.getChannel().sendMessage(new EmbedBuilder()
            .setTitle("Error!")
            .setDescription("You chose " + actualChoice + " and bot chose " + getChoiceName(botChoice) + "."));
        }
    }

    public static void texttoimg(MessageCreateEvent event) {
        String text = "";
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
                    collection.insertOne(doc);
                    return "Updated replies!";
                };
    
                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }
    
    public static Color getRandomColor() {
    	Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.ORANGE, Color.PINK, Color.GRAY,
        		Color.MAGENTA, Color.YELLOW, Color.DARK_GRAY, Color.WHITE, Color.BLACK, Color.LIGHT_GRAY};
        int choice = new Random().nextInt(colors.length);
        return colors[choice];
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

