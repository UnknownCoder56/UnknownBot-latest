package com.uniqueapps.UnknownBot.commands;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        static DataInputStream socketIn;
        static DataOutputStream socketOut;

        public Admes(MessageCreateEvent event, Message message) {
            this.event = event;
            this.message = message;
        }

        public static void initServer() {
            new Thread(() -> {
                try (ServerSocket serverSocket = new ServerSocket(12102)) {
                    System.out.println("Admes server started at port " + serverSocket.getLocalPort() + "!");
                    while (true) {
                        Socket client = serverSocket.accept();
                        System.out.println("New client joined at " + client.getInetAddress() + ", port " + client.getPort());
                        socketIn = new DataInputStream(client.getInputStream());
                        socketOut = new DataOutputStream(client.getOutputStream());
                        new Thread(() -> {
                            while (true) {
                                if (client.isClosed()) {
                                    System.out.println("A client left!");
                                    socketIn = null;
                                    socketOut = null;
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        @Override
        public void run() {
            String text = event.getMessage().getContent();
            String asked = text.substring(7);
            if (event.getServer().isPresent()) {
                System.out.println("'" + event.getMessageAuthor().getName() + "'" + " asked in " + "'" + event.getServer().get().getName()
                        + "'" + ": " + asked);
            }
            if (socketOut != null) {
                try {
                    socketOut.writeUTF("Question: " + asked + "\n\n" +
                            "Reply: ");
                    socketOut.flush();
                    if (socketIn != null) {
                        try {
                            String reply;
                            do {
                                reply = socketIn.readUTF();
                            } while (reply.isEmpty());
                            event.getMessage().reply(new EmbedBuilder()
                                    .setTitle("Reply: " + reply)
                                    .setColor(BasicCommands.getRandomColor()));
                        } catch (IOException e) {
                            e.printStackTrace();
                            event.getMessage().reply(new EmbedBuilder()
                                    .setTitle("Reply: " + "...")
                                    .setColor(BasicCommands.getRandomColor()));
                        }
                    } else {
                        event.getMessage().reply(new EmbedBuilder()
                                .setTitle("Reply: " + "...")
                                .setColor(BasicCommands.getRandomColor()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    event.getMessage().reply(new EmbedBuilder()
                            .setTitle("Reply: " + "...")
                            .setColor(BasicCommands.getRandomColor()));
                }
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
                String searchURL = GOOGLE_SEARCH_URL + "?q=" + search + "&num=" + 10 + "&sourceid=chrome&ie=UTF-8";
                System.out.println("SEARCH: " + searchURL);
                Document doc = Jsoup.connect(searchURL).userAgent("Chrome/70.0.3538.77").get();
                File file = new File("draft.html");
                file.delete();
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(doc.html());
                writer.close();
                new MessageBuilder()
                        .setEmbed(new EmbedBuilder()
                                .setTitle("Here are your results for\n```" + searchURL + "```:-")
                                .setColor(BasicCommands.getRandomColor()))
                        .addAttachment(file)
                        .send(event.getChannel());
            } catch (StringIndexOutOfBoundsException | IOException ex) {
                ex.printStackTrace();
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Incorrect arguments given! Correct syntax: >gsearch (search text)")
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
                String[] args = event.getMessage().getContent().split(" ");
                String text = args[2];
                File file = new File(args[1]);
                file.delete();
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(text);
                writer.close();
                new MessageBuilder()
                        .setEmbed(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Here's your file:-")
                                .setColor(BasicCommands.getRandomColor()))
                        .addAttachment(file)
                        .send(event.getChannel());
            } catch (IndexOutOfBoundsException ex) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Incorrect arguments given! Correct syntax: >makefile (file name) (file content)")
                        .setColor(BasicCommands.getRandomColor()));
            } catch (IOException e) {
                throw new RuntimeException(e);
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
