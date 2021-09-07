import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Commands {

    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    public static Map<String, String> customReplies = new HashMap<>();

    public static void admes(MessageCreateEvent event) {
        try {
            event.getChannel().sendMessage("Info: Bot will be halted until it replies to you.");
            String text = event.getMessage().getContent();
            String asked = text.substring(7);
            if (event.getServer().isPresent()) {
                System.out.println("'" + event.getMessageAuthor().getName() + "'" + " asked in " + "'" + event.getServer().get().getName()
                        + "'" + ": " + asked);
            }
            @SuppressWarnings("resource")
			Scanner scn = new Scanner(System.in);
            System.out.println("Reply: ");
            if (scn.hasNextLine()) {
                String text2 = scn.nextLine();
                event.getChannel().sendMessage("Reply: " + text2);
            } else {
                event.getChannel().sendMessage("Reply: " + "...");
            }
        } catch (StringIndexOutOfBoundsException ex) {
            event.getChannel().sendMessage("Please ask something after typing '>admes' and " +
                    "put space between command and asking.");
        }
    }

    public static void gsearch(MessageCreateEvent event) {
        try {
            String search = URLEncoder.encode(event.getMessage().getContent().substring(9), StandardCharsets.UTF_8);
            String searchURL = GOOGLE_SEARCH_URL + "?q=" + search + "&num=" + 10;
            System.out.println("SEARCH: " + searchURL);
            Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
            File file = new File("C:\\Users\\Arpan\\OneDrive\\Desktop\\Bot Files\\draft.html");
            file.delete();
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(doc.html());
            writer.close();
            event.getChannel().sendMessage("Here are your results:- " + searchURL);
            event.getChannel().sendMessage(file);
        } catch (StringIndexOutOfBoundsException | IOException ex) {
            event.getChannel().sendMessage("Please type search text after typing '>gsearch' and " +
                    "put space between command and text.");
        }
    }

    public static void makefile(MessageCreateEvent event) {
        try {
            String text = event.getMessage().getContent().substring(10);
            File file = new File("C:\\Users\\Arpan\\OneDrive\\Desktop\\Bot Files\\text.txt");
            file.delete();
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(text);
            writer.close();
            event.getChannel().sendMessage(file);
        } catch (StringIndexOutOfBoundsException ex) {
            event.getChannel().sendMessage("Please type text after typing '>makefile' and " +
                    "put space between command and text.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void calc(MessageCreateEvent event) {
        String[] text = event.getMessage().getContent().split(",");
        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(text));
        String num = argsList.get(0).substring(6);
        argsList.set(0, num);
        System.out.println("Args: " + argsList);
        try {
            switch (argsList.get(1)) {
                case ("+") -> {
                    float num1 = Float.parseFloat(argsList.get(0));
                    float num2 = Float.parseFloat(argsList.get(2));
                    float result = num1 + num2;
                    event.getChannel().sendMessage(num1 + " " + argsList.get(1) + " " + num2 + " = " + result);
                }
                case ("-") -> {
                    float num1 = Float.parseFloat(argsList.get(0));
                    float num2 = Float.parseFloat(argsList.get(2));
                    float result = num1 - num2;
                    event.getChannel().sendMessage(num1 + " " + argsList.get(1) + " " + num2 + " = " + result);
                }
                case ("*") -> {
                    float num1 = Float.parseFloat(argsList.get(0));
                    float num2 = Float.parseFloat(argsList.get(2));
                    float result = num1 * num2;
                    event.getChannel().sendMessage(num1 + " " + argsList.get(1) + " " + num2 + " = " + result);
                }
                case ("/") -> {
                    float num1 = Float.parseFloat(argsList.get(0));
                    float num2 = Float.parseFloat(argsList.get(2));
                    float result = num1 / num2;
                    event.getChannel().sendMessage(num1 + " " + argsList.get(1) + " " + num2 + " = " + result);
                }
                default -> event.getChannel().sendMessage("Not a valid operation symbol. Valid ones are +, -, * and /.");
            }
        } catch (NumberFormatException ex) {
            event.getChannel().sendMessage("Please provide a number, as '>calc (num1),(+ or - or * or /),(num2)'.");
        } catch (IndexOutOfBoundsException ex) {
            event.getChannel().sendMessage("Incorrect number of arguments given!");
        } catch (Exception ex) {
            event.getChannel().sendMessage("Unknown Error Occured! Please report this to the developer (UnknownPro56).\n" +
            "Error: " + ex.getMessage());
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
                event.getChannel().sendMessage("Successfully set custom reply! Bot will now reply with '"
                + customReplies.get(argsList.get(0)) + "' when any message contains '" + argsList.get(0) + "'.");
            	refreshReplies();
            } else {
                event.getChannel().sendMessage("No arguments given! Correct syntax - >reply (text),(reply) _No spaces between commas and arguments!");
            }
        } catch (IndexOutOfBoundsException ex) {
            event.getChannel().sendMessage("Incorrect arguments given. Correct syntax - >reply (text),(reply) _No spaces between commas and arguments!");
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
                event.getChannel().sendMessage("Successfully disabled custom reply " + text + "!");
                return;
            }
        }
        event.getChannel().sendMessage("No reply named '" + event.getMessage().getContent().substring(9) + "' was found!");
    }

    public static void clearMessages(MessageCreateEvent event) {
        Clear clear = new Clear(event);
        CompletableFuture<Clear> completableFuture = CompletableFuture
                .supplyAsync(() -> clear);

        completableFuture
                .thenApplyAsync(clear1 -> {
                    clear1.run();
                    return clear1;
                });
    }

    public static void ping(MessageCreateEvent event) {
        event.getChannel().sendMessage("Pong! Latency is " + Main.api.measureRestLatency() + " ms");
    }

    public static void hello(MessageCreateEvent event) {
        event.getChannel().sendMessage("Hello, " + event.getMessageAuthor().getName() + "!");
    }

    public static void dt(MessageCreateEvent event) {
        event.getChannel().sendMessage(LocalDateTime.now().format(
                new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a").toFormatter()));
    }

    public static void help(MessageCreateEvent event) {
        ArrayList<Role> roles = new ArrayList<>(Main.api.getRoles());
        Color color;
        if (roles.get(0).getColor().isPresent()) {
            color = roles.get(0).getColor().get();
        } else {
            color = Color.RED;
        }
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
                .setColor(color)
                .setTimestamp(Instant.now());

        event.getChannel().sendMessage(embed);
    }

    public static void botinfo(MessageCreateEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("UnknownBot Status:-")
                .addField("Server count", String.valueOf(Main.api.getServers().size()), true)
                .addField("User count", String.valueOf(Main.api.getCachedUsers().size()), true)
                .addField("Ping", "\nRest ping: " + Main.api.measureRestLatency() +
                        "\nGateway ping: " + Main.api.getLatestGatewayLatency(), true)
                .addField("Invite Link", Main.api.createBotInvite(Permissions.fromBitmask(PermissionType.ADMINISTRATOR.getValue())), true)
                .addField("Version", "1.5.0", true)
                .addField("Bot type", "Utility and Fun Bot", true)
                .addField("Developer", "\uD835\uDE50\uD835\uDE63\uD835\uDE60\uD835\uDE63\uD835\uDE64\uD835\uDE6C\uD835\uDE63\uD835\uDE4B\uD835\uDE67\uD835\uDE64 56#9802", true);

        event.getChannel().sendMessage(embed);
    }

    public static void replies(MessageCreateEvent event) {
        StringBuilder repl = new StringBuilder();
        for (String reply : customReplies.keySet()) {
            repl.append(reply).append(": ").append(customReplies.get(reply)).append("\n");
        }
        event.getChannel().sendMessage("Currently set custom replies:-\n" + repl);
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
}

class Clear implements Runnable {

    MessageCreateEvent event;

    @Override
    public void run() {
        String[] args = event.getMessage().getContent().split(" ");
        try {
            int messagesToClear = Integer.parseInt(args[1]);
            MessageSet messages = event.getChannel().getMessages(messagesToClear + 1).get();
            event.getChannel().deleteMessages(messages);
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle(event.getMessageAuthor().getName() + " cleared " + (messages.size() - 1) + " message(s) in: " + event.getChannel()));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("Supplied argument is not a number or is too large!");
        }
    }

    public Clear(MessageCreateEvent event1) {
        event = event1;
    }
}
