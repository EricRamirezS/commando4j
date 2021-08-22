package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.stream.Collectors;

public final class IntegerArgument extends Argument<Integer> {

    public IntegerArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.FLOAT);
    }

    @Override
    public @Nullable String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        try {
            Integer number = Integer.parseInt(arg);
            switch (inRange(number)){
                case NOT_IN_BETWEEN:
                    return MessageFormat.format("{0} debe estar entre {1} y {2}", number, getMin(), getMax());
                case LOWER_THAN:
                    return MessageFormat.format("{0} debe ser mayor que {1}", number, getMin());
                case BIGGER_THAN:
                    return MessageFormat.format("{0} debe ser menor que {1}", number, getMax());
            }
            if (!isOneOf(number)){
                return MessageFormat.format("Por favor, ingrese una de las siguientes opciones: \n{0}",
                        getValidValues().stream().map(Object::toString).collect(Collectors.joining("\n")));
            }
            return null;
        } catch (Exception ex){
            return MessageFormat.format("`{0}` no es un número entero", arg);
        }
    }

    @Override
    public @NotNull Integer parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        return Integer.parseInt(arg);
    }
}
