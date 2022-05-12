package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class BooleanArgument extends Argument<Boolean> {


    public BooleanArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.BOOLEAN);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_YesOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).toList().contains(arg.toLowerCase(Locale.ROOT))) return null;
        if (Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_NoOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).toList().contains(arg.toLowerCase(Locale.ROOT))) return null;
        return LocalizedFormat.format("Argument_Boolean_Invalid", event, arg, getName());
    }

    @Override
    public @NotNull Boolean parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        return Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_YesOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).toList().contains(arg.toLowerCase(Locale.ROOT));
    }
}
