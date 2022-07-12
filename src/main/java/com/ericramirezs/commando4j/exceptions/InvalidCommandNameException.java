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

import javax.naming.InvalidNameException;
import java.text.MessageFormat;

/**
 * Exception thrown when the command name includes non-alphanumeric characters.
 */
public class InvalidCommandNameException extends InvalidNameException {

    public InvalidCommandNameException() {
        super("Command names can only contain characters.");
    }

    public InvalidCommandNameException(final String name) {
        super(MessageFormat.format("Command names can only contain characters, but {0} was input", name));
    }
}
