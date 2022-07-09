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
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Class to request an argument of type AudioChannel to the user.
 *
 * @see net.dv8tion.jda.api.entities.AudioChannel
 */
public final class AudioChannelArgument extends Argument<AudioChannelArgument, AudioChannel> {

    public AudioChannelArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.AUDIO_CHANNEL);
    }

    @Override
    public String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        List<AudioChannel> audioChannels = new ArrayList<>();
        audioChannels.addAll(event.getGuild().getVoiceChannels());
        audioChannels.addAll(event.getGuild().getStageChannels());

        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<AudioChannel> channel = audioChannels.stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent())
                return oneOf(channel.get(), event, Channel::getName, "Argument_VoiceChannel_OneOf");
            else return LocalizedFormat.format("Argument_VoiceChannel_NotFound", event);
        }
        List<AudioChannel> channels = audioChannels.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format("Argument_VoiceChannel_NotFound", event);
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Channel::getName, "Argument_VoiceChannel_OneOf");
        channels = audioChannels.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Channel::getName, "Argument_VoiceChannel_OneOf");
        return LocalizedFormat.format("Argument_VoiceChannel_TooMany", event);
    }

    @Override
    public @Nullable AudioChannel parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        List<AudioChannel> audioChannels = new ArrayList<>();
        audioChannels.addAll(event.getGuild().getVoiceChannels());
        audioChannels.addAll(event.getGuild().getStageChannels());

        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<AudioChannel> channel = audioChannels.stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()) {
                return channel.get();
            }
        }
        List<AudioChannel> channels = audioChannels.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = audioChannels.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
