package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MessageArgument extends Argument<Message> {

    public MessageArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.MEMBER);
    }

    @Override
    public @Nullable String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^[0-9]+$"))
            return "El agumento ingresado no es la id de un mensaje.";
        if (event.getChannel().getHistory().getMessageById(arg) == null)
            return "No se ha encontrado el mensaje indicado";
        return null;
    }

    @Override
    public Message parse(@NotNull GuildMessageReceivedEvent event, String arg) {
        return event.getChannel().getHistory().getMessageById(arg);
    }
}
