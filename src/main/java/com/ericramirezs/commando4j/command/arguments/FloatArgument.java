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
 * Class to request an argument of type Float to the user.
 */
public final class FloatArgument extends Argument<FloatArgument, Double> {

    public FloatArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.FLOAT);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        try {
            Double number = Double.parseDouble(arg);
            return switch (inRange(number)) {
                case NOT_IN_BETWEEN ->
                        LocalizedFormat.format("Argument_Integer_Between", event, number, getMin(), getMax());
                case LOWER_THAN -> LocalizedFormat.format("Argument_Integer_GreaterThan", event, number, getMin());
                case BIGGER_THAN -> LocalizedFormat.format("Argument_Integer_LessThan", event, number, getMax());
                default -> oneOf(number, event, Object::toString, "Argument_Integer_OneOf");
            };
        } catch (Exception ex) {
            return LocalizedFormat.format("Argument_Integer_Invalid", event, arg);
        }
    }

    @Override
    public @NotNull Double parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        return Double.parseDouble(arg);
    }
}
