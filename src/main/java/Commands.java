import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Commands {

    public static Map<String, String> customReplies = new HashMap<>();
    public static final String version = "2.0.0";

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
                switch (argsList.get(1)) {
                    case ("+") -> {
                        float result = num1 + num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                    }
                    case ("-") -> {
                        float result = num1 - num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                    }
                    case ("*") -> {
                        float result = num1 * num2;
                        reply = num1 + " " + argsList.get(1) + " " + num2 + " = " + result;
                    }
                    case ("/") -> {
                        float result = num1 / num2;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void hello(MessageCreateEvent event) {
        event.getChannel().sendMessage(new EmbedBuilder()
        		.setTitle("Hello!")
        		.setDescription("Hello There, " + event.getMessageAuthor().getName() + "! UnknownBot"
        				+ " here at your service. Type '>help for a list of supported commands.")
        		.setColor(getRandomColor()));
    }

    public static void dt(MessageCreateEvent event) {
        event.getChannel().sendMessage(new EmbedBuilder()
        		.setTitle("Current System Time (IST)")
        		.setDescription(LocalDateTime.now().format(
        				new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a").toFormatter()).toUpperCase())
        		.setColor(getRandomColor()));
    }

    public static void help(MessageCreateEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("UnknownBot Commands:-")
                .setAuthor("UnknownBot")
                .addField(">help", "Displays this help message.", true)
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
                .addField(">clear (amount)", "Clears specified number of messages.", true)
                .setColor(getRandomColor())
                .setTimestamp(Instant.now());

        event.getChannel().sendMessage(embed);
    }

    public static void botinfo(MessageCreateEvent event) {
        EmbedBuilder embed;
		try {
			embed = new EmbedBuilder()
			        .setTitle("UnknownBot Status:-")
			        .addField("Server count", String.valueOf(Main.api.getServers().size()), true)
			        .addField("User count", String.valueOf(Main.api.getCachedUsers().size()), true)
			        .addField("Ping", "\nRest ping: " + TimeUnit.NANOSECONDS.toMillis(Main.api.measureRestLatency().get().getNano()) + " ms" +
			                "\nGateway ping: " + TimeUnit.NANOSECONDS.toMillis(Main.api.getLatestGatewayLatency().getNano()) + " ms", true)
			        .addField("Invite Link", Main.api.createBotInvite(Permissions.fromBitmask(PermissionType.ADMINISTRATOR.getValue())), true)
			        .addField("Version", version, true)
			        .addField("Bot type", "Utility and Fun Bot", true)
			        .addField("Developer", "\uD835\uDE50\uD835\uDE63\uD835\uDE60\uD835\uDE63\uD835\uDE64\uD835\uDE6C\uD835\uDE63\uD835\uDE4B\uD835\uDE67\uD835\uDE64 56#9802", true)
			        .setColor(getRandomColor());
			
			event.getChannel().sendMessage(embed);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void replies(MessageCreateEvent event) {
        StringBuilder repl = new StringBuilder();
        for (String reply : customReplies.keySet()) {
            repl.append(reply).append(": ").append(customReplies.get(reply)).append("\n");
        }
        event.getChannel().sendMessage(new EmbedBuilder()
        		.setTitle("Currently set custom replies:-")
        		.setDescription(repl.toString())
        		.setColor(getRandomColor()));
    }
    
    public static void refreshReplies() {
        File arrayFile = new File("C:\\Users\\Arpan\\OneDrive\\Desktop\\Bot Files\\replyArray.data");
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
        		Color.MAGENTA, Color.YELLOW};
        int choice = new Random().nextInt(colors.length);
        return colors[choice];
    }
}

