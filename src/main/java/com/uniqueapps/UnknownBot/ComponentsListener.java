package com.uniqueapps.UnknownBot;

import com.uniqueapps.UnknownBot.commands.BasicCommands;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

public class ComponentsListener implements MessageComponentCreateListener {

    @Override
    public void onComponentCreate(MessageComponentCreateEvent messageComponentCreateEvent) {
        String customId = messageComponentCreateEvent.getMessageComponentInteraction().getCustomId();
        switch (customId) {
            case "help_category" -> {
                String category = messageComponentCreateEvent.getMessageComponentInteraction().asSelectMenuInteraction().orElseThrow().getChosenOptions().get(0).getValue();
                messageComponentCreateEvent.getMessageComponentInteraction().createImmediateResponder()
                        .addEmbed(BasicCommands.help(category))
                        .respond();
            }
            default -> {

            }
        }
    }
}
