package com.uniqueapps.unknownbot.commands;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import com.uniqueapps.unknownbot.Helper;
import com.uniqueapps.unknownbot.Main;
import com.uniqueapps.unknownbot.objects.Warn;

import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class ModCommands {

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
                            .setColor(Helper.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't kick that user! Reasons - No kick permission, lower role or" +
                                    "lower position.")
                            .setColor(Helper.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(Helper.getRandomColor()));
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
                            .setColor(Helper.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't ban that user! Reasons - No ban permission, lower role or" +
                                    "lower position.")
                            .setColor(Helper.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(Helper.getRandomColor()));
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
                            .setColor(Helper.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't mute that user! Reasons - No mute permission, lower role or" +
                                    "lower position.")
                            .setColor(Helper.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(Helper.getRandomColor()));
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
                            .setColor(Helper.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't unban that user! Reasons - No unban permission, lower role or" +
                                    "lower position.")
                            .setColor(Helper.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(Helper.getRandomColor()));
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
                            .setColor(Helper.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't unmute that user! Reasons - No unmute permission, lower role or" +
                                    "lower position.")
                            .setColor(Helper.getRandomColor()));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(Helper.getRandomColor()));
        }
    }

    public static void warn(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            String cause = StringUtils.substringBetween(event.getMessage().getContent(), "\"", "\"");
            User user = event.getMessage().getMentionedUsers().get(0);
            Server server = event.getServer().get();
            if (!Helper.warnMap.containsKey(server.getId())) {
                Helper.warnMap.put(server.getId(), new HashMap<>());
                if (!Helper.warnMap.get(server.getId())
                        .containsKey(user.getId())) {
                    Warn warn = new Warn(cause, user.getId());
                    Helper.warnMap.get(server.getId())
                            .put(warn.getUserId(), warn);
                } else {
                    Helper.warnMap.get(server.getId())
                            .get(user.getId()).newWarn(cause);
                }
            } else {
                if (!Helper.warnMap.get(server.getId())
                        .containsKey(user.getId())) {
                    Warn warn = new Warn(cause, user.getId());
                    Helper.warnMap.get(event.getServer().get().getId())
                            .put(warn.getUserId(), warn);
                } else {
                    Helper.warnMap.get(server.getId())
                            .get(user.getId()).newWarn(cause);
                }
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success!")
                    .setDescription("Successfully warned " + user.getDiscriminatedName() + " for cause:\n" +
                            cause + "\nThey now have " + Helper.warnMap.get(event.getServer().get().getId()).get(user.getId()).warns + " warn(s).")
                    .setColor(Helper.getRandomColor()));
            if (user.getPrivateChannel().isPresent()) {
                if (event.getServer().isPresent()) {
                    user.getPrivateChannel().get().sendMessage(new EmbedBuilder()
                            .setTitle("Alert!")
                            .setDescription("You have been warned in **" + event.getServer().get().getName() + "** for reason: **" + cause +
                                    "**. You now have **" + Helper.warnMap.get(event.getServer().get().getId()).get(user.getId()).warns + "** warn(s) in that server.")
                            .setColor(Helper.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("This is not a server!"));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Alert!")
                        .setDescription("Failed to DM user, but warn was successful.")
                        .setColor(Helper.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(Helper.getRandomColor()));
        }
        Helper.refreshWarns();
    }

    public static void clearWarn(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            User user = event.getMessage().getMentionedUsers().get(0);
            if (event.getServer().isPresent()) {
                if (Helper.warnMap.containsKey(event.getServer().get().getId())) {
                    Helper.warnMap.get(event.getServer().get().getId())
                            .remove(user.getId());
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully removed all warnings for " + user.getDiscriminatedName() + "!")
                            .setColor(Helper.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("No warns were found for this user!")
                            .setColor(Helper.getRandomColor()));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("No warns were found for this user!")
                        .setColor(Helper.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(Helper.getRandomColor()));
        }
        Helper.refreshWarns();
    }

    public static void getWarns(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            User user = event.getMessage().getMentionedUsers().get(0);
            StringBuilder stringBuilder = new StringBuilder();
            if (event.getServer().isPresent()) {
                if (Helper.warnMap.containsKey(event.getServer().get().getId())) {
                    if (Helper.warnMap.get(event.getServer().get().getId()).containsKey(user.getId())) {
                        Warn warn = Helper.warnMap.get(event.getServer().get().getId())
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
                    .setColor(Helper.getRandomColor()));
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!")
                    .setColor(Helper.getRandomColor()));
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
}
