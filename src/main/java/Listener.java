import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Locale;

public class Listener implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (!message.getAuthor().isBotUser()) {
            if (message.getContent().contains(">")) {
                if (message.getContent().toLowerCase(Locale.ROOT).contains(">ping")) Commands.ping(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">hello")) Commands.hello(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">hello")) Commands.hello(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">dt")) Commands.dt(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">help")) Commands.help(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">replies")) Commands.replies(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">botinfo")) Commands.botinfo(event);

                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">admes")) Commands.admes(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">gsearch")) Commands.gsearch(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">makefile")) Commands.makefile(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">calc")) Commands.calc(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">reply")) Commands.setCustomReply(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">noreply")) Commands.noReply(event);
                else if (message.getContent().toLowerCase(Locale.ROOT).contains(">clear")) Commands.clearMessages(event);

                else event.getChannel().sendMessage("No such command was found! Type '>help' to view available commands.");
            } else {
                if (getReply(message.getContent()) != null) Commands.customReply(event, getReply(message.getContent()));
            }
        }
    }

    public static String getReply(String text) {
        for (String reply : Commands.customReplies.keySet()) {
            if (text.contains(reply)) return Commands.customReplies.get(reply);
        }
        return null;
    }
}
