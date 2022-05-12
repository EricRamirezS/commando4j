package examples;

import edu.rice.cs.util.ArgumentTokenizer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.Command;
import org.EricRamirezS.jdacommando.command.Slash;
import org.EricRamirezS.jdacommando.command.types.Argument;
import org.EricRamirezS.jdacommando.command.types.StringArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChooseCommandExample extends Command implements Slash {

    public ChooseCommandExample() throws Exception {
        super(
                "choose",
                "utils",
                "Chooses a random option from a given list. Options separated by whitespaces",
                new StringArgument("options", "list of options")
                        .setRequired(true)
        );
        setGuildOnly(false);
        setPrivateUseOnly(false);
        setRunInThread(true);
        setThreadOnly(false);
        setNsfw(false);

        addClientPermissions(Permission.MESSAGE_SEND);
    }

    private static void ping(@NotNull MessageChannel channel, @NotNull List<String> options) {
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @UnmodifiableView @NotNull Map<String, Argument> args) {
        String optionsRaw = args.get("options").getValue().toString();
        List<String> options = ArgumentTokenizer.tokenize(optionsRaw);
        MessageChannel channel = event.getChannel();
        Random rand = new Random();
        String randomElement = options.get(rand.nextInt(options.size()));
        channel.sendMessage(randomElement).queue();

    }

    @Override
    public void runSlash(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, Argument> args) {
        String optionsRaw = args.get("options").getValue().toString();
        List<String> options = ArgumentTokenizer.tokenize(optionsRaw);
        Random rand = new Random();
        String randomElement = options.get(rand.nextInt(options.size()));
        event.reply(randomElement).setEphemeral(true).queue();
    }
}
