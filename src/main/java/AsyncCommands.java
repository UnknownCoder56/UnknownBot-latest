import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class AsyncCommands {
    public static class Clear implements Runnable {

        MessageCreateEvent event;

        public Clear(MessageCreateEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            if (event.getMessageAuthor().isServerAdmin()) {
                String[] args = event.getMessage().getContent().split(" ");
                try {
                    int messagesToClear = Integer.parseInt(args[1]);
                    MessageSet messages = event.getChannel().getMessages(messagesToClear + 1).get();
                    event.getChannel().deleteMessages(messages);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription(event.getMessageAuthor().getName() + " cleared " + (messages.size() - 1) + " message(s) in: " + (event.getServerTextChannel().isPresent() ? event.getServerTextChannel().get().getMentionTag() : null))
                            .setColor(BasicCommands.getRandomColor()));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("Supplied argument is not a number or is too large!")
                            .setColor(BasicCommands.getRandomColor()));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You don't have admin perms, so you cannot use mod commands!")
                        .setColor(BasicCommands.getRandomColor()));
            }
        }
    }

    public static class Admes implements Runnable {

        MessageCreateEvent event;
        Message message;

        public Admes(MessageCreateEvent event, Message message) {
            this.event = event;
            this.message = message;
        }

        @Override
        public void run() {
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
                event.getMessage().reply(new EmbedBuilder()
                        .setTitle("Reply: " + text2)
                        .setColor(BasicCommands.getRandomColor()));
            } else {
                event.getMessage().reply(new EmbedBuilder()
                        .setTitle("Reply: " + "...")
                        .setColor(BasicCommands.getRandomColor()));
            }
        }
    }

    public static class Gsearch implements Runnable {

        public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
        MessageCreateEvent event;

        public Gsearch(MessageCreateEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
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
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Here are your results:- " + searchURL)
                        .setColor(BasicCommands.getRandomColor()));
                event.getChannel().sendMessage(file);
            } catch (StringIndexOutOfBoundsException | IOException ex) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Please type search text after typing '>gsearch' and " +
                                "put space between command and text.")
                        .setColor(BasicCommands.getRandomColor()));
            }
        }
    }

    public static class Makefile implements Runnable {

        MessageCreateEvent event;

        public Makefile(MessageCreateEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
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
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Please type text after typing '>makefile' and " +
                                "put space between command and text.")
                        .setColor(BasicCommands.getRandomColor()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Nuke implements Runnable {

        MessageCreateEvent event;

        public Nuke(MessageCreateEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            if (event.getMessageAuthor().isServerAdmin()) {
                if (event.getServerTextChannel().isPresent() && event.getServer().isPresent()) {
                    ServerTextChannel channel = event.getServerTextChannel().get();
                    channel.delete();
                    try {
                        ServerTextChannel serverTextChannel = event.getServer().get().createTextChannelBuilder()
                                .setName(channel.getName())
                                .setCategory((channel.getCategory().isPresent() ? channel.getCategory().get() : null))
                                .setTopic(channel.getTopic())
                                .setSlowmodeDelayInSeconds(channel.getSlowmodeDelayInSeconds())
                                .create().get();
                        serverTextChannel.sendMessage(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Successfully nuked this channel (Nuked By: " +
                                        event.getMessageAuthor().getName() + ").")
                                .setColor(BasicCommands.getRandomColor()));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You don't have admin perms, so you cannot use mod commands!")
                        .setColor(BasicCommands.getRandomColor()));
            }
        }
    }
}
