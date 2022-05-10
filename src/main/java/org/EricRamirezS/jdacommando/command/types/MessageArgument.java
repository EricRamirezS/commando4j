package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MessageArgument extends Argument<Message> {

    public MessageArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.MESSAGE);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^\\d+$"))
            return LocalizedFormat.format("Argument_Message_Invalid", event);
        if (event.getChannel().getHistory().getMessageById(arg) == null)
            return LocalizedFormat.format("Argument_Message_NotFound", event);
        return null;
    }

    @Override
    public Message parse(@NotNull MessageReceivedEvent event, String arg) {
        return event.getChannel().getHistory().getMessageById(arg);
    }
}
