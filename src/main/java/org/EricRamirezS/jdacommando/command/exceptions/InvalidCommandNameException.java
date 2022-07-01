package org.EricRamirezS.jdacommando.command.exceptions;

import javax.naming.InvalidNameException;
import java.text.MessageFormat;

public class InvalidCommandNameException extends InvalidNameException {

    public InvalidCommandNameException() {
        super("Command names can only contain characters.");
    }

    public InvalidCommandNameException(String name) {
        super(MessageFormat.format( "Command names can only contain characters, but {0} was input", name));
    }

}
