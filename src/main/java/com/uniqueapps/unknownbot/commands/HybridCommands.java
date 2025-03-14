package com.uniqueapps.unknownbot.commands;

import com.uniqueapps.unknownbot.Helper;
import com.uniqueapps.unknownbot.Main;
import com.uniqueapps.unknownbot.objects.UserSettings;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HybridCommands {

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
                    .addField("Version", Helper.VERSION, true)
                    .addField("Bot discord server", "https://discord.gg/HaVaeRkNhP", true)
                    .addField("Bot type", "Utility, Moderation and Economy Bot", true)
                    .addField("Developer", "unknownpro56", true)
                    .setColor(Helper.getRandomColor());

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
                    .addField("User ID: ", user.getIdAsString(), true)
                    .addField("Is bot?: ", user.isBot() ? "Yes" : "No", true)
                    .addField("Account created: ", "<t:" + user.getCreationTimestamp().getEpochSecond() + ":F>", true);

            serverOptional.flatMap(user::getJoinedAtTimestamp).ifPresent(instant ->
                    embed.addField("Joined server: ", "<t:" + instant.getEpochSecond() + ":F>", true));

            embed.addField("Roles: ", stringBuilder.toString(), true)
                    .addField("Is server admin?: ", isUserServerAdmin.get() ? "Yes" : "No", true);

            if (user.isBotOwner()) embed.addField("Is bot owner?: ", "Yes", true);

            embed.setColor(Helper.getRandomColor());

            embedBuilder.set(embed);
        }, () -> embedBuilder.set(new EmbedBuilder()
                .setTitle("Error!")
                .setDescription("You are not a user! You can't use this command.")
                .setColor(Helper.getRandomColor())));

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
                    .addField("Server ID: ", server.getIdAsString(), true)
                    .addField("Server owner: ", server.getOwner().isPresent() ? server.getOwner().get().getDisplayName(server) : "Not found!", true)
                    .addField("Created on: ", "<t:" + server.getCreationTimestamp() + ":F>", true)
                    .addField("Members count: ", String.valueOf(server.getMemberCount()), true)
                    .addField("Humans / Bots count: ", humans.size() + " / " + bots.size(), true)
                    .addField("Roles count: ", String.valueOf(server.getRoles().size()))
                    .addField("Total channels count: ", String.valueOf(server.getChannels().size()), true)
                    .addField("Text / Voice channels count: ", server.getTextChannels().size() + " / " + server.getVoiceChannels().size(), true)
                    .addField("Boosts count:", String.valueOf(server.getBoostCount()), true)
                    .addField("Boost level: ", String.valueOf(server.getBoostLevel()), true)
                    .setColor(Helper.getRandomColor());

            embedBuilder.set(embed);
        }, () -> embedBuilder.set(new EmbedBuilder()
                .setTitle("Error!")
                .setDescription("This command only works in servers!")));

        return embedBuilder.get();
    }

    public static EmbedBuilder textToImage(String text) {
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

        return new EmbedBuilder()
                .setTitle("Success!")
                .setDescription("Here is your image.")
                .setImage(img)
                .setColor(Helper.getRandomColor());
    }

    public static EmbedBuilder changeUserSettings(Optional<User> authorOptional, String setting, String settingValue) {
        AtomicReference<EmbedBuilder> embedBuilder = new AtomicReference<>();
        authorOptional.ifPresentOrElse(user -> {
            long id = user.getId();
            if (Objects.equals(settingValue, "true")) {
                UserSettings settings = Main.userSettingsMap.get(id);
                switch (setting) {
                    case "bankdm" -> {
                        settings.setBankDmEnabled(true);
                        embedBuilder.set(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Enabled bank transaction DMs! Now you WILL be DMed about all your bank transactions.")
                                .setColor(Helper.getRandomColor()));
                    }
                    case "passive" -> {
                        settings.setBankPassiveEnabled(true);
                        embedBuilder.set(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Enabled passive mode! Now NEITHER anyone can rob you, NOR you can rob anyone else.\n" +
                                        "You also CANNOT give money to someone else.")
                                .setColor(Helper.getRandomColor()));
                    }
                    default -> {
                        embedBuilder.set(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("Setting type " + setting + " not found!")
                                .setColor(Helper.getRandomColor()));
                        return;
                    }
                }
                Main.userSettingsMap.replace(id, settings);
                Helper.refreshUserSettings();
            } else if (Objects.equals(settingValue, "false")) {
                UserSettings settings = Main.userSettingsMap.get(id);
                switch (setting) {
                    case "bankdm" -> {
                        settings.setBankDmEnabled(false);
                        embedBuilder.set(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Disabled bank transaction DMs! Now you WON'T be DMed about any of your bank transactions.")
                                .setColor(Helper.getRandomColor()));
                    }
                    case "passive" -> {
                        settings.setBankPassiveEnabled(false);
                        embedBuilder.set(new EmbedBuilder()
                                .setTitle("Success!")
                                .setDescription("Disabled passive mode! Now anyone CAN rob you, and you CAN rob anyone else.\n" +
                                        "You also CAN give money to someone else.")
                                .setColor(Helper.getRandomColor()));
                    }
                    default -> {
                        embedBuilder.set(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("Setting type " + setting + " not found!")
                                .setColor(Helper.getRandomColor()));
                        return;
                    }
                }
                Main.userSettingsMap.replace(id, settings);
                Helper.refreshUserSettings();
            } else {
                embedBuilder.set(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Incorrect arguments given! Correct syntax: '>setting (type) (true or false)'.\n" +
                                "Example: >setting bankdm false")
                        .setColor(Helper.getRandomColor()));
            }
        }, () -> embedBuilder.set(new EmbedBuilder()
                .setTitle("Error!")
                .setDescription("You are not a user! You can't use this command.")
                .setColor(Helper.getRandomColor())));

        return embedBuilder.get();
    }

    public static EmbedBuilder rps(String chosenChoice, TextChannel channel) {
        if (chosenChoice != null) {
            char choice = chosenChoice.charAt(0);
            int intChoice = Helper.getRandomInteger(2, 0);
            char[] botChoices = {'r', 'p', 's'};
            char botChoice = botChoices[intChoice];
            Helper.RpsResult winStatus = Helper.getWinStatus(botChoice, choice);

            if (winStatus == Helper.RpsResult.USER_WIN) {
                return new EmbedBuilder()
                        .setTitle("You win!")
                        .setDescription("You chose " + Helper.getChoiceName(choice) + " and bot chose " + Helper.getChoiceName(botChoice) + ".")
                        .setColor(Helper.getRandomColor());
            } else if (winStatus == Helper.RpsResult.BOT_WIN) {
                return new EmbedBuilder()
                        .setTitle("Bot wins!")
                        .setDescription("You chose " + Helper.getChoiceName(choice) + " and bot chose " + Helper.getChoiceName(botChoice) + ".")
                        .setColor(Helper.getRandomColor());
            } else if (winStatus == Helper.RpsResult.TIE) {
                return new EmbedBuilder()
                        .setTitle("Tie!")
                        .setDescription("You chose " + Helper.getChoiceName(choice) + " and bot chose " + Helper.getChoiceName(botChoice) + ".")
                        .setColor(Helper.getRandomColor());
            } else if (winStatus == Helper.RpsResult.ERROR) {
                return new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("You chose " + chosenChoice + " and bot chose " + Helper.getChoiceName(botChoice) + ".")
                        .setColor(Helper.getRandomColor());
            }
        } else {
            new MessageBuilder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Rock Paper Scissors!")
                            .setDescription("Select a choice (rock/paper/scissors).")
                            .setColor(Helper.getRandomColor()))
                    .addComponents(ActionRow.of(
                            Button.success("rps_rock", "Rock"),
                            Button.success("rps_paper", "Paper"),
                            Button.success("rps_scissors", "Scissors")
                    ))
                    .send(channel);
        }
        return null;
    }
}
