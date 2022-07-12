/*
 *
 *    Copyright 2022 Eric Bastian Ramírez Santis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ericramirezs.commando4j.exceptions;

import com.ericramirezs.commando4j.CommandEngine;
import com.ericramirezs.commando4j.ICommandEngine;
import com.ericramirezs.commando4j.arguments.IArgument;
import com.ericramirezs.commando4j.command.ICommand;
import com.ericramirezs.commando4j.util.LocalizedFormat;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exception thrown when the user call a command missing a required argument with no default value.
 */
public class MissingArgumentException extends Exception {

    public MissingArgumentException(final ICommand command,
                                    @SuppressWarnings("rawtypes") @NotNull final IArgument argument) {
        super(processMessage(command, argument, null));
    }

    public MissingArgumentException(final ICommand command,
                                    @SuppressWarnings("rawtypes") @NotNull final IArgument argument,
                                    @NotNull final MessageReceivedEvent event) {
        super(processMessage(command, argument, event));
    }

    private static @NotNull String processMessage(final ICommand command,
                                                  @SuppressWarnings("rawtypes") @NotNull final IArgument argument,
                                                  @Nullable final MessageReceivedEvent event) {
        final ICommandEngine engine = CommandEngine.getInstance();
        final ICommand helpCommand = engine.getHelpCommand();
        String helpMessage = "";
        final String missingArgumentMessage;
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
