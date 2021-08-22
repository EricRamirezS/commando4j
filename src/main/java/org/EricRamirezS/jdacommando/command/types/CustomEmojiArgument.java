package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CustomEmojiArgument extends Argument<Emote> {


    public CustomEmojiArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.CUSTOM_EMOJI);
    }

    @Override
    public @Nullable String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<a?:([a-zA-Z0-9_]+):)?([0-9]+)>?$")) {
            Optional<Emote> emoji = event.getGuild().getEmotes().stream().filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent()){
                return null;
            } else {
                return MessageFormat.format("No he podido encontrar el {0} indicado", "emoji");
            }
        }

        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<Emote> emojis = event.getGuild().getEmotes().stream().filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg)).collect(Collectors.toList());
        if (emojis.size() == 0) return "No he podido encontrar el emoji indicado";
        if (emojis.size() == 1) return null;
        emojis = event.getGuild().getEmotes().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg)).collect(Collectors.toList());
        if (emojis.size() == 1) return null;
        return "Se han encontrado multiples emojis, se más específico, por favor.";
    }


    @Override
    public @Nullable Emote parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<a?:([a-zA-Z0-9_]+):)?([0-9]+)>?$")) {
            Optional<Emote> emoji = event.getGuild().getEmotes().stream().filter(e -> e.getAsMention().equals(arg)).findFirst();
            if (emoji.isPresent()){
                return emoji.get();
            }
        }

        final String lowerCaseArg = arg.toLowerCase(Locale.ROOT);

        List<Emote> emojis = event.getGuild().getEmotes().stream().filter(e -> e.getName().toLowerCase(Locale.ROOT).contains(lowerCaseArg)).collect(Collectors.toList());
        if (emojis.size() == 1) return emojis.get(0);
        emojis = event.getGuild().getEmotes().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(lowerCaseArg)).collect(Collectors.toList());
        if (emojis.size() == 1) return emojis.get(0);
        return null;
    }
}
