package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.Command;
import org.EricRamirezS.jdacommando.command.CommandConfig;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandArgument extends Argument<Command> {

    public CommandArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.COMMAND);
    }

    @Override
    public @Nullable String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        Command command = CommandConfig.getCommand(arg);
        if (command != null) return null;
        List<Command> commands = CommandConfig.getCommandsByPartialMatch(arg);
        if (commands.size() == 0) return MessageFormat.format("No se ha encontrado ningún comando con el nombre ``", arg);
        if (commands.size() == 1) return null;
        return "Se han encontrado multiples comandos, se más específico, por favor.";
    }

    @Override
    public Command parse(@NotNull GuildMessageReceivedEvent event, String arg) {
        Command command = CommandConfig.getCommand(arg);
        if (command != null) return command;
        List<Command> commands = CommandConfig.getCommandsByPartialMatch(arg);
        return commands.get(0);
    }

    private @Nullable String oneOf(Command command){
        if (isOneOf(command))return null;
        return MessageFormat.format("Por favor, ingrese una de las siguientes opciones: \n{0}",
                getValidValues().stream().map(Command::getName).collect(Collectors.joining("\n")));
    }

}
