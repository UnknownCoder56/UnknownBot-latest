import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CurrencyCommands {

    public static Map<Long, Long> balanceMap = new HashMap<>();

    public static void balance(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            if (!balanceMap.containsKey(event.getMessageAuthor().asUser().get().getId())) {
                balanceMap.put(event.getMessageAuthor().asUser().get().getId(), 0L);
            }
            long bal = balanceMap.get(event.getMessageAuthor().asUser().get().getId());
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle(event.getMessageAuthor().getDisplayName() + "'s balance:-")
                    .addField("Bank", ":coin: " + bal)
                    .setColor(BasicCommands.getRandomColor()));
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Only users can access this command! Maybe you are a bot.")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void creditBalance(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            if (!balanceMap.containsKey(event.getMessageAuthor().asUser().get().getId())) {
                balanceMap.put(event.getMessageAuthor().asUser().get().getId(), 0L);
            }
            String[] args = event.getMessage().getContent().split(" ");
            long oldBal = balanceMap.get(event.getMessageAuthor().asUser().get().getId());
            long credit = Long.parseLong(args[1]);
            if (credit > 0) {
                long newBal = oldBal + credit;
                balanceMap.replace(event.getMessageAuthor().asUser().get().getId(), oldBal, newBal);
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Successfully updated account! Details:-")
                        .addField("Opening Balance", ":coin: " + oldBal)
                        .addField("Deposited", ":coin: " + credit)
                        .addField("Closing Balance", ":coin: " + newBal)
                        .setColor(BasicCommands.getRandomColor()));
                refreshBalances();
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Value should be more than 0.")
                        .setColor(BasicCommands.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Only users can access this command! Maybe you are a bot.")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void debitBalance(MessageCreateEvent event) {
        if (event.getMessageAuthor().asUser().isPresent()) {
            if (!balanceMap.containsKey(event.getMessageAuthor().asUser().get().getId())) {
                balanceMap.put(event.getMessageAuthor().asUser().get().getId(), 0L);
            }
            String[] args = event.getMessage().getContent().split(" ");
            long oldBal = balanceMap.get(event.getMessageAuthor().asUser().get().getId());
            long debit = Long.parseLong(args[1]);
            if (debit > 0) {
                if (debit <= oldBal) {
                    long newBal = oldBal - debit;
                    balanceMap.replace(event.getMessageAuthor().asUser().get().getId(), oldBal, newBal);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Successfully updated account! Details:-")
                            .addField("Opening Balance", ":coin: " + oldBal)
                            .addField("Withdrawn", ":coin: " + debit)
                            .addField("Closing Balance", ":coin: " + newBal)
                            .setColor(BasicCommands.getRandomColor()));
                    refreshBalances();
                } else if (oldBal == 0) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You can't withdraw, because you have no money!")
                            .setColor(BasicCommands.getRandomColor()));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("You can't withdraw more than you have in your bank!")
                            .setColor(BasicCommands.getRandomColor()));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("Value should be more than 0.")
                        .setColor(BasicCommands.getRandomColor()));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("Only users can access this command! Maybe you are a bot.")
                    .setColor(BasicCommands.getRandomColor()));
        }
    }

    public static void refreshBalances() {
        File balanceFile = new File("balanceMap.data");
        try {
            balanceFile.delete();
            balanceFile.createNewFile();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(balanceFile))) {
                objectOutputStream.writeObject(balanceMap);
                System.out.println("Balance file updated!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
