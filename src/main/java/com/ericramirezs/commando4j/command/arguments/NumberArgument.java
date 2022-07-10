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

package com.ericramirezs.commando4j.command.arguments;

import com.ericramirezs.commando4j.command.enums.ArgumentTypes;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

abstract class NumberArgument<A extends Argument, T extends Number> extends Argument<A, T> {

    /**
     * Creates an instance of an Argument Object.
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     * @param type   Category of the argument implementation.
     */
    NumberArgument(@NotNull final String name,
                             @NotNull final String prompt,
                             @NotNull final ArgumentTypes type) {
        super(name, prompt, type);
    }

    final String validate(final Event event,
                                    final String arg,
                                    final String oneOfKey,
                                    final String betweenKey,
                                    final String lessThanKey,
                                    final String GreaterThanKey,
                                    final String invalidKey) {
        try {
            final T number;
            if (event instanceof MessageReceivedEvent e) number = parse(e, arg);
            else if (event instanceof SlashCommandInteractionEvent e) number = parse(e, arg);
            else throw new Exception();
            return switch (inRange(number)) {
                case NOT_IN_BETWEEN -> LocalizedFormat.format(betweenKey, event, number, getMin(), getMax());
                case LOWER_THAN -> LocalizedFormat.format(GreaterThanKey, event, number, getMin());
                case BIGGER_THAN -> LocalizedFormat.format(lessThanKey, event, number, getMax());
                default -> oneOf(number, event, Object::toString, oneOfKey);
            };
        } catch (final Exception ex) {
            return LocalizedFormat.format("Argument_Float_Invalid", event, arg);
        }
    }
}
