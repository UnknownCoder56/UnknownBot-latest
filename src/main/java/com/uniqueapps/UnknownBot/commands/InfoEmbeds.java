package com.uniqueapps.UnknownBot.commands;

import com.uniqueapps.UnknownBot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class InfoEmbeds {

    public static EmbedBuilder botInfo(Optional<Server> serverOptional) {
        try {
            EmbedBuilder embed;
            StringBuilder stringBuilder = new StringBuilder();
            AtomicBoolean isBotServerAdmin = new AtomicBoolean(false);

            serverOptional.ifPresentOrElse(server -> {
                if (!server.getRoles(Main.api.getYourself()).isEmpty()) {
                    for (Role role : server.getRoles(Main.api.getYourself())) {
                        if (role.getAllowedPermissions().contains(PermissionType.ADMINISTRATOR)) {
                            isBotServerAdmin.set(true);
                        }
                        stringBuilder.append(role.getMentionTag()).append("\n");
                    }
                } else {
                    stringBuilder.append("None");
                }
            }, () -> stringBuilder.append("None"));

            embed = new EmbedBuilder()
                    .setTitle("UnknownBot Status:-")
                    .addField("Server count", String.valueOf(Main.api.getServers().size()), true)
                    .addField("User count", String.valueOf(Main.api.getCachedUsers().size()), true)
                    .addField("Ping", "\nRest ping: " + TimeUnit.NANOSECONDS.toMillis(Main.api.measureRestLatency().get().getNano()) + " ms" +
                            "\nGateway ping: " + TimeUnit.NANOSECONDS.toMillis(Main.api.getLatestGatewayLatency().getNano()) + " ms", true)
                    .addField("Roles", stringBuilder.toString(), true)
                    .addField("Is bot admin?", isBotServerAdmin.get() ? "Yes" : "No", true)
                    .addField("Invite Link", Main.api.createBotInvite(Permissions.fromBitmask(PermissionType.ADMINISTRATOR.getValue())), true)
                    .addField("Version", BasicCommands.version, true)
                    .addField("Bot website", "https://user783667580106702848.pepich.de/", true)
                    .addField("Bot type", "Utility, Moderation and Economy Bot", true)
                    .addField("Developer", "\uD835\uDE50\uD835\uDE63\uD835\uDE60\uD835\uDE63\uD835\uDE64\uD835\uDE6C\uD835\uDE63\uD835\uDE4B\uD835\uDE67\uD835\uDE64 56#9802", true)
                    .setColor(BasicCommands.getRandomColor());

            return embed;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static EmbedBuilder userInfo(Optional<User> authorOptional, Optional<Server> serverOptional, User maybeUser) {
        AtomicReference<EmbedBuilder> embedBuilder = new AtomicReference<>();
        authorOptional.ifPresentOrElse(author -> {
            User user = maybeUser != null ? maybeUser : author;

            StringBuilder stringBuilder = new StringBuilder();
            AtomicBoolean isUserServerAdmin = new AtomicBoolean(false);

            serverOptional.ifPresentOrElse((server) -> {
                if (!server.getRoles(user).isEmpty()) {
                    var roles = server.getRoles(user);
                    Collections.reverse(roles);
                    for (Role role : roles) {
                        if (role.getAllowedPermissions().contains(PermissionType.ADMINISTRATOR)) {
                            isUserServerAdmin.set(true);
                        }
                        stringBuilder.append(role.getMentionTag()).append("\n");
                    }
                } else {
                    stringBuilder.append("None");
                }
            }, () -> stringBuilder.append("None"));

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Information about " + (serverOptional.isPresent() ? user.getDisplayName(serverOptional.get()) : user.getDiscriminatedName()) + ":-")
                    .setImage(serverOptional.isPresent() ? user.getEffectiveAvatar(serverOptional.get()) : user.getAvatar())
                    .addField("Discriminated name: ", user.getDiscriminatedName(), true)
                    .addField("Account id: ", user.getIdAsString(), true)
                    .addField("Is bot?: ", user.isBot() ? "Yes" : "No", true)
                    .addField("Account created: ", user.getCreationTimestamp().atZone(ZoneId.of("UTC")).toLocalDateTime().format(
                            new DateTimeFormatterBuilder()
                                    .appendPattern("dd MMMM yyyy hh:mm:ss")
                                    .toFormatter()) + " (UTC)", true);

            serverOptional.flatMap(user::getJoinedAtTimestamp).ifPresent(instant ->
                    embed.addField("Joined server: ", instant.atZone(ZoneId.of("UTC")).toLocalDateTime().format(
                            new DateTimeFormatterBuilder()
                                    .appendPattern("dd MMMM yyyy hh:mm:ss")
                                    .toFormatter()) + " (UTC)", true));

            embed.addField("Roles: ", stringBuilder.toString(), true)
                    .addField("Is server admin?: ", isUserServerAdmin.get() ? "Yes" : "No", true);

            if (user.isBotOwner()) embed.addField("Is bot owner?: ", "Yes", true);

            embed.setColor(BasicCommands.getRandomColor());

            embedBuilder.set(embed);
        }, () -> embedBuilder.set(new EmbedBuilder()
                .setTitle("Error!")
                .setDescription("You are not a user! You can't use this command.")
                .setColor(BasicCommands.getRandomColor())));

        return embedBuilder.get();
    }

    public static EmbedBuilder serverInfo(Optional<Server> serverOptional) {
        AtomicReference<EmbedBuilder> embedBuilder = new AtomicReference<>();
        serverOptional.ifPresentOrElse(server -> {
            Set<User> bots = new HashSet<>();
            Set<User> humans = new HashSet<>();
            server.getMembers().forEach(user -> {
                if (user.isBot()) {
                    bots.add(user);
                } else {
                    humans.add(user);
                }
            });

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Information about " + server.getName() + ":-")
                    .setImage(server.getIcon().isPresent() ? server.getIcon().get() : null)
                    .addField("Server Id: ", server.getIdAsString(), true)
                    .addField("Server owner: ", server.getOwner().isPresent() ? server.getOwner().get().getDisplayName(server) : "Not found!", true)
                    .addField("Created on: ", server.getCreationTimestamp().atZone(ZoneId.of("UTC")).toLocalDateTime().format(new DateTimeFormatterBuilder()
                            .appendPattern("dd MMMM yyyy hh:mm:ss")
                            .toFormatter()), true)
                    .addField("Member count: ", String.valueOf(server.getMemberCount()), true)
                    .addField("Humans / Bots count: ", humans.size() + " / " + bots.size(), true)
                    .addField("Roles count: ", String.valueOf(server.getRoles().size()))
                    .addField("Total channels count: ", String.valueOf(server.getChannels().size()), true)
                    .addField("Text / Voice channels count: ", server.getTextChannels().size() + " / " + server.getVoiceChannels().size(), true)
                    .addField("Boosts count:", String.valueOf(server.getBoostCount()), true)
                    .addField("Boost level: ", String.valueOf(server.getBoostLevel()), true)
                    .setColor(BasicCommands.getRandomColor());

            embedBuilder.set(embed);
        }, () -> embedBuilder.set(new EmbedBuilder()
                .setTitle("Error!")
                .setDescription("This command only works in servers!")));

        return embedBuilder.get();
    }
}
