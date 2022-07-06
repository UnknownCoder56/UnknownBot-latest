package com.uniqueapps.UnknownBot.commands;

import static com.uniqueapps.UnknownBot.commands.BasicCommands.getRandomColor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.money.Monetary;

import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javamoney.moneta.Money;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uniqueapps.UnknownBot.Main;

public class SlashCommands implements SlashCommandCreateListener {

    public SlashCommands() {
        createCommandsIfNotExist();
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        var command = event.getSlashCommandInteraction().getCommandName();
        switch (command) {
            case "ping" -> ping(event);
            case "hello" -> hello(event);
            case "datetime" -> datetime(event);
            case "calculate" -> calc(event);
            case "dm" -> dm(event);
            case "currconv" -> currencyConvert(event);
            case "makecolor" -> generateColor(event);
            case "randomcolor" -> randomColor(event);
        }
    }

    private void createCommandsIfNotExist() {
        Server server = Main.api.getServerById(973174048672600104L).orElseThrow();
        Main.api.bulkOverwriteGlobalApplicationCommands(new ArrayList<>()).join();
        Main.api.bulkOverwriteServerApplicationCommands(server, new ArrayList<>()).join();
        Main.api.getServerSlashCommands(server).thenApplyAsync(commands -> {
            if (commands.stream().noneMatch(slashCommand -> slashCommand.getName().equals("ping"))) {
                SlashCommand.with("ping", "Displays bot latency.")
                        .createForServer(server)
                        .join();
            }
            if (commands.stream().noneMatch(slashCommand -> slashCommand.getName().equals("hello"))) {
                SlashCommand.with("hello", "Says hello to the user.")
                        .createForServer(server)
                        .join();
            }
            if (commands.stream().noneMatch(slashCommand -> slashCommand.getName().equals("datetime"))) {
                SlashCommand.with("datetime", "Displays the current UTC or GMT date and time.")
                        .createForServer(server)
                        .join();
            }
            if (commands.stream().noneMatch(slashCommand -> slashCommand.getName().equals("calculate"))) {
                SlashCommand.with("calculate", "Performs calculations on two numbers.")
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "number_1", "The number 1 of the problem.", true))
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "operation_symbol", "The operation type to perform between the numbers.", true,
                                Arrays.asList(
                                        SlashCommandOptionChoice.create("add", "+"),
                                        SlashCommandOptionChoice.create("subtract", "-"),
                                        SlashCommandOptionChoice.create("multiply", "*"),
                                        SlashCommandOptionChoice.create("divide", "/")
                                )))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "number_2", "The number 2 of the problem.", true))
                        .createForServer(server)
                        .join();
            }
            if (commands.stream().noneMatch(slashCommand -> slashCommand.getName().equals("dm"))) {
                SlashCommand.with("dm", "DMs a message to a user.")
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "The user to DM.", true))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "dm_message", "The message to DM to the user", true))
                        .createForServer(server)
                        .join();
            }
            if (commands.stream().noneMatch(slashCommand -> slashCommand.getName().equals("gencolor"))) {
                SlashCommand.with("makecolor", "Generates a color based on given RGB values.")
                        .addOption(SlashCommandOption.createLongOption("red", "The red value of the color. Must be between -1 and 256 (exclusive).", true))
                        .addOption(SlashCommandOption.createLongOption("green", "The green value of the color. Must be between -1 and 256 (exclusive).", true))
                        .addOption(SlashCommandOption.createLongOption("blue", "The blue value of the color. Must be between -1 and 256 (exclusive).", true))
                        .createForServer(server)
                        .join();
            }
            if (commands.stream().noneMatch(slashCommand -> slashCommand.getName().equals("randcolor"))) {
                SlashCommand.with("randomcolor", "Generates a random color.")
                        .createForServer(server)
                        .join();
            }
            if (commands.stream().noneMatch(slashCommand -> slashCommand.getName().equals("currconv"))) {
                try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/currencies.json")))) {
                    JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
                    var jsonArray = object.get("currencies").getAsJsonArray();
                    List<SlashCommandOptionChoice> currencyChoices = new ArrayList<>();
                    jsonArray.forEach((jsonElement -> {
                        JsonObject currency = jsonElement.getAsJsonObject();
                        currencyChoices.add(SlashCommandOptionChoice.create(currency.get("name").getAsString(), currency.get("code").getAsString()));
                    }));
                    SlashCommand.with("currconv", "Converts one currency to another.")
                            .addOption(SlashCommandOption.createDecimalOption("convert_amount", "The amount of money to convert", true))
                            .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "from_currency", "The currency of the given amount", true, currencyChoices))
                            .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "to_currency", "The currency to convert the amount to", true, currencyChoices))
                            .createForServer(server)
                            .join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return commands;
        });
    }

    private void ping(SlashCommandCreateEvent event) {
        event.getSlashCommandInteraction().createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle("Pong!")
                        .setDescription("Latency is " + Main.api.measureRestLatency().join().toMillis() + " ms.")
                        .setColor(getRandomColor()))
                .respond();
    }

    private void hello(SlashCommandCreateEvent event) {
        event.getSlashCommandInteraction().createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle("Hello!")
                        .setDescription("Hello there, " + event.getSlashCommandInteraction().getUser().getName() +
                                "! UnknownBot at your service. Type \">help\" to get a list of available commands.")
                        .setColor(getRandomColor()))
                .respond();
    }

    private void datetime(SlashCommandCreateEvent event) {
        event.getSlashCommandInteraction().createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle("Current Time:-")
                        .addField("UTC or GMT", LocalDateTime.now(ZoneId.of("UTC")).format(
                                new DateTimeFormatterBuilder().appendPattern("dd MMMM yyyy hh:mm:ss a").toFormatter()).toUpperCase())
                        .setColor(getRandomColor()))
                .respond();
    }

    private void calc(SlashCommandCreateEvent event) {
        double num1 = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("number_1")).findFirst().orElseThrow().getDecimalValue().orElseThrow();
        String sign = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("operation_symbol")).findFirst().orElseThrow().getStringValue().orElseThrow();
        double num2 = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("number_2")).findFirst().orElseThrow().getDecimalValue().orElseThrow();
        double result;
        String reply;

        switch (sign) {
            case ("+") -> {
                result = num1 + num2;
                reply = num1 + " " + sign + " " + num2 + " = " + result;
            }
            case ("-") -> {
                result = num1 - num2;
                reply = num1 + " " + sign + " " + num2 + " = " + result;
            }
            case ("*") -> {
                result = num1 * num2;
                reply = num1 + " " + sign + " " + num2 + " = " + result;
            }
            case ("/") -> {
                result = num1 / num2;
                reply = num1 + " " + sign + " " + num2 + " = " + result;
            }
            default -> reply = "Not a valid operation symbol. Valid ones are +, -, * and /.";
        }

        event.getSlashCommandInteraction().createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle(reply)
                        .setColor(getRandomColor()))
                .respond();
    }

    private void dm(SlashCommandCreateEvent event) {
        User user = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("user")).findFirst().orElseThrow().getUserValue().orElseThrow();
        String message = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("dm_message")).findFirst().orElseThrow().getStringValue().orElseThrow();
        try {
            PrivateChannel channel = user.openPrivateChannel().get();

            event.getSlashCommandInteraction().getServer().ifPresentOrElse((server) -> channel.sendMessage(new EmbedBuilder()
                            .setTitle("Alert! Message from " + event.getSlashCommandInteraction().getUser().getName() + " at " +
                                    server.getName() + " :-")
                            .setDescription(message)
                            .setColor(getRandomColor())),
                    () -> channel.sendMessage(new EmbedBuilder()
                            .setTitle("Alert! Message from " + event.getSlashCommandInteraction().getUser().getName() + ":-")
                            .setDescription(message)
                            .setColor(getRandomColor())));

            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully DM-ed message to user.")
                            .setColor(getRandomColor()))
                    .respond();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("DM to user failed (Possible reason: User's DMs are closed).")
                            .setColor(getRandomColor()))
                    .respond();
        }
    }

    private void currencyConvert(SlashCommandCreateEvent event) {
        double givenAmount = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("convert_amount")).findFirst().orElseThrow().getDecimalValue().orElseThrow();
        String givenFrom = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("from_currency")).findFirst().orElseThrow().getStringValue().orElseThrow();
        String givenTo = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("to_currency")).findFirst().orElseThrow().getStringValue().orElseThrow();

        Money fromMoney = Money.of(givenAmount, givenFrom).with(Monetary.getDefaultRounding());

        String exchangeApiKey = System.getenv("EXAPI");
        try {
            URL requestURL = new URL("https://v6.exchangerate-api.com/v6/" + exchangeApiKey + "/pair/" + givenFrom + "/" + givenTo + "/" + fromMoney.getNumber().doubleValueExact());
            HttpURLConnection request = (HttpURLConnection) requestURL.openConnection();
            request.connect();

            JsonObject reply = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();
            if (!reply.get("result").getAsString().equals("error")) {
                String convertedAmountStr = reply.get("conversion_result").getAsString();
                Money toMoney = Money.of(Double.parseDouble(convertedAmountStr), givenTo).with(Monetary.getDefaultRounding());
                event.getSlashCommandInteraction().createImmediateResponder()
                        .addEmbed(new EmbedBuilder()
                                .setTitle(givenFrom + " to " + givenTo + " conversion:-")
                                .setDescription(fromMoney + " = " + toMoney)
                                .setColor(getRandomColor()))
                        .respond();
            } else {
                event.getSlashCommandInteraction().createImmediateResponder()
                        .addEmbed(new EmbedBuilder()
                                .setTitle("Error!")
                                .setDescription("Conversion failed!")
                                .setColor(getRandomColor()))
                        .respond();
            }
        } catch (IOException e) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("Conversion failed!")
                            .setColor(getRandomColor()))
                    .respond();
            e.printStackTrace();
        }
    }

    private void generateColor(SlashCommandCreateEvent event) {
        long red = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("red")).findFirst().orElseThrow().getLongValue().orElseThrow();
        long green = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("green")).findFirst().orElseThrow().getLongValue().orElseThrow();
        long blue = event.getSlashCommandInteraction().getArguments().stream().filter(slashCommandInteractionOption -> slashCommandInteractionOption.getName().equals("blue")).findFirst().orElseThrow().getLongValue().orElseThrow();
        if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Creating color...")
                            .setColor(getRandomColor()))
                    .respond();
            Color color = new Color(Math.toIntExact(red), Math.toIntExact(green), Math.toIntExact(blue));
            BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(color);
            graphics.fillRoundRect(0, 0, image.getWidth(), image.getHeight(), 50, 50);
            graphics.drawString("RGB: " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue(), 10, 10);
            graphics.drawString("HEX: " + String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()), 10, 20);
            graphics.dispose();
            event.getSlashCommandInteraction().createFollowupMessageBuilder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Here's your color for (R:" + red + ", G:" + green + ", B:" + blue + "):-")
                            .setImage(image)
                            .setColor(color))
                    .send();
        } else {
            event.getSlashCommandInteraction().createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("Value of R, G and B must between -1 and 256 (exclusive)!"))
                    .respond();
        }
    }

    private void randomColor(SlashCommandCreateEvent event) {
        event.getSlashCommandInteraction().createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle("Creating color...")
                        .setColor(getRandomColor()))
                .respond();
        Color color = getRandomColor();
        BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(color);
        graphics.fillRoundRect(0, 0, image.getWidth(), image.getHeight(), 50, 50);
        graphics.dispose();
        event.getSlashCommandInteraction().createFollowupMessageBuilder()
                .addEmbed(new EmbedBuilder()
                        .setTitle("Success!")
                        .setDescription("Here's your color for (R:" + color.getRed() + ", G:" + color.getGreen() + ", B:" + color.getBlue() + "):-")
                        .setImage(image)
                        .setColor(color))
                .send();
    }
}
