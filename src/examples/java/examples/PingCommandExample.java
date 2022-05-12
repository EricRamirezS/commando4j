package examples;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.Command;
import org.EricRamirezS.jdacommando.command.Slash;
import org.EricRamirezS.jdacommando.command.types.Argument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public class PingCommandExample extends Command implements Slash {

    public PingCommandExample() throws Exception {
        super(
                "ping",
                "utils",
                "Ping is an utility that acts as a test to see if a networked device is reachable.");
        /* Settings with default values
        setGuildOnly(false);
        setPrivateUseOnly(false);
        setRunInThread(true);
        setThreadOnly(false);
        setNsfw(false);
          */
        addClientPermissions(Permission.MESSAGE_SEND);
    }

    private static void ping(@NotNull MessageChannel channel) {
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong!") /* => RestAction<Message> */
                .queue(response /* => Message */ -> response
                        .editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue());
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @UnmodifiableView @NotNull Map<String, Argument> args) {
        ping(event.getChannel());
    }

    @Override
    public void runSlash(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, Argument> args) {
        ping(event.getChannel());
    }
}
