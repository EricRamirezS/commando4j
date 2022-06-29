package org.EricRamirezS.jdacommando.command.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;

public class DuplicatedArgumentNameException extends Exception {

    public DuplicatedArgumentNameException(){
        super(LocalizedFormat.format("DevelopmentError_DuplicatedArguments"));
    }

    public DuplicatedArgumentNameException(MessageReceivedEvent event){
        super(LocalizedFormat.format("DevelopmentError_DuplicatedArguments", event));
    }

}
