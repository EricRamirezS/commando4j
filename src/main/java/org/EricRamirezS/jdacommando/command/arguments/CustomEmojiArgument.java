package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

public final class CustomEmojiArgument extends Argument<CustomEmojiArgument, Emote> {

    public CustomEmojiArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.CUSTOM_EMOJI);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<a?:(\\w+):)?(\\d+)>?$")) {
            Optional<Emote> emoji = event.getGuild().getEmotes().stream()
                    .filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent()) return oneOf(emoji.get(), event, Emote::getAsMention, "Argument_Emoji_OneOf");
            else return LocalizedFormat.format("Argument_Emoji_NotFound", event);
        }
        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<Emote> emojis = event.getGuild().getEmotes().stream()
                .filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg)).toList();
        if (emojis.size() == 0) return LocalizedFormat.format("Argument_Emoji_NotFound", event);
        if (emojis.size() == 1) return oneOf(emojis.get(0), event, Emote::getAsMention, "Argument_Emoji_OneOf");
        emojis = event.getGuild().getEmotes().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg)).toList();
        if (emojis.size() == 1) return null;
        return LocalizedFormat.format("Argument_Emoji_TooMany", event);
    }


    @Override
    public @Nullable Emote parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<a?:(\\w+):)?(\\d+)>?$")) {
            Optional<Emote> emoji = event.getGuild().getEmotes().stream().filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent()) {
                return emoji.get();
            }
        }

        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<Emote> emojis = event.getGuild().getEmotes().stream()
                .filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg)).toList();
        if (emojis.size() == 1) return emojis.get(0);
        emojis = event.getGuild().getEmotes().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg)).toList();
        if (emojis.size() == 1) return emojis.get(0);
        return null;
    }
}
