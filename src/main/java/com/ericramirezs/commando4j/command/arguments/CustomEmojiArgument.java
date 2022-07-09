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
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Class to request an argument of type RichCustomEmoji to the user.
 *
 * @see net.dv8tion.jda.api.entities.emoji.RichCustomEmoji
 */
public final class CustomEmojiArgument extends Argument<CustomEmojiArgument, RichCustomEmoji> {

    public CustomEmojiArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.CUSTOM_EMOJI);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<a?:(\\w+):)?(\\d+)>?$")) {
            Optional<RichCustomEmoji> emoji = event.getGuild().getEmojis().stream()
                    .filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent())
                return oneOf(emoji.get(), event, RichCustomEmoji::getAsMention, "Argument_Emoji_OneOf");
            else return LocalizedFormat.format("Argument_Emoji_NotFound", event);
        }
        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<RichCustomEmoji> emojis = event.getGuild().getEmojis().stream()
                .filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg)).toList();
        if (emojis.size() == 0) return LocalizedFormat.format("Argument_Emoji_NotFound", event);
        if (emojis.size() == 1)
            return oneOf(emojis.get(0), event, RichCustomEmoji::getAsMention, "Argument_Emoji_OneOf");
        emojis = event.getGuild().getEmojis().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg)).toList();
        if (emojis.size() == 1) return null;
        return LocalizedFormat.format("Argument_Emoji_TooMany", event);
    }

    @Override
    public @Nullable RichCustomEmoji parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<a?:(\\w+):)?(\\d+)>?$")) {
            Optional<RichCustomEmoji> emoji = event.getGuild().getEmojis().stream().filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent()) {
                return emoji.get();
            }
        }

        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<RichCustomEmoji> emojis = event.getGuild().getEmojis().stream()
                .filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg)).toList();
        if (emojis.size() == 1) return emojis.get(0);
        emojis = event.getGuild().getEmojis().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg)).toList();
        if (emojis.size() == 1) return emojis.get(0);
        return null;
    }
}
