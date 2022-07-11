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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class to request an argument of type RichCustomEmoji to the user.
 *
 * @see net.dv8tion.jda.api.entities.emoji.RichCustomEmoji
 */
public final class CustomEmojiArgument extends Argument<CustomEmojiArgument, RichCustomEmoji> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public CustomEmojiArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.CUSTOM_EMOJI);
    }

    @Override
    public @Nullable String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        if (arg.matches("^(?:<a?:(\\w+):)?(\\d+)>?$")) {
            final Optional<RichCustomEmoji> emoji = event.getGuild().getEmojis().stream()
                    .filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent())
                return oneOf(emoji.get(), event, RichCustomEmoji::getAsMention, "Argument_Emoji_OneOf");
            else return LocalizedFormat.format("Argument_Emoji_NotFound", event);
        }
        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<RichCustomEmoji> emojis = event.getGuild().getEmojis().stream()
                .filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg))
                .collect(Collectors.toList());
        if (emojis.size() == 0) return LocalizedFormat.format("Argument_Emoji_NotFound", event);
        if (emojis.size() == 1)
            return oneOf(emojis.get(0), event, RichCustomEmoji::getAsMention, "Argument_Emoji_OneOf");
        emojis = event.getGuild().getEmojis().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg))
                .collect(Collectors.toList());
        if (emojis.size() == 1) return null;
        return LocalizedFormat.format("Argument_Emoji_TooMany", event);
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        if (arg.matches("^(?:<a?:(\\w+):)?(\\d+)>?$")) {
            final Optional<RichCustomEmoji> emoji = Objects.requireNonNull(event.getGuild()).getEmojis().stream()
                    .filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent())
                return oneOf(emoji.get(), event, RichCustomEmoji::getAsMention, "Argument_Emoji_OneOf");
            else return LocalizedFormat.format("Argument_Emoji_NotFound", event);
        }
        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<RichCustomEmoji> emojis = Objects.requireNonNull(event.getGuild()).getEmojis().stream()
                .filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg))
                .collect(Collectors.toList());
        if (emojis.size() == 0) return LocalizedFormat.format("Argument_Emoji_NotFound", event);
        if (emojis.size() == 1)
            return oneOf(emojis.get(0), event, RichCustomEmoji::getAsMention, "Argument_Emoji_OneOf");
        emojis = event.getGuild().getEmojis().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg))
                .collect(Collectors.toList());
        if (emojis.size() == 1) return null;
        return LocalizedFormat.format("Argument_Emoji_TooMany", event);
    }

    @Override
    public @Nullable RichCustomEmoji parse(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        if (arg.matches("^(?:<a?:(\\w+):)?(\\d+)>?$")) {
            final Optional<RichCustomEmoji> emoji = event.getGuild().getEmojis().stream()
                    .filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent()) {
                return emoji.get();
            }
        }

        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<RichCustomEmoji> emojis = event.getGuild().getEmojis().stream()
                .filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg))
                .collect(Collectors.toList());
        if (emojis.size() == 1) return emojis.get(0);
        emojis = event.getGuild().getEmojis().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg))
                .collect(Collectors.toList());
        if (emojis.size() == 1) return emojis.get(0);
        return null;
    }

    @Override
    public RichCustomEmoji parse(final SlashCommandInteractionEvent event, final String arg) {
        if (arg.matches("^(?:<a?:(\\w+):)?(\\d+)>?$")) {
            final Optional<RichCustomEmoji> emoji = Objects.requireNonNull(event.getGuild()).getEmojis()
                    .stream().filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent()) {
                return emoji.get();
            }
        }

        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<RichCustomEmoji> emojis = Objects.requireNonNull(event.getGuild()).getEmojis().stream()
                .filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg))
                .collect(Collectors.toList());
        if (emojis.size() == 1) return emojis.get(0);
        emojis = event.getGuild().getEmojis().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg))
                .collect(Collectors.toList());
        if (emojis.size() == 1) return emojis.get(0);
        return null;
    }

    @Override
    public CustomEmojiArgument clone() {
        return clone(new CustomEmojiArgument(getName(), getPrompt()));
    }
}
