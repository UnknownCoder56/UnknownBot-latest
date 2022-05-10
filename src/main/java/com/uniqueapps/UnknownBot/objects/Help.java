package com.uniqueapps.UnknownBot.objects;

import java.util.HashMap;
import java.util.Map;

public class Help {
    
    public Map<String, String> utilityCommands = new HashMap<>();
    public Map<String, String> moderationCommands = new HashMap<>();
    public Map<String, String> economyCommands = new HashMap<>();

    public Help() {
        initUtility();
        initModeration();
        initEconomy();
    }

    private void initUtility() {
        utilityCommands.put(">help (category)", "Displays this help message.");
        utilityCommands.put(">botinfo", "Shows information about UnknownBot.");
        utilityCommands.put(">hello", "Says hello to you.");
        utilityCommands.put(">ping", "Displays bot latency.");
        utilityCommands.put(">admes (query)", "Ask anything to the bot.");
        utilityCommands.put(">dt", "Shows the current date and time (AM/PM).");
        utilityCommands.put(">gsearch (search text)", "Searches google and returns results as html.");
        utilityCommands.put(">makefile (text)", "Creates file from text.");
        utilityCommands.put(">calc (num1),(sign),(num2) _Warning: No spaces after/before/in commas_", "Does calculation. Supported signs -> +, -, *, /");
        utilityCommands.put(">reply (text),(reply) _Warning: No spaces after/before/in commas_", "Makes the bot reply when you send a specific text.");
        utilityCommands.put(">noreply (text)", "Disables custom reply.");
        utilityCommands.put(">replies", "Displays all custom replies set.");
        utilityCommands.put(">dm (mention) \"message\"", "DMs a message to a user.");
        utilityCommands.put(">rps (choice)", "Play \"Rock Paper Scissors\" with the bot. Choices include: ```r, p, s``` or ```rock, paper, scissors```.");
        utilityCommands.put(">tti (text)", "Converts given text to image.");
        utilityCommands.put(">setting (type) (true or false)", "Changes your user settings, where type include - 'bankdm', 'passive'. Example: \">setting bankdm false\"");
    }

    private void initModeration() {
        moderationCommands.put(">clear (amount)", "Clears specified number of messages.");
        moderationCommands.put(">warn (mention) \"cause\"", "Warns a user. _Usage: Type >warn, then mention" +
                            " user, then put reason within quotation marks (\") (no reason supported too). Put space between each" +
                            " argument. Multiple warns supported. Warns are isolated for each server._");
        moderationCommands.put(">kick (mention)", "Kicks the mentioned user.");
        moderationCommands.put(">ban (mention)", "Bans the mentioned user.");
        moderationCommands.put(">mute", "Mutes the mentioned user (Mute = Disable chat and VC).");
        moderationCommands.put(">nowarns (mention)", "Clear all warns for a user (Individual removal not" +
                            " supported yet).");
        moderationCommands.put(">unban (mention)", "Unbans the mentioned user.");
        moderationCommands.put(">unmute", "Unmutes the mentioned user (Mute = Enable chat and VC).");
        moderationCommands.put(">getwarns (mention)", "Gets all warns for a user.");
        moderationCommands.put(">nuke", "Cleans everything in a channel.");
    }

    private void initEconomy() {
        economyCommands.put(">bal", "Shows your current bank balance");
        economyCommands.put(">bal (mention)", "Shows others' current bank balance");
        economyCommands.put(">work", "You work and gain money!");
        economyCommands.put(">lb", "Compare and check out richest users of your server!");
        economyCommands.put(">glb", "Compare and check richest users of our bot (Global)!");
        economyCommands.put(">rob (mention)", "Rob others and get money, the dark way.");
        economyCommands.put(">give (amount) (mention)", "Transfer money to others' accounts!");
        economyCommands.put(">daily", "Get your daily earnings!");
        economyCommands.put(">shop", "Shows a list of all items in the shop, and their details.");
        economyCommands.put(">buy (item codename)", "Buy an item from the shop. Codename with command is available when you type 'shop', under the 'Command for buying' section.");
        economyCommands.put(">use (item codename)", "Use an item in your inventory. Codename with command is available when you type 'shop', under the 'Command for using' section");
        economyCommands.put(">inv", "Displays a list of all items in your inventory.");
    }
}
