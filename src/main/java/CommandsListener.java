import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;

public class CommandsListener implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (!message.getAuthor().isBotUser()) {
            if (message.getContent().startsWith(">")) {

                if (message.getServer().isPresent()) {
                    LocalDateTime localDateTime = message.getCreationTimestamp().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a").toFormatter();
                    System.out.println("\nBot was asked: " + message.getContent() +
                            "\nAt: " + message.getServer().get().getName() +
                            "\nBy: " + message.getAuthor().getName() +
                            "\nOn: " + dtf.format(localDateTime).toUpperCase(Locale.ROOT));
                }

                if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">ping")) BasicCommands.ping(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">hello")) BasicCommands.hello(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">hello")) BasicCommands.hello(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">dt")) BasicCommands.dt(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">help")) BasicCommands.help(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">replies")) BasicCommands.replies(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">botinfo")) BasicCommands.botinfo(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">admes")) BasicCommands.admes(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">gsearch")) BasicCommands.gsearch(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">makefile")) BasicCommands.makefile(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">calc")) BasicCommands.calc(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">reply")) BasicCommands.setCustomReply(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">noreply")) BasicCommands.noReply(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">clear")) BasicCommands.clearMessages(event);

                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">warn")) ModCommands.warn(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">kick")) ModCommands.kick(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">ban")) ModCommands.ban(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">mute")) ModCommands.mute(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">nowarns")) ModCommands.clearWarn(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">unban")) ModCommands.unban(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">unmute")) ModCommands.unMute(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).startsWith(">getwarns")) ModCommands.getWarns(event);

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
