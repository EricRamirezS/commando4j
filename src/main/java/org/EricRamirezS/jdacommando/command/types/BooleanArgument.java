package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

public final class BooleanArgument extends Argument<Boolean> {


    public BooleanArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.BOOLEAN);
    }

    private static final Set<String> TRUE = new HashSet<String>(){{
        addAll(Arrays.asList("true", "t", "yes", "y", "on", "enable", "enabled", "1", "+", "sí", "si", "s", "verdadero"));
    }};

    private static final Set<String> FALSE = new HashSet<String>(){{
        addAll(Arrays.asList("false", "f", "no", "n", "off", "disable", "disabled", "0", "-", "falso"));
    }};

    @Override
    public @Nullable String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (TRUE.contains(arg.toLowerCase(Locale.ROOT))) return null;
        if (FALSE.contains(arg.toLowerCase(Locale.ROOT))) return null;
        return MessageFormat.format("`{0}` no es un valor válido para el parámetro `{1}`", arg, getName());
    }

    @Override
    public @NotNull Boolean parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        return TRUE.contains(arg.toLowerCase(Locale.ROOT));
    }
}
