import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModCommands {

    public static Map<Long, Map<Long, Warn>> warnMap = new HashMap<>();

    public static void kick(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canKickUser(Main.api.getYourself(), event.getMessage().getMentionedUsers().get(0))) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.kickUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully kicked user " + user.getDiscriminatedName() + "."));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't kick that user! Reasons - No kick permission, lower role or" +
                                    "lower position."));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!"));
        }
    }

    public static void ban(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canBanUser(Main.api.getYourself(), event.getMessage().getMentionedUsers().get(0))) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.banUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully banned user " + user.getDiscriminatedName() + "."));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't ban that user! Reasons - No kick permission, lower role or" +
                                    "lower position."));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!"));
        }
    }

    public static void mute(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canMuteMembers(Main.api.getYourself())) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.muteUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully muted user " + user.getDiscriminatedName() + "."));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't mute that user! Reasons - No kick permission, lower role or" +
                                    "lower position."));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!"));
        }
    }

    public static void unban(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canBanUser(Main.api.getYourself(), event.getMessage().getMentionedUsers().get(0))) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.unbanUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully unbanned user " + user.getDiscriminatedName() + "."));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't unban that user! Reasons - No kick permission, lower role or" +
                                    "lower position."));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!"));
        }
    }

    public static void unMute(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            if (event.getServer().isPresent()) {
                if (event.getServer().get().canMuteMembers(Main.api.getYourself())) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    Server server = event.getServer().get();
                    server.unmuteUser(user);
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully unmuted user " + user.getDiscriminatedName() + "."));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("I can't unmute that user! Reasons - No kick permission, lower role or" +
                                    "lower position."));
                }
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!"));
        }
    }

    public static void warn(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            String cause = StringUtils.substringBetween(event.getMessage().getContent(), "\"", "\"");
            User user = event.getMessage().getMentionedUsers().get(0);
            Server server = event.getServer().get();
            if (!warnMap.containsKey(server.getId())) {
                warnMap.put(server.getId(), new HashMap<>());
                if (!warnMap.get(server.getId())
                        .containsKey(user.getId())) {
                    Warn warn = new Warn(cause, user.getId());
                    warnMap.get(server.getId())
                            .put(warn.getUserId(), warn);
                } else {
                    warnMap.get(server.getId())
                            .get(user.getId()).newWarn(cause);
                }
            } else {
                if (!warnMap.get(server.getId())
                        .containsKey(user.getId())) {
                    Warn warn = new Warn(cause, user.getId());
                    warnMap.get(event.getServer().get().getId())
                            .put(warn.getUserId(), warn);
                } else {
                    warnMap.get(server.getId())
                            .get(user.getId()).newWarn(cause);
                }
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Success!")
                    .setDescription("Successfully warned " + user.getDiscriminatedName() + " for cause:\n" +
                            cause + "\nHe/She now has " + warnMap.get(event.getServer().get().getId()).get(user.getId()).warns + " warn(s)."));
            if (user.getPrivateChannel().isPresent()) {
                if (event.getServer().isPresent()) {
                    user.getPrivateChannel().get().sendMessage(new EmbedBuilder()
                            .setTitle("Alert!")
                            .setDescription("You have been warned in **" + event.getServer().get().getName() + "** for reason: **" + cause +
                                    "**. You now have **" + warnMap.get(event.getServer().get().getId()).get(user.getId()).warns + "** warn(s) in that server."));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("This is not a server!"));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Alert!")
                        .setDescription("Failed to DM user, but warn was successful."));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!"));
        }
        refreshWarns();
    }

    public static void clearWarn(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            User user = event.getMessage().getMentionedUsers().get(0);
            if (event.getServer().isPresent()) {
                if (warnMap.containsKey(event.getServer().get().getId())) {
                    warnMap.get(event.getServer().get().getId())
                            .remove(user.getId());
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Success!")
                            .setDescription("Successfully removed all warnings for " + user.getDiscriminatedName() + "!"));
                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("No warns were found for this user!"));
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Error!")
                        .setDescription("No warns were found for this user!"));
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!"));
        }
        refreshWarns();
    }

    public static void getWarns(MessageCreateEvent event) {
        if (event.getMessageAuthor().isServerAdmin()) {
            User user = event.getMessage().getMentionedUsers().get(0);
            StringBuilder stringBuilder = new StringBuilder();
            if (event.getServer().isPresent()) {
                if (warnMap.containsKey(event.getServer().get().getId())) {
                    if (warnMap.get(event.getServer().get().getId()).containsKey(user.getId())) {
                        Warn warn = warnMap.get(event.getServer().get().getId())
                                .get(user.getId());
                        for (String warnCause : warn.warnCauses) {
                            stringBuilder.append(warnCause).append("\n");
                        }
                    } else {
                        stringBuilder.append("No warns were found for this user!");
                    }
                } else {
                    stringBuilder.append("No warns were found for this user!");
                }
            } else {
                stringBuilder.append("No warns were found for this user!");
            }
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Warns for " + user.getDiscriminatedName() + ":-")
                    .setDescription(stringBuilder.toString()));
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Error!")
                    .setDescription("You don't have admin perms, so you cannot use mod commands!"));
        }
    }

    public static void refreshWarns() {
        File warnMapFile = new File("warnsMap.data");
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(warnMapFile))) {
            System.out.println("Old file: " + objectInputStream.readObject());
            objectInputStream.close();
            warnMapFile.delete();
            warnMapFile.createNewFile();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(warnMapFile))) {
            System.out.println("New data: " + warnMap);
            objectOutputStream.writeObject(warnMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(warnMapFile))) {
            System.out.println("New file: " + objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class Warn implements Serializable {

    ArrayList<String> warnCauses = new ArrayList<>();
    int warns;
    long userId;

    public Warn(String cause, long userId) {
        warns++;
        warnCauses.add(cause);
        this.userId = userId;
    }

    public void newWarn(String cause) {
        warns++;
        warnCauses.add(cause);
    }

    public long getUserId() {
        return userId;
    }
}
