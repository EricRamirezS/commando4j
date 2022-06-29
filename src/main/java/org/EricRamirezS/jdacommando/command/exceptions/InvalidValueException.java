package org.EricRamirezS.jdacommando.command.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.ICommandEngine;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.command.ICommand;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvalidValueException extends Exception {

    public InvalidValueException(@SuppressWarnings("rawtypes") @NotNull IArgument argument, String message, Command command, MessageReceivedEvent event) {
        super(processMessage(command, argument, message,event));
    }

    private static @NotNull String processMessage(ICommand command,
                                                  @SuppressWarnings("rawtypes") @NotNull IArgument argument,
                                                  String message,
                                                  @Nullable MessageReceivedEvent event) {
        ICommandEngine engine = CommandEngine.getInstance();
        ICommand helpCommand = engine.getHelpCommand();
        String helpMessage = "";
        String missingArgumentMessage = argument.getName() + ": " + message;
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

        missingArgumentMessage += "\n\n" + helpMessage;
        return missingArgumentMessage.trim();
    }
}
