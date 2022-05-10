package org.EricRamirezS.jdacommando.command.exceptions;

import org.EricRamirezS.jdacommando.command.types.Argument;
import org.jetbrains.annotations.NotNull;

public class InvalidValueException extends Exception {

    public InvalidValueException(@SuppressWarnings("rawtypes") @NotNull Argument argument, String message) {
        super(argument.getName() + ": " + message);
    }
}
