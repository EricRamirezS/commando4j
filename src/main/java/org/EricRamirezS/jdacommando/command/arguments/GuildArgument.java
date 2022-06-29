package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.entities.Guild;
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
import java.util.stream.Collectors;

public final class GuildArgument extends Argument<GuildArgument, Guild> {

    public GuildArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.GUILD);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        List<Guild> botGuilds = event.getJDA().getGuilds();

        if (arg.matches("(\\d+)")) {
            Optional<Guild> channel = botGuilds.stream()
                    .filter(c -> c.getId().equals(arg))
                    .findFirst();
            if (channel.isPresent())
                return oneOf(channel.get(), event, Guild::getName, "Argument_Guild_OneOf");
            else return LocalizedFormat.format("Argument_Guild_NotFound");
        }
        List<Guild> channels = botGuilds.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format("Argument_Guild_NotFound");
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Guild::getName, "Argument_Guild_OneOf");
        channels = botGuilds.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Guild::getName, "Argument_Guild_OneOf");
        return LocalizedFormat.format("Argument_Guild_TooMany", event);
    }

    @Override
    public @Nullable Guild parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<Guild> channel = event.getJDA().getGuilds().stream()
                    .filter(c -> c.getId().equals(arg))
                    .findFirst();
            if (channel.isPresent()) {
                return channel.get();
            }
        }
        List<Guild> channels = event.getJDA().getGuilds().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = event.getJDA().getGuilds().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
