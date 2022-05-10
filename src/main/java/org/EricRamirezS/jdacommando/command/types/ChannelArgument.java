package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ChannelArgument extends Argument<GuildChannel> {

    public ChannelArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.CHANNEL);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<GuildChannel> channel = event.getGuild().getChannels().stream()
                    .filter(c -> c.getAsMention().equals(arg))
                    .findFirst();
            if (channel.isPresent())
                return oneOf(channel.get(), event, IMentionable::getAsMention, "Argument_Channel_OneOf");
            else return LocalizedFormat.format("Argument_Channel_NotFound");
        }
        List<GuildChannel> channels = event.getGuild().getChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format("Argument_Channel_NotFound");
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, "Argument_Channel_OneOf");
        channels = event.getGuild().getChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, "Argument_Channel_OneOf");
        return LocalizedFormat.format("Argument_Channel_TooMany", event);
    }

    @Override
    public @Nullable GuildChannel parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<GuildChannel> channel = event.getGuild().getChannels().stream()
                    .filter(c -> c.getAsMention().equals(arg))
                    .findFirst();
            if (channel.isPresent()) {
                return channel.get();
            }
        }
        List<GuildChannel> channels = event.getGuild().getChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = event.getGuild().getChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
