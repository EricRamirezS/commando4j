package examples;

import com.ericramirezs.commando4j.Slash;
import com.ericramirezs.commando4j.arguments.IArgument;
import com.ericramirezs.commando4j.command.Command;
import com.ericramirezs.commando4j.exceptions.DuplicatedArgumentNameException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HelloWorldExample extends Command implements Slash {

    public HelloWorldExample() throws DuplicatedArgumentNameException {
        super("hello", "examples", "Hello world");
    }

    @Override
    public void run(MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        sendReply(event, "Hello world");
    }

    public void run(SlashCommandInteractionEvent event, Map<String, IArgument> args) {
        sendReply(event, "Hello world");
    }
}
