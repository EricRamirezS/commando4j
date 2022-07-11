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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A type for command arguments that handles multiple other types.
 * <p>
 *     It will try to parse arguments in the same order they're added. The first success will be the Value of this argument.
 * </p>
 * <p>
 *     Make sure to use <strong>instanceof</strong> to check what of Argument was the valid one.
 * </p>
 *
 * @see IArgument
 */
@SuppressWarnings("ALL")
public final class UnionArgument extends Argument<UnionArgument, IArgument> {

    private final List<IArgument> arguments = new ArrayList<>();

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public UnionArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.UNION);
    }

    /**
     * Adds a new type of argument accepted by the UnionArgument
     *
     * @param argument IArgument Object
     * @return a reference to this object.
     */
    public UnionArgument addArgumentType(final IArgument argument) {
        arguments.add(argument);
        return this;
    }

    @Override
    public @Nullable String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        final List<IArgument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null).collect(Collectors.toList());
        if (results.size() > 0) return null;

        final List<String> errors = arguments
                .stream()
                .map(a -> a.validate(event, arg))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return LocalizedFormat.format("Argument_Union_Error", event,
                String.join(LocalizedFormat.format("NormalText_Or", event, "\n\n", "\n\n"), errors));
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        final List<IArgument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null).collect(Collectors.toList());
        if (results.size() > 0) return null;

        final List<String> errors = arguments
                .stream()
                .map(a -> a.validate(event, arg))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return LocalizedFormat.format("Argument_Union_Error", event,
                String.join(LocalizedFormat.format("NormalText_Or", event, "\n\n", "\n\n"), errors));
    }

    @Override
    public @NotNull IArgument parse(@NotNull final MessageReceivedEvent event, final String arg) throws Exception {
        final List<IArgument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null).collect(Collectors.toList());

        final IArgument argument = results.get(0);
        //noinspection unchecked
        argument.setValue(argument.parse(event, arg));
        return argument;
    }

    @Override
    public IArgument parse(final SlashCommandInteractionEvent event, final String arg) throws Exception {
        final List<IArgument> results = arguments
                .stream()
                .filter(a -> a.validate(event, arg) == null).collect(Collectors.toList());

        final IArgument argument = results.get(0);
        //noinspection unchecked
        argument.setValue(argument.parse(event, arg));
        return argument;
    }

    @Contract(" -> new")
    public @NotNull List<IArgument> getArguments() {
        return new ArrayList<>(arguments);
    }

    @Override
    public UnionArgument clone() {
        UnionArgument a = clone(new UnionArgument(getName(), getPrompt()));
        for (IArgument arg : arguments) {
            a.addArgumentType(arg.clone());
        }
        return a;
    }
}
