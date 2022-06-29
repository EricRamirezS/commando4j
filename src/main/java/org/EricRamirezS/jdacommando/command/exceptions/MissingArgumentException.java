package org.EricRamirezS.jdacommando.command.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.ICommandEngine;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.command.ICommand;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MissingArgumentException extends Exception {

    public MissingArgumentException(ICommand command,
                                    @SuppressWarnings("rawtypes") @NotNull IArgument argument) {
        super(processMessage(command, argument, null));
    }

    public MissingArgumentException(ICommand command,
                                    @SuppressWarnings("rawtypes") @NotNull IArgument argument,
                                    @NotNull MessageReceivedEvent event) {
        super(processMessage(command, argument, event));
    }

    private static @NotNull String processMessage(ICommand command,
                                                  @SuppressWarnings("rawtypes") @NotNull IArgument argument,
                                                  @Nullable MessageReceivedEvent event) {
        ICommandEngine engine = CommandEngine.getInstance();
        ICommand helpCommand = engine.getHelpCommand();
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
            missingArgumentMessage = LocalizedFormat.format("UserError_MissingArgument",
                    event,
                    argument.getName(),
                    argument.getType().toString(event),
                    argument.getPrompt(event),
                    helpMessage);
        } else {
            missingArgumentMessage = LocalizedFormat.format("UserError_MissingArgument",
                    argument.getName(),
                    argument.getType().toString(),
                    argument.getPrompt(),
                    helpMessage);
        }
        return missingArgumentMessage.trim();
    }
}
