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

package com.ericramirezs.commando4j.arguments;

import com.ericramirezs.commando4j.CommandEngine;
import com.ericramirezs.commando4j.enums.ArgumentTypes;
import com.ericramirezs.commando4j.util.LocalizedFormat;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Class to request an argument of type Boolean to the user.
 */
public final class BooleanArgument extends Argument<BooleanArgument, Boolean> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public BooleanArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.BOOLEAN);
    }

    @Override
    public @Nullable String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        return validate((Event) event, arg);
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        return validate((Event) event, arg);
    }

    private @Nullable String validate(@NotNull final Event event, @NotNull final String arg) {
        if (Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_YesOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).collect(Collectors.toList()).contains(arg.toLowerCase(Locale.ROOT))) return null;
        if (Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_NoOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).collect(Collectors.toList()).contains(arg.toLowerCase(Locale.ROOT))) return null;
        return LocalizedFormat.format("Argument_Boolean_Invalid", event, arg, getName());
    }

    @Override
    public @NotNull Boolean parse(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        return Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_YesOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).collect(Collectors.toList()).contains(arg.toLowerCase(Locale.ROOT));
    }

    @Override
    public Boolean parse(final SlashCommandInteractionEvent event, final String arg)  {
        return Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_YesOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).collect(Collectors.toList()).contains(arg.toLowerCase(Locale.ROOT));
    }

    @Override
    public BooleanArgument clone() {
        return clone(new BooleanArgument(getName(), getPrompt()));
    }
}
