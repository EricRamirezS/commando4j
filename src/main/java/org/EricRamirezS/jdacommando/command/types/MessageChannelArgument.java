package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class MessageChannelArgument extends Argument<MessageChannel> {

    public MessageChannelArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.MESSAGE_CHANNEL);
    }

    @Override
    public String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        List<MessageChannel> data = new ArrayList<>();
        data.addAll(event.getGuild().getTextChannels());
        data.addAll(event.getGuild().getThreadChannels());

        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<MessageChannel> channel = data.stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()) {
                return oneOf(channel.get(), event, Channel::getAsMention, "Argument_TextChannel_OneOf");
            } else {
                return LocalizedFormat.format("Argument_TextChannel_NotFound", event);
            }
        }
        List<MessageChannel> channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format("Argument_TextChannel_NotFound", event);
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Channel::getAsMention, "Argument_TextChannel_OneOf");
        channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Channel::getAsMention, "Argument_TextChannel_OneOf");
        return LocalizedFormat.format("Argument_TextChannel_TooMany", event);
    }

    @Override
    public @Nullable MessageChannel parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        List<MessageChannel> data = new ArrayList<>();
        data.addAll(event.getGuild().getTextChannels());
        data.addAll(event.getGuild().getThreadChannels());

        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<MessageChannel> channel = data.stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()) {
                return channel.get();
            }
        }
        List<MessageChannel> channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
