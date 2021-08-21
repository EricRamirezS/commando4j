package org.EricRamirezS.jdacommando.command.exceptions;

import org.EricRamirezS.jdacommando.command.types.BaseArgument;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

public class MissingArgumentException extends Exception{

    public MissingArgumentException(@SuppressWarnings("rawtypes") @NotNull BaseArgument argument){
        super(MessageFormat.format("Falta el argumento {0}.\n{1}", argument.getName(), argument.getPrompt()));
    }
}
