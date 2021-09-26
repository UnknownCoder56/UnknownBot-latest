import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class Main {

    public static DiscordApi api;
    public static void main(String[] args) {

        initData();
        api = new DiscordApiBuilder()
                .setToken(System.getenv("TOKEN"))
                .login().join();

        api.addListener(new Listener());
        api.updateActivity(ActivityType.WATCHING, " >help | UniqueApps Co.");
        System.out.println(api.createBotInvite());
    }

    @SuppressWarnings("unchecked")
    public static void initData() {
        File arrayFile = new File("C:\\Users\\Arpan\\OneDrive\\Desktop\\Bot Files\\replyArray.data");
        try {
            arrayFile.createNewFile();
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(arrayFile))) {
                Commands.customReplies = (Map<String, String>) objectInputStream.readObject();
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
