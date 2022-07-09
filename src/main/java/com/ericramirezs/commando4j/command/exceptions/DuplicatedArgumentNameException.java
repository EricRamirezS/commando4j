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

package com.ericramirezs.commando4j.command.exceptions;

import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Exception thrown when another Argument with the same name has been registered in a Command.
 */
public class DuplicatedArgumentNameException extends Exception {

    public DuplicatedArgumentNameException() {
        super(LocalizedFormat.format("DevelopmentError_DuplicatedArguments"));
    }

    public DuplicatedArgumentNameException(MessageReceivedEvent event) {
        super(LocalizedFormat.format("DevelopmentError_DuplicatedArguments", event));
    }
}
