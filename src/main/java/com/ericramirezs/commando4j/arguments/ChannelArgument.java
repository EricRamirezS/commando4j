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
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to request an argument of type GuildChannel to the user.
 *
 * @see net.dv8tion.jda.api.entities.GuildChannel
 */
public final class ChannelArgument extends GenericChannelArgument<ChannelArgument, GuildChannel> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public ChannelArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.CHANNEL);
    }

    @Override
    public @Nullable String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        final List<GuildChannel> data = new ArrayList<>();
        data.addAll(event.getGuild().getChannels());
        data.addAll(event.getGuild().getThreadChannels());

        return validateFromList(data, arg, event,
                "Argument_Channel_OneOf",
                "Argument_Channel_NotFound",
                "Argument_Channel_TooMany");
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        final List<GuildChannel> data = new ArrayList<>();
        data.addAll(Objects.requireNonNull(event.getGuild()).getChannels());
        data.addAll(event.getGuild().getThreadChannels());

        return validateFromList(data, arg, event,
                "Argument_Channel_OneOf",
                "Argument_Channel_NotFound",
                "Argument_Channel_TooMany");
    }

    @Override
    public @Nullable GuildChannel parse(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        final List<GuildChannel> data = new ArrayList<>();
        data.addAll(event.getGuild().getChannels());
        data.addAll(event.getGuild().getThreadChannels());

        return parseFromList(data, arg);
    }

    @Override
    public GuildChannel parse(final SlashCommandInteractionEvent event, final String arg) {
        final List<GuildChannel> data = new ArrayList<>();
        data.addAll(Objects.requireNonNull(event.getGuild()).getChannels());
        data.addAll(event.getGuild().getThreadChannels());

        return parseFromList(data, arg);
    }

    @Override
    public ChannelArgument clone() {
        return clone(new ChannelArgument(getName(), getPrompt()));
    }
}
