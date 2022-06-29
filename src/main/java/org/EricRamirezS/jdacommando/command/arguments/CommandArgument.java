package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.command.ICommand;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

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
