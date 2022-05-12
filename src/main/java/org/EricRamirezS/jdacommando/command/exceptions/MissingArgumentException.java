package org.EricRamirezS.jdacommando.command.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.Command;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.types.Argument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MissingArgumentException extends Exception {

    public MissingArgumentException(Command command,
                                    @SuppressWarnings("rawtypes") @NotNull Argument argument) {
        super(processMessage(command, argument, null));
    }

    public MissingArgumentException(Command command,
                                    @SuppressWarnings("rawtypes") @NotNull Argument argument,
                                    @NotNull MessageReceivedEvent event) {
        super(processMessage(command, argument, event));
    }

    private static @NotNull String processMessage(Command command,
                                                  @SuppressWarnings("rawtypes") @NotNull Argument argument,
                                                  @Nullable MessageReceivedEvent event) {
        CommandEngine engine = CommandEngine.getInstance();
        Command helpCommand = engine.getHelpCommand();
        String helpMessage = "";
        String missingArgumentMessage;
        if (helpCommand != null && event != null) {
            helpMessage = LocalizedFormat.format("Command_HelpDefaultMessage",
                    event,
                    engine.getPrefix(event),
                    helpCommand.getName(),
                    command.getName());
        } else if (helpCommand != null) {
            helpMessage = LocalizedFormat.format("Command_HelpDefaultMessage",
                    engine.getPrefix(),
                    helpCommand.getName(),
                    command.getName());
        }
        if (event != null) {
            missingArgumentMessage = LocalizedFormat.format("Exceptions_MissingArgument",
                    event,
                    argument.getName(),
                    argument.getType().toString(event),
                    argument.getPrompt(),
                    helpMessage);
        } else {
            missingArgumentMessage = LocalizedFormat.format("Exceptions_MissingArgument",
                    argument.getName(),
                    argument.getType().toString(),
                    argument.getPrompt(),
                    helpMessage);
        }
        return missingArgumentMessage.trim();
    }
}
