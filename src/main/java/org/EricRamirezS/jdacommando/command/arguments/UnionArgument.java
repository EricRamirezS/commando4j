package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class UnionArgument extends Argument<UnionArgument, IArgument> {

    private final List<IArgument> arguments = new ArrayList<>();

    public UnionArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.UNION);
    }

    public UnionArgument addArgumentType(IArgument argument) {
        arguments.add(argument);
        return this;
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        List<IArgument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null).toList();
        if (results.size() > 0) return null;

        List<String> errors = arguments
                .stream()
                .map(a -> a.validate(event, arg))
                .filter(Objects::nonNull)
                .toList();
        return LocalizedFormat.format("Argument_Union_Error", event,
                String.join(LocalizedFormat.format("NormalText_Or", event, "\n\n", "\n\n"), errors));
    }

    @Override
    public @NotNull IArgument parse(@NotNull MessageReceivedEvent event, String arg) {
        List<IArgument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null).toList();

        IArgument argument = results.get(0);
        //noinspection unchecked
        argument.setValue(argument.parse(event, arg));
        return argument;
    }

    @Contract(" -> new")
    public @NotNull List<IArgument> getArguments() {
        return new ArrayList<>(arguments);
    }
}
