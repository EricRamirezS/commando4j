package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class ThreadChannelArgument extends Argument<ThreadChannel> {

    public ThreadChannelArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.THREAD_CHANNEL);
    }

    @Override
    public String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<ThreadChannel> channel = event.getGuild().getThreadChannels().stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()) {
                return oneOf(channel.get(), event, IMentionable::getAsMention, "Argument_ThreadChannel_OneOf");
            } else {
                return LocalizedFormat.format("Argument_ThreadChannel_NotFound", event);
            }
        }
        List<ThreadChannel> channels = event.getGuild().getThreadChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format("Argument_ThreadChannel_NotFound", event);
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Channel::getAsMention, "Argument_ThreadChannel_OneOf");
        channels = event.getGuild().getThreadChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, Channel::getAsMention, "Argument_ThreadChannel_OneOf");
        return LocalizedFormat.format("Argument_ThreadChannel_TooMany", event);
    }

    @Override
    public @Nullable ThreadChannel parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<ThreadChannel> channel = event.getGuild().getThreadChannels().stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()) {
                return channel.get();
            }
        }
        List<ThreadChannel> channels = event.getGuild().getThreadChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = event.getGuild().getThreadChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}