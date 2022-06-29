package org.EricRamirezS.jdacommando.command.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;

public class DuplicatedNameException extends Exception {

    public DuplicatedNameException(String name){
        super(LocalizedFormat.format("DevelopmentError_DuplicatedName", name));
    }

    public DuplicatedNameException(String name, MessageReceivedEvent event){
        super(LocalizedFormat.format("DevelopmentError_DuplicatedName", event, name));
    }

}
