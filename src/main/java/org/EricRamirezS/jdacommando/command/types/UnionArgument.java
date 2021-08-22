package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class UnionArgument extends Argument<Argument>{

    private final List<Argument> arguments = new ArrayList<>();

    public void addArgumentType(Argument argument){
        arguments.add(argument);
    }

    public UnionArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.UNION);
    }

    @Override
    public @Nullable String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        List<Argument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null)
                .collect(Collectors.toList());
        if (results.size() > 0) return null;

        List<String> errors = arguments
                .stream()
                .map(a -> a.validate(event,arg))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return String.join("\n", errors);
    }

    @Override
    public @NotNull Argument parse(@NotNull GuildMessageReceivedEvent event, String arg) {
        List<Argument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null)
                .collect(Collectors.toList());

        Argument argument = results.get(0);
        argument.setValue(argument.parse(event, arg));
        return argument;
    }

    @Contract(" -> new")
    public @NotNull List<Argument> getArguments() {
        return new ArrayList<>(arguments);
    }
}
