package com.uniqueapps.UnknownBot;

import com.uniqueapps.UnknownBot.commands.BasicCommands;
import com.uniqueapps.UnknownBot.commands.HybridCommands;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

public class ComponentsListener implements MessageComponentCreateListener {

    @Override
    public void onComponentCreate(MessageComponentCreateEvent messageComponentCreateEvent) {
        String customId = messageComponentCreateEvent.getMessageComponentInteraction().getCustomId();
        switch (customId) {
            case "help_category" -> {
                String category = messageComponentCreateEvent.getMessageComponentInteraction().asSelectMenuInteraction().orElseThrow().getChosenOptions().get(0).getValue();
                EmbedBuilder embedBuilder = BasicCommands.help(category);
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater()
                        .removeAllEmbeds()
                        .addEmbed(embedBuilder)
                        .update();
            }
            case "rps_rock" -> {
                EmbedBuilder embedBuilder = HybridCommands.rps("rock", messageComponentCreateEvent.getMessageComponentInteraction().getChannel().orElseThrow());
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater()
                        .removeAllEmbeds()
                        .addEmbed(embedBuilder)
                        .update();
            }
            case "rps_paper" -> {
                EmbedBuilder embedBuilder = HybridCommands.rps("paper", messageComponentCreateEvent.getMessageComponentInteraction().getChannel().orElseThrow());
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater()
                        .removeAllEmbeds()
                        .addEmbed(embedBuilder)
                        .update();
            }
            case "rps_scissors" -> {
                EmbedBuilder embedBuilder = HybridCommands.rps("scissors", messageComponentCreateEvent.getMessageComponentInteraction().getChannel().orElseThrow());
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater()
                        .removeAllEmbeds()
                        .addEmbed(embedBuilder)
                        .update();
            }
            default -> {

            }
        }
    }
}
