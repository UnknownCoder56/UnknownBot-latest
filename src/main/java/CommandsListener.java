import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.channel.user.PrivateChannelCreateListener;
import org.javacord.api.listener.message.MessageCreateListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

public class CommandsListener implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (!message.getAuthor().isBotUser()) {
            if (message.getContent().startsWith(">")) {

                if (message.getServer().isPresent()) {
                    LocalDateTime localDateTime = message.getCreationTimestamp().atZone(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
                    DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a").toFormatter();
                    System.out.println("\nBot was asked: " + message.getContent() +
                            "\nAt: " + message.getServer().get().getName() +
                            "\nBy: " + message.getAuthor().getName() +
                            "\nOn: " + dtf.format(localDateTime).toUpperCase(Locale.ROOT));
                } else {
                    LocalDateTime localDateTime = message.getCreationTimestamp().atZone(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
                    DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a").toFormatter();
                    System.out.println("\nBot was asked: " + message.getContent() +
                            "\nAt: " + "DMs" +
                            "\nBy: " + message.getAuthor().getName() +
                            "\nOn: " + dtf.format(localDateTime).toUpperCase(Locale.ROOT));
                }

                String command = message.getContent().toLowerCase(Locale.ROOT);

                // Utility commands
                if (command.startsWith(">ping")) BasicCommands.ping(event);
                else if (command.startsWith(">hello")) BasicCommands.hello(event);
                else if (command.startsWith(">dt")) BasicCommands.dt(event);
                else if (command.startsWith(">help")) BasicCommands.help(event);
                else if (command.startsWith(">replies")) BasicCommands.replies(event);
                else if (command.startsWith(">botinfo")) BasicCommands.botinfo(event);
                else if (command.startsWith(">admes")) BasicCommands.admes(event);
                else if (command.startsWith(">gsearch")) BasicCommands.gsearch(event);
                else if (command.startsWith(">makefile")) BasicCommands.makefile(event);
                else if (command.startsWith(">calc")) BasicCommands.calc(event);
                else if (command.startsWith(">reply")) BasicCommands.setCustomReply(event);
                else if (command.startsWith(">noreply")) BasicCommands.noReply(event);
                else if (command.startsWith(">clear")) BasicCommands.clearMessages(event);
                else if (command.startsWith(">dm")) BasicCommands.dm(event);
                else if (command.startsWith(">nuke")) BasicCommands.nuke(event);

                // Mod commands
                else if (command.startsWith(">warn")) ModCommands.warn(event);
                else if (command.startsWith(">kick")) ModCommands.kick(event);
                else if (command.startsWith(">ban")) ModCommands.ban(event);
                else if (command.startsWith(">mute")) ModCommands.mute(event);
                else if (command.startsWith(">nowarns")) ModCommands.clearWarn(event);
                else if (command.startsWith(">unban")) ModCommands.unban(event);
                else if (command.startsWith(">unmute")) ModCommands.unMute(event);
                else if (command.startsWith(">getwarns")) ModCommands.getWarns(event);

                // Currency commands
                else if (command.startsWith(">bal")) CurrencyCommands.balance(event);
                else if (command.startsWith(">work")) CurrencyCommands.work(event);
                else if (command.startsWith(">lb")) CurrencyCommands.leaderboard(event);
                else if (command.startsWith(">glb")) CurrencyCommands.globalLeaderboard(event);
                else if (command.startsWith(">rob")) CurrencyCommands.rob(event);
                else if (command.startsWith(">give")) CurrencyCommands.give(event);

                // Error handler
                else event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("No such command was found! Type '>help' to view available commands."));
            } else if (getReply(message.getContent()) != null) {
                BasicCommands.customReply(event, getReply(message.getContent()));
            }

            if (!message.getMentionedRoles().isEmpty()) {
                for (Role role : message.getMentionedRoles()) {
                    if (message.getServer().get().getRoles(Main.api.getYourself()).contains(role)) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Info!")
                                .setDescription("My prefix is \">\"!"));
                        break;
                    }
                }
            } else if (!message.getMentionedUsers().isEmpty()) {
                for (User user : message.getMentionedUsers()) {
                    if (Main.api.getYourself().getId() == user.getId()) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Info!")
                                .setDescription("My prefix is \">\"!"));
                        break;
                    }
                }
            }
        }
    }

    public static String getReply(String text) {
        for (String reply : BasicCommands.customReplies.keySet()) {
            if (text.contains(reply)) return BasicCommands.customReplies.get(reply);
        }
        return null;
    }
}
