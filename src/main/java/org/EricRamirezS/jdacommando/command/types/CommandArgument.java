package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.Command;
import org.EricRamirezS.jdacommando.command.Engine;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class CommandArgument extends Argument<Command> {

    public CommandArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.COMMAND);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        Command command = Engine.getInstance().getCommand(arg);
        if (command != null) return oneOf(command, event, Command::getName, "Argument_Command_OneOf");
        List<Command> commands = Engine.getInstance().getCommandsByPartialMatch(arg);
        if (commands.size() == 0) return LocalizedFormat.format("Argument_Command_NotFound", event);
        if (commands.size() == 1) return oneOf(commands.get(0), event, Command::getName, "Argument_Command_OneOf");
        commands = Engine.getInstance().getCommandsByExactMatch(arg);
        if (commands.size() == 1) return oneOf(commands.get(0), event, Command::getName, "Argument_Command_OneOf");
        return LocalizedFormat.format("Argument_Command_TooMany", event);
    }

    @Override
    public Command parse(@NotNull MessageReceivedEvent event, String arg) {
        Command command = Engine.getInstance().getCommand(arg);
        if (command != null) return command;
        List<Command> commands = Engine.getInstance().getCommandsByPartialMatch(arg);
        if (commands.size() == 1) return commands.get(0);
        commands = Engine.getInstance().getCommandsByExactMatch(arg);
        return commands.get(0);
    }
}
