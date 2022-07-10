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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class to request an argument of type Guild to the user.
 *
 * @see net.dv8tion.jda.api.entities.Guild
 */
public final class GuildArgument extends Argument<GuildArgument, Guild> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public GuildArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.GUILD);
    }

    @Override
    public @Nullable String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        final List<Guild> data = event.getJDA().getGuilds();

        return validateFromList(data, arg, event,
                "Argument_Guild_OneOf",
                "Argument_Guild_NotFound",
                "Argument_Guild_TooMany");
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        final List<Guild> data = event.getJDA().getGuilds();

        return validateFromList(data, arg, event,
                "Argument_Guild_OneOf",
                "Argument_Guild_NotFound",
                "Argument_Guild_TooMany");
    }

    private @Nullable String validateFromList(final List<Guild> data,
                                            final @NotNull String arg,
                                            final Event event,
                                            final String oneOfKey,
                                            final String notFoundKey,
                                            final String tooManyKey) {
        if (arg.matches("(\\d+)")) {
            final Optional<Guild> channel = data.stream()
                    .filter(c -> c.getId().equals(arg))
                    .findFirst();
            if (channel.isPresent())
                return oneOf(channel.get(), event, Guild::getName, oneOfKey);
            else return LocalizedFormat.format(notFoundKey);
        }
        List<Guild> channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format(notFoundKey);
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Guild::getName, oneOfKey);
        channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Guild::getName, oneOfKey);
        return LocalizedFormat.format(tooManyKey, event);
    }

    @Override
    public @Nullable Guild parse(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        return parseFromList(event.getJDA().getGuilds(), arg);
    }

    @Override
    public Guild parse(final SlashCommandInteractionEvent event, final String arg) {
        return parseFromList(event.getJDA().getGuilds(), arg);
    }

    private @Nullable Guild parseFromList(final List<Guild> data, final @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            final Optional<Guild> channel = data.stream()
                    .filter(c -> c.getId().equals(arg))
                    .findFirst();
            if (channel.isPresent()) {
                return channel.get();
            }
        }
        List<Guild> channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
