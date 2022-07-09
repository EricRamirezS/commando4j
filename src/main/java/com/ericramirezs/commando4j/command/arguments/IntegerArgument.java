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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class to request an argument of type Integer to the user.
 */
public final class IntegerArgument extends Argument<IntegerArgument, Long> {

    public IntegerArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.INTEGER);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        try {
            Long number = Long.parseLong(arg);
            return switch (inRange(number)) {
                case NOT_IN_BETWEEN ->
                        LocalizedFormat.format("Argument_Float_Between", event, number, getMin(), getMax());
                case LOWER_THAN -> LocalizedFormat.format("Argument_Float_GreaterThan", event, number, getMin());
                case BIGGER_THAN -> LocalizedFormat.format("Argument_Float_LessThan", event, number, getMax());
                default -> oneOf(number, event, Object::toString, "Argument_Float_OneOf");
            };
        } catch (Exception ex) {
            return LocalizedFormat.format("Argument_Float_Invalid", event, arg);
        }
    }

    @Override
    public @NotNull Long parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        return Long.parseLong(arg);
    }
}
