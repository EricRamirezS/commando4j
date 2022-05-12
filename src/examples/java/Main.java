import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.EricRamirezS.jdacommando.command.CommandEngine;

import java.util.Locale;

public class Main {

    public static void main(String[] args) throws Exception {

        CommandEngine engine = CommandEngine.getInstance();

        // Set's the default language of the engine
        // If not set, English US will be used
        engine.setLanguage(new Locale("en", "US"));

        // Note: It is important to register your ReadyListener before building
        JDA jda = JDABuilder.createDefault("OTczNTE5ODg0NTMxMTUwODY4.GPjWE6.ZM0LQR_baV9q5pq7-IdGfQcwKXYKqCA2TlLPsw")
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
