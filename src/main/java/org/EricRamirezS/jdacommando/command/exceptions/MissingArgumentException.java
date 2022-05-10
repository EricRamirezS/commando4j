package org.EricRamirezS.jdacommando.command.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.types.Argument;
import org.jetbrains.annotations.NotNull;

public class MissingArgumentException extends Exception {

    public MissingArgumentException(@SuppressWarnings("rawtypes") @NotNull Argument argument) {
        super(LocalizedFormat.format("Exceptions_MissingArgument", argument.getName(), argument.getPrompt()));
    }

    public MissingArgumentException(@SuppressWarnings("rawtypes") @NotNull Argument argument, MessageReceivedEvent event) {
        super(LocalizedFormat.format("Exceptions_MissingArgument", event, argument.getName(), argument.getPrompt()));
    }
}
