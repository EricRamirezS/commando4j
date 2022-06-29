import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.ICommandEngine;

public class Main {

    public static void main(String[] args) throws Exception {
        ICommandEngine engine = CommandEngine.getInstance();

        JDA jda = JDABuilder.createLight(System.getenv("BOT_TOKEN"))
                .addEventListeners(engine)
                .build();

        addCommandPackages();
        engine.loadCommands();

        jda.awaitReady();
    }

    private static void addCommandPackages() {
        CommandEngine.includeBuildInUtils(); // Include Command in the engine
        CommandEngine.addCommandPackage("examples"); // Command Classpath package
    }
}
