package com.uniqueapps.UnknownBot.commands;

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
import com.uniqueapps.UnknownBot.Main;
import com.uniqueapps.UnknownBot.objects.Warn;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class ModCommands {

    public static Map<Long, Map<Long, Warn>> warnMap = new HashMap<>();

    public static void kick(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canKickUser(Main.api.getYourself(), event.getMessage().getMentionedUsers().get(0))) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.kickUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully kicked user " + user.getDiscriminatedName() + ".")
                            .setColor(BasicCommands.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't kick that user! Reasons - No kick permission, lower role or" +
                                    "lower position.")
                            .setColor(BasicCommands.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void ban(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canBanUser(Main.api.getYourself(), event.getMessage().getMentionedUsers().get(0))) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.banUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully banned user " + user.getDiscriminatedName() + ".")
                            .setColor(BasicCommands.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't ban that user! Reasons - No ban permission, lower role or" +
                                    "lower position.")
                            .setColor(BasicCommands.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void mute(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canMuteMembers(Main.api.getYourself())) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.muteUser(user);
                    server.deafenUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully muted user " + user.getDiscriminatedName() + ".")
                            .setColor(BasicCommands.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't mute that user! Reasons - No mute permission, lower role or" +
                                    "lower position.")
                            .setColor(BasicCommands.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void unban(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canBanUser(Main.api.getYourself(), event.getMessage().getMentionedUsers().get(0))) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.unbanUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully unbanned user " + user.getDiscriminatedName() + ".")
                            .setColor(BasicCommands.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't unban that user! Reasons - No unban permission, lower role or" +
                                    "lower position.")
                            .setColor(BasicCommands.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void unmute(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canMuteMembers(Main.api.getYourself())) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.unmuteUser(user);
                    server.undeafenUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully unmuted user " + user.getDiscriminatedName() + ".")
                            .setColor(BasicCommands.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't unmute that user! Reasons - No unmute permission, lower role or" +
                                    "lower position.")
                            .setColor(BasicCommands.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void warn(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            String cause = StringUtils.substringBetween(event.getMessage().getContent(), "\"", "\"");
            User user = event.getMessage().getMentionedUsers().get(0);
            Server server = event.getServer().get();
            if (!warnMap.containsKey(server.getId())) {
                warnMap.put(server.getId(), new HashMap<>());
                if (!warnMap.get(server.getId())
                        .containsKey(user.getId())) {
                    Warn warn = new Warn(cause, user.getId());
                    warnMap.get(server.getId())
                            .put(warn.getUserId(), warn);
                } else {
                    warnMap.get(server.getId())
                            .get(user.getId()).newWarn(cause);
                }
            } else {
                if (!warnMap.get(server.getId())
                        .containsKey(user.getId())) {
                    Warn warn = new Warn(cause, user.getId());
                    warnMap.get(event.getServer().get().getId())
                            .put(warn.getUserId(), warn);
                } else {
                    warnMap.get(server.getId())
                            .get(user.getId()).newWarn(cause);
                }
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success!")
                    .setDescription("Successfully warned " + user.getDiscriminatedName() + " for cause:\n" +
                            cause + "\nHe/She now has " + warnMap.get(event.getServer().get().getId()).get(user.getId()).warns + " warn(s).")
                    .setColor(BasicCommands.getRandomColor()));
            if (user.getPrivateChannel().isPresent()) {
                if (event.getServer().isPresent()) {
                    user.getPrivateChannel().get().sendMessage(new EmbedBuilder()
                            .setTitle("Alert!")
                            .setDescription("You have been warned in **" + event.getServer().get().getName() + "** for reason: **" + cause +
                                    "**. You now have **" + warnMap.get(event.getServer().get().getId()).get(user.getId()).warns + "** warn(s) in that server.")
                            .setColor(BasicCommands.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("This is not a server!"));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Alert!")
                        .setDescription("Failed to DM user, but warn was successful.")
                        .setColor(BasicCommands.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(BasicCommands.getRandomColor()));
        }
        refreshWarns();
    }

    public static void clearWarn(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            User user = event.getMessage().getMentionedUsers().get(0);
            if (event.getServer().isPresent()) {
                if (warnMap.containsKey(event.getServer().get().getId())) {
                    warnMap.get(event.getServer().get().getId())
                            .remove(user.getId());
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully removed all warnings for " + user.getDiscriminatedName() + "!")
                            .setColor(BasicCommands.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("No warns were found for this user!")
                            .setColor(BasicCommands.getRandomColor()));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("No warns were found for this user!")
                        .setColor(BasicCommands.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(BasicCommands.getRandomColor()));
        }
        refreshWarns();
    }

    public static void getWarns(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            User user = event.getMessage().getMentionedUsers().get(0);
            StringBuilder stringBuilder = new StringBuilder();
            if (event.getServer().isPresent()) {
                if (warnMap.containsKey(event.getServer().get().getId())) {
                    if (warnMap.get(event.getServer().get().getId()).containsKey(user.getId())) {
                        Warn warn = warnMap.get(event.getServer().get().getId())
                                .get(user.getId());
                        for (String warnCause : warn.warnCauses) {
                            stringBuilder.append(warnCause).append("\n");
                        }
                    } else {
                        stringBuilder.append("No warns were found for this user!");
                    }
                } else {
                    stringBuilder.append("No warns were found for this user!");
                }
            } else {
                stringBuilder.append("No warns were found for this user!");
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Warns for " + user.getDiscriminatedName() + ":-")
                    .setDescription(stringBuilder.toString())
                    .setColor(BasicCommands.getRandomColor()));
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void refreshWarns() {
        new Thread(() -> {
            try (MongoClient client = MongoClients.create(Main.settings); ClientSession session = client.startSession()) {
                TransactionBody<String> txnBody = () -> {
                    MongoCollection<Document> collection = client.getDatabase("UnknownDatabase").getCollection("UnknownCollection");
                    List<Document> docs = new ArrayList<>();
                    for (Map<Long, Warn> map : warnMap.values()) {
                        List<Document> warns = new ArrayList<>();
                        for (Warn warn : map.values()) {
                            Document document = new Document()
                                    .append("id", warn.getUserId())
                                    .append("warns", warn.getWarns())
                                    .append("causes", warn.getWarnCauses());
                            warns.add(document);
                        }
                        docs.add(new Document().append("key", map.keySet()).append("val", warns));
                    }
                    Document doc = new Document()
                            .append("name", "warn")
                            .append("key", warnMap.keySet())
                            .append("val", docs);
                    if (collection.countDocuments(Filters.eq("name", "warn")) > 0) {
                        collection.replaceOne(Filters.eq("name", "warn"), doc);
                    } else {
                        collection.insertOne(doc);
                    }
                    return "Updated warns!";
                };
    
                System.out.println(session.withTransaction(txnBody));
            }
        }).start();
    }
}
