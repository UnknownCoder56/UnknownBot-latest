package com.uniqueapps.unknownbot;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.listener.interaction.ModalSubmitListener;

public class ModalsListener implements ModalSubmitListener {

    public static final int prizeOrCost = 9000;

    @Override
    public void onModalSubmit(ModalSubmitEvent modalSubmitEvent) {
        String customId = modalSubmitEvent.getModalInteraction().getCustomId();
        if (customId.contains("laptop_code_result")) {
            int correctAnswer = Integer.parseInt(customId.split("_")[3]);
            String input = modalSubmitEvent.getModalInteraction().getTextInputValueByCustomId("laptop_code_answer").orElseThrow();
            try {
                int answer = Integer.parseInt(input);
                if (answer == correctAnswer) {
                    if (Helper.creditBalance(prizeOrCost, modalSubmitEvent.getModalInteraction().getUser(), modalSubmitEvent.getModalInteraction().getChannel().orElseThrow())) {
                        modalSubmitEvent.getModalInteraction().createImmediateResponder()
                                .addEmbed(new EmbedBuilder()
                                        .setTitle("Success!")
                                        .setDescription("Hacking successful! You got :coin: " + prizeOrCost))
                                .respond();
                    }
                } else {
                    if (Helper.debitBalance(prizeOrCost, modalSubmitEvent.getModalInteraction().getUser(), modalSubmitEvent.getModalInteraction().getChannel().orElseThrow())) {
                        modalSubmitEvent.getModalInteraction().createImmediateResponder()
                                .addEmbed(new EmbedBuilder()
                                        .setTitle("Failure!")
                                        .setDescription("Hacking failed due to wrong answer. You lost :coin: " + prizeOrCost))
                                .respond();
                    }
                }
            } catch (NumberFormatException ex) {
                if (Helper.debitBalance(prizeOrCost, modalSubmitEvent.getModalInteraction().getUser(), modalSubmitEvent.getModalInteraction().getChannel().orElseThrow())) {
                    modalSubmitEvent.getModalInteraction().createImmediateResponder()
                            .addEmbed(new EmbedBuilder()
                                    .setTitle("Failure!")
                                    .setDescription("Hacking failed due to incorrect input type. You lost :coin: " + prizeOrCost))
                            .respond();
                }
            }
        }
    }
}
