package org.EricRamirezS.jdacommando.command.arguments;

import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;


public abstract class LocalDateTimeArgument<A extends LocalDateTimeArgument, T> extends Argument<A, T> {

    public LocalDateTimeArgument(@NotNull String name, String prompt, ArgumentTypes argumentType) {
        super(name, prompt, argumentType);
    }

    private boolean forcedDiscordTag = false;

    public A setForcedTag() {
        forcedDiscordTag = true;
        //noinspection unchecked
        return (A) this;
    }

    public boolean isForcedDiscordTag() {
        return forcedDiscordTag;
    }
}
