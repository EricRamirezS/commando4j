package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class StringArgument extends Argument<String> {

    public StringArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.STRING);
    }

    @Override
    public String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        return switch (inRange(arg)) {
            case NOT_IN_BETWEEN -> LocalizedFormat.format("Argument_String_Between", getMin(), getMax());
            case BIGGER_THAN -> LocalizedFormat.format("Argument_String_TooLong", getMax());
            case LOWER_THAN -> LocalizedFormat.format("Argument_String_TooShor", getMin());
            default -> oneOf(arg, event, Objects::toString, "Argument_String_OneOf");
        };
    }

    @Override
    public String parse(@NotNull MessageReceivedEvent event, String arg) {
        return arg;
    }
}
