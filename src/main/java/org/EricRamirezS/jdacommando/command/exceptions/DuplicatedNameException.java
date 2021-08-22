package org.EricRamirezS.jdacommando.command.exceptions;

import java.text.MessageFormat;

public class DuplicatedNameException extends Exception {

    public DuplicatedNameException(String name){
        super(MessageFormat.format("Ya existe un comando con el nombre/alias `{0}`", name));
    }
}
