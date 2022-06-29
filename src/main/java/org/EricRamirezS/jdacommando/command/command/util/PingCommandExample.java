package org.EricRamirezS.jdacommando.command.command.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.Slash;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public class PingCommandExample extends Command implements Slash {

    public PingCommandExample() throws Exception {
        super(
                "ping",
                "util",
                "Command_Ping_Description");
        addClientPermissions(Permission.MESSAGE_SEND);
    }

    @Override
    public String getDescription() {
        return LocalizedFormat.format(super.getDescription());
    }

    @Override
    public String getDescription(Event event) {
        return LocalizedFormat.format(super.getDescription(),event);
    }

    private static void ping(@NotNull MessageChannel channel) {
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong!") /* => RestAction<Message> */
                .queue(response /* => Message */ -> response
                        .editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue());
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        ping(event.getChannel());
    }

    @Override
    public void run(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, IArgument> args) {
        ping(event.getChannel());
    }
}
