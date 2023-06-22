package com.uniqueapps.UnknownBot;

import com.uniqueapps.UnknownBot.commands.BasicCommands;
import com.uniqueapps.UnknownBot.commands.CurrencyCommands;
import com.uniqueapps.UnknownBot.commands.ModCommands;
import com.uniqueapps.UnknownBot.objects.Shop;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class CommandsListener implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        AsyncListener asyncListener = new AsyncListener(event);
        CompletableFuture<AsyncListener> listenerCompletableFuture = CompletableFuture
                .supplyAsync(() -> asyncListener);

        listenerCompletableFuture
                .thenApplyAsync((asyncListener1) -> {
                    asyncListener1.run();
                    return asyncListener1;
                });
    }
}

class AsyncListener implements Runnable {

    MessageCreateEvent event;

    public AsyncListener(MessageCreateEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        Message message = event.getMessage();
        if (!message.getAuthor().isBotUser()) {
            if (message.getContent().startsWith(">")) {
                if (message.getServer().isPresent()) {
                    DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a")
                            .toFormatter();
                    System.out.println("\nBot was asked: " + message.getContent() +
                            "\nAt: " + message.getServer().get().getName() +
                            "\nBy: " + message.getAuthor().getName() +
                            "\nOn: " + dtf.format(message.getCreationTimestamp().atZone(ZoneId.of("Asia/Kolkata"))
                                    .toLocalDateTime()));
                } else {
                    DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a")
                            .toFormatter();
                    System.out.println("\nBot was asked: " + message.getContent() +
                            "\nAt: " + "DMs" +
                            "\nBy: " + message.getAuthor().getName() +
                            "\nOn: " + dtf.format(message.getCreationTimestamp().atZone(ZoneId.of("Asia/Kolkata"))
                                    .toLocalDateTime()));
                }
                
                switch (message.getContent().toLowerCase(Locale.ROOT).split(" ")[0]) {
                	// Utility commands
                	case ">ping" -> BasicCommands.ping(event); // Slashed
                	case ">hello" -> BasicCommands.hello(event); // Slashed
                	case ">dt" -> BasicCommands.dt(event); // Slashed
                	case ">help" -> BasicCommands.help(event);
                	case ">replies" -> BasicCommands.replies(event);
                	case ">botinfo" -> BasicCommands.botInfo(event); // Slashed
                	case ">userinfo" -> BasicCommands.userInfo(event); // Slashed
                	case ">serverinfo" -> BasicCommands.serverInfo(event); // Slashed
                	case ">admes" -> BasicCommands.admes(event);
                	case ">makefile" -> BasicCommands.makefile(event); // Slashed
                	case ">calc" -> BasicCommands.calc(event); // Slashed
                	case ">reply" -> BasicCommands.setCustomReply(event);
                	case ">noreply" -> BasicCommands.noReply(event);
                	case ">dm" -> BasicCommands.dm(event); // Slashed
                	case ">rps" -> BasicCommands.rps(event);
                	case ">tti" -> BasicCommands.texttoimg(event);
                	case ">setting" -> BasicCommands.changeUserSettings(event);
                	
                	// Moderation commands
                    case ">nuke" -> ModCommands.nuke(event);
                    case ">clear" -> ModCommands.clearMessages(event);
                	case ">warn" -> ModCommands.warn(event);
                	case ">kick" -> ModCommands.kick(event);
                	case ">ban" -> ModCommands.ban(event);
                	case ">mute" -> ModCommands.mute(event);
                	case ">nowarns" -> ModCommands.clearWarn(event);
                	case ">unban" -> ModCommands.unban(event);
                	case ">unmute" -> ModCommands.unmute(event);
                	case ">getwarns" -> ModCommands.getWarns(event);
                	
                	// Economy commands
                	case ">bal" -> CurrencyCommands.balance(event);
                	case ">work" -> CurrencyCommands.work(event);
                	case ">lb" -> CurrencyCommands.leaderboard(event);
                	case ">glb" -> CurrencyCommands.globalLeaderboard(event);
                	case ">rob" -> CurrencyCommands.rob(event);
                	case ">give" -> CurrencyCommands.give(event);
                	case ">daily" -> CurrencyCommands.daily(event);
                    case ">weekly" -> CurrencyCommands.weekly(event);
                    case ">monthly" -> CurrencyCommands.monthly(event);
                	case ">shop", ">buy", ">use" -> Shop.handleCommands(event);
                    case ">inv" -> CurrencyCommands.inv(event);
                	
                	// Error handler
                	default -> event.getChannel().sendMessage(new EmbedBuilder()
                			.setTitle("Error!")
                            .setDescription("No such command was found! Type '>help' to view available commands."));
                }
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

    public String getReply(String text) {
        for (String reply : BasicCommands.customReplies.keySet()) {
            if (text.contains(reply)) {
                return BasicCommands.customReplies.get(reply);
            }
        }
        return null;
    }
}
