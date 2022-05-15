import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.EricRamirezS.jdacommando.command.CommandEngine;

import java.util.Locale;

public class Main {

    public static void main(String[] args) throws Exception {

        CommandEngine engine = CommandEngine.getInstance();

        JDA jda = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))
                .addEventListeners(engine)
                .build();

        // Only required if any command is to be registered as a slash command
        engine.setJda(jda);

        addCommandPackages();

        // Load commands from  packages previously set
        engine.loadCommands();

        // optionally block until JDA is ready
        jda.awaitReady();
    }

    private static void addCommandPackages(){
        // Set packages names where Command classes are located
        CommandEngine.addCommandPackage("examples");
    }
}
