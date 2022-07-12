/*
 *
 *    Copyright 2022 Eric Bastian Ramírez Santis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ericramirezs.commando4j.exceptions;

import com.ericramirezs.commando4j.CommandEngine;
import com.ericramirezs.commando4j.ICommandEngine;
import com.ericramirezs.commando4j.command.ICommand;
import com.ericramirezs.commando4j.util.LocalizedFormat;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InvalidCallException extends Exception{

    public InvalidCallException(final MessageReceivedEvent event){
        super(processMessage(event));
    }

    private static String processMessage(final MessageReceivedEvent event) {
        final ICommandEngine engine = CommandEngine.getInstance();
        final ICommand helpCommand = engine.getHelpCommand();
        String helpMessage = "";
        String missingArgumentMessage = LocalizedFormat.format("Engine_CommandCall_Failure", event);
        if (helpCommand != null && event != null) {
            helpMessage = LocalizedFormat.format("Command_HelpDefaultMessageNoCommand",
                    event,
                    engine.getPrefix(event),
                    helpCommand.getName());
        } else if (helpCommand != null) {
            helpMessage = LocalizedFormat.format("Command_HelpDefaultMessageNoCommand",
                    engine.getPrefix(),
                    helpCommand.getName());
        }

        missingArgumentMessage += "\n\n" + helpMessage;

        return missingArgumentMessage;
    }
}
