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

import com.ericramirezs.commando4j.command.CommandEngine;
import com.ericramirezs.commando4j.command.enums.ArgumentTypes;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

/**
 * Class to request an argument of type Boolean to the user.
 */
public final class BooleanArgument extends Argument<BooleanArgument, Boolean> {

    public BooleanArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.BOOLEAN);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_YesOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).toList().contains(arg.toLowerCase(Locale.ROOT))) return null;
        if (Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_NoOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).toList().contains(arg.toLowerCase(Locale.ROOT))) return null;
        return LocalizedFormat.format("Argument_Boolean_Invalid", event, arg, getName());
    }

    @Override
    public @NotNull Boolean parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        return Arrays.stream(CommandEngine.getInstance().getStringArray(
                "Argument_Boolean_YesOptions",
                CommandEngine.getInstance().getLanguage(event)
        )).toList().contains(arg.toLowerCase(Locale.ROOT));
    }
}
