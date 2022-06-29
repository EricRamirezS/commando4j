package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.TextChannel;
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

public final class TextChannelArgument extends Argument<TextChannelArgument, TextChannel> {

    public TextChannelArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.TEXT_CHANNEL);
    }

    @Override
    public String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<TextChannel> channel = event.getGuild().getTextChannels().stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()) {
                return oneOf(channel.get(), event, Channel::getAsMention, "Argument_TextChannel_OneOf");
            } else {
                return LocalizedFormat.format("Argument_TextChannel_NotFound", event);
            }
        }
        List<TextChannel> channels = event.getGuild().getTextChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format("Argument_TextChannel_NotFound", event);
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Channel::getAsMention, "Argument_TextChannel_OneOf");
        channels = event.getGuild().getTextChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Channel::getAsMention, "Argument_TextChannel_OneOf");
        return LocalizedFormat.format("Argument_TextChannel_TooMany", event);
    }

    @Override
    public @Nullable TextChannel parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<TextChannel> channel = event.getGuild().getTextChannels().stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()) {
                return channel.get();
            }
        }
        List<TextChannel> channels = event.getGuild().getTextChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = event.getGuild().getTextChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
