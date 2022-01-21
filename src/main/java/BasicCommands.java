import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BasicCommands {

    public static Map<String, String> customReplies = new HashMap<>();
    public static final String version = "3.2.0";
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
        String[] args = event.getMessage().getContent().split(" ");
        String category = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : "";
        String[] categories = {"utility", "moderation", "economy"};
        if (Objects.equals(category, categories[0])) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("UnknownBot's Utility commands:-")
                    .addField(">help (category)", "Displays this help message.", true)
                    .addField(">botinfo", "Shows information about UnknownBot.", true)
                    .addField(">hello", "Says hello to you.", true)
                    .addField(">ping", "Displays bot latency.", true)
                    .addField(">admes (query)", "Ask anything to the bot.", true)
                    .addField(">dt", "Shows the current date and time (AM/PM).", true)
                    .addField(">gsearch (search text)", "Searches google and returns results as html.", true)
                    .addField(">makefile (text)", "Creates file from text.", true)
                    .addField(">calc (num1),(sign),(num2) _Warning: No spaces after/before/in commas_", "Does calculation. Supported signs -> +, -, *, /", true)
                    .addField(">reply (text),(reply) _Warning: No spaces after/before/in commas_", "Makes the bot reply when you send a specific text.", true)
                    .addField(">noreply (text)", "Disables custom reply.", true)
                    .addField(">replies", "Displays all custom replies set.", true)
                    .addField(">dm (mention) \"message\"", "DMs a message to a user.", true)
                    .addField(">rps (choice)", "Play \"Rock Paper Scissors\" with the bot. Choices include: ```r, p, s``` or ```rock, paper, scissors```.", true)
                    .setColor(getRandomColor()));
        } else if (Objects.equals(category, categories[1])) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("UnknownBot's Moderation commands:-")
                    .addField(">clear (amount)", "Clears specified number of messages.", true)
                    .addField(">warn (mention) \"cause\"", "Warns a user. _Usage: Type >warn, then mention" +
                            " user, then put reason within quotation marks (\") (no reason supported too). Put space between each" +
                            " argument. Multiple warns supported. Warns are isolated for each server._", true)
                    .addField(">kick (mention)", "Kicks the mentioned user.", true)
                    .addField(">ban (mention)", "Bans the mentioned user.", true)
                    .addField(">mute", "Mutes the mentioned user (Mute = Disable chat and VC).", true)
                    .addField(">nowarns (mention)", "Clear all warns for a user (Individual removal not" +
                            " supported yet).", true)
                    .addField(">unban (mention)", "Unbans the mentioned user.", true)
                    .addField(">unmute", "Unmutes the mentioned user (Mute = Enable chat and VC).", true)
                    .addField(">getwarns (mention)", "Gets all warns for a user.", true)
                    .addField(">nuke", "Cleans everything in a channel.", true)
                    .setColor(getRandomColor()));
        } else if (Objects.equals(category, categories[2])) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("UnknownBot's Economy commands:-")
                    .addField(">bal", "Shows your current bank balance", true)
                    .addField(">bal (mention)", "Shows others' current bank balance", true)
                    .addField(">work", "You work and gain money!", true)
                    .addField(">lb", "Compare and check out richest users of your server!", true)
                    .addField(">glb", "Compare and check richest users of our bot (Global)!", true)
                    .addField(">rob (mention)", "Rob others and get money, the dark way.", true)
                    .addField(">give (amount) (mention)", "Transfer money to others' accounts!", true)
                    .setColor(getRandomColor()));
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
        String[] args = event.getMessage().getContent().split("");
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

    // Helper methods
    public static void refreshReplies() {
        File arrayFile = new File("replyArray.data");
        try {
            arrayFile.delete();
            arrayFile.createNewFile();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(arrayFile))) {
                objectOutputStream.writeObject(customReplies);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

