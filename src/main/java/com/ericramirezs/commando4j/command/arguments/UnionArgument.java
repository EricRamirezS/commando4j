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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A type for command arguments that handles multiple other types.
 *
 * @see IArgument
 */
public final class UnionArgument extends Argument<UnionArgument, IArgument> {

    private final List<IArgument> arguments = new ArrayList<>();

    public UnionArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.UNION);
    }

    /**
     * Adds a new type of argument accepted by the UnionArgument
     *
     * @param argument IArgument Object
     * @return a reference to this object.
     */
    public UnionArgument addArgumentType(IArgument argument) {
        arguments.add(argument);
        return this;
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        List<IArgument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null).toList();
        if (results.size() > 0) return null;

        List<String> errors = arguments
                .stream()
                .map(a -> a.validate(event, arg))
                .filter(Objects::nonNull)
                .toList();
        return LocalizedFormat.format("Argument_Union_Error", event,
                String.join(LocalizedFormat.format("NormalText_Or", event, "\n\n", "\n\n"), errors));
    }

    @Override
    public @NotNull IArgument parse(@NotNull MessageReceivedEvent event, String arg) throws Exception {
        List<IArgument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null).toList();

        IArgument argument = results.get(0);
        //noinspection unchecked
        argument.setValue(argument.parse(event, arg));
        return argument;
    }

    @Contract(" -> new")
    public @NotNull List<IArgument> getArguments() {
        return new ArrayList<>(arguments);
    }
}
