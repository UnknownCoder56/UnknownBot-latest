package com.uniqueapps.unknownbot;

import com.uniqueapps.unknownbot.commands.BasicCommands;
import com.uniqueapps.unknownbot.commands.HybridCommands;
import com.uniqueapps.unknownbot.objects.Shop;
import org.apache.commons.lang3.tuple.Pair;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.StringJoiner;

public class ComponentsListener implements MessageComponentCreateListener {

    @Override
    public void onComponentCreate(MessageComponentCreateEvent messageComponentCreateEvent) {
        String customId = messageComponentCreateEvent.getMessageComponentInteraction().getCustomId();
        if (customId.contains("laptop")) {
            long userId = Long.parseLong(customId.split("_")[2]);
            if (userId != messageComponentCreateEvent.getMessageComponentInteraction().getUser().getId()) {
                messageComponentCreateEvent.getMessageComponentInteraction().createImmediateResponder()
                        .addEmbed(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("You can't use someone else's PC!"))
                        .respond();
                return;
            }
            if (customId.contains("code")) {
                if (!Shop.ownedItems.get(userId).containsKey("Hacker Code")) {
                    messageComponentCreateEvent.getMessageComponentInteraction().createImmediateResponder()
                            .addEmbed(new EmbedBuilder()
                                    .setTitle("Error!")
                                    .setDescription("You don't have the Hacker Code! Buy it from the shop."))
                            .respond();
                    return;
                }
                Pair<String, Integer> question = generatePattern();
                messageComponentCreateEvent.getInteraction().respondWithModal("laptop_code_result_" + question.getRight(), "What's the next number in the pattern?",
                        ActionRow.of(TextInput.create(TextInputStyle.SHORT, "laptop_code_answer", question.getLeft(), true)));
            } else if (customId.contains("off")) {
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater()
                        .removeAllEmbeds()
                        .addEmbed(new EmbedBuilder()
                                .setTitle("You shut down the laptop."))
                        .update();
            }
            return;
        }
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
            default -> messageComponentCreateEvent.getMessageComponentInteraction().createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("Something went wrong. Please try again."))
                    .respond();
        }
    }

    private Pair<String, Integer> generatePattern() {
        int patternMultiplier = Helper.getRandomInteger(3, 1);
        int patternAdder = Helper.getRandomInteger(8, 1);
        int currentPatternNumber = 1;
        int answer;
        int[] pattern = new int[9];
        for (int i = 1; i < 10; i++) {
            currentPatternNumber += (patternMultiplier * i) + patternAdder;
            pattern[i - 1] = currentPatternNumber;
        }

        StringJoiner joiner = new StringJoiner(", ");
        joiner.add("1");
        for (int j : pattern) {
            joiner.add(String.valueOf(j));
        }

        currentPatternNumber += (patternMultiplier * 10) + patternAdder;
        answer = currentPatternNumber;

        return Pair.of(joiner.toString(), answer);
    }
}
