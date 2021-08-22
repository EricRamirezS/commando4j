package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

public final class StringArgument extends Argument<String> {

    public StringArgument(@NotNull String name, @Nullable String prompt, @NotNull ArgumentTypes type) {
        super(name, prompt, type);
    }

    @Override
    public String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        switch (inRange(arg)){
            case BIGGER_THAN:
                return MessageFormat.format("El texto no puede tener más de {0} caracteres", getMax());
            case LOWER_THAN:
                return MessageFormat.format("El texto no puede tener menos de {0} caracteres", getMin());
            case NOT_IN_BETWEEN:
                return MessageFormat.format("El número de caracteres debe estar entre {0} y {1}", getMin(), getMax());

        }
        return oneOf(arg);
    }

    private @Nullable String oneOf(String arg) {
        if (isOneOf(arg)) return null;
        return MessageFormat.format("Por favor, ingrese una de las siguientes opciones: \n{0}",
                String.join("\n", getValidValues()));
    }

    @Override
    public String parse(@NotNull GuildMessageReceivedEvent event, String arg) {
        return arg;
    }
}
