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

package com.ericramirezs.commando4j.command.arguments;

import com.ericramirezs.commando4j.command.CommandEngine;
import com.ericramirezs.commando4j.command.command.ICommand;
import com.ericramirezs.commando4j.command.enums.ArgumentTypes;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Class to request an argument of type ICommand to the user.
 *
 * @see ICommand
 */
public final class CommandArgument extends Argument<CommandArgument, ICommand> {

    public CommandArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.COMMAND);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        ICommand command = CommandEngine.getInstance().getCommand(arg);
        if (command != null) return oneOf(command, event, ICommand::getName, "Argument_Command_OneOf");
        List<ICommand> commands = CommandEngine.getInstance().getCommandsByPartialMatch(arg);
        if (commands.size() == 0) return LocalizedFormat.format("Argument_Command_NotFound", event);
        if (commands.size() == 1) return oneOf(commands.get(0), event, ICommand::getName, "Argument_Command_OneOf");
        commands = CommandEngine.getInstance().getCommandsByExactMatch(arg);
        if (commands.size() == 1) return oneOf(commands.get(0), event, ICommand::getName, "Argument_Command_OneOf");
        return LocalizedFormat.format("Argument_Command_TooMany", event);
    }

    @Override
    public ICommand parse(@NotNull MessageReceivedEvent event, String arg) {
        ICommand command = CommandEngine.getInstance().getCommand(arg);
        if (command != null) return command;
        List<ICommand> commands = CommandEngine.getInstance().getCommandsByPartialMatch(arg);
        if (commands.size() == 1) return commands.get(0);
        commands = CommandEngine.getInstance().getCommandsByExactMatch(arg);
        return commands.get(0);
    }
}
