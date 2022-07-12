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

import com.ericramirezs.commando4j.enums.ArgumentTypes;
import com.ericramirezs.commando4j.util.LocalizedFormat;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
abstract class GenericChannelArgument<A extends GenericChannelArgument, T extends Channel> extends Argument<A, T> {

    GenericChannelArgument(@NotNull final String name,
                           @NotNull final String prompt,
                           @NotNull final ArgumentTypes type) {
        super(name, prompt, type);
    }

    final @Nullable String validateFromList(final List<T> data,
                                            final @NotNull String arg,
                                            final Event event,
                                            final String oneOfKey,
                                            final String notFoundKey,
                                            final String tooManyKey) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            final Optional<T> channel = data.stream()
                    .filter(c -> c.getAsMention().equals(arg))
                    .findFirst();
            if (channel.isPresent())
                return oneOf(channel.get(), event, IMentionable::getAsMention, oneOfKey);
            else return LocalizedFormat.format(notFoundKey);
        }
        List<T> channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 0) return LocalizedFormat.format(notFoundKey);
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, oneOfKey);
        channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, oneOfKey);
        return LocalizedFormat.format(tooManyKey, event);
    }

    final @Nullable T parseFromList(final List<T> data, final @NotNull String arg) {

        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            final Optional<T> channel = data.stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()) {
                return channel.get();
            }
        }

        List<T> channels = data.stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = data.stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
