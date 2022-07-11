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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class to request an argument of type Integer to the user.
 */
public final class IntegerArgument extends NumberArgument<IntegerArgument, Long> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public IntegerArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.INTEGER);
    }

    @Override
    public @Nullable String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        return validate(event, arg,
                "Argument_Integer_OneOf",
                "Argument_Integer_Between",
                "Argument_Integer_LessThan",
                "Argument_Integer_GreaterThan",
                "Argument_Integer_Invalid");
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        return validate(event, arg,
                "Argument_Integer_OneOf",
                "Argument_Integer_Between",
                "Argument_Integer_LessThan",
                "Argument_Integer_GreaterThan",
                "Argument_Integer_Invalid");
    }

    @Override
    public @NotNull Long parse(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        return Long.parseLong(arg);
    }

    @Override
    public @NotNull Long parse(final SlashCommandInteractionEvent event, final String arg) {
        return Long.parseLong(arg);
    }

    @Override
    public IntegerArgument clone() {
        return clone(new IntegerArgument(getName(), getPrompt()));
    }
}
