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

import com.ericramirezs.commando4j.util.LocalizedFormat;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Exception thrown when another Command has been registered with the same Name or Alias.
 */
public class DuplicatedNameException extends Exception {

    public DuplicatedNameException(final String name) {
        super(LocalizedFormat.format("DevelopmentError_DuplicatedName", name));
    }

    public DuplicatedNameException(final String name, final MessageReceivedEvent event) {
        super(LocalizedFormat.format("DevelopmentError_DuplicatedName", event, name));
    }
}
