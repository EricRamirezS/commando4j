package org.EricRamirezS.jdacommando.command.exceptions;

import javax.naming.InvalidNameException;

public class InvalidCommandNameException extends InvalidNameException {

    public InvalidCommandNameException() {
        super("Command names can only contain characters.");
    }
}
