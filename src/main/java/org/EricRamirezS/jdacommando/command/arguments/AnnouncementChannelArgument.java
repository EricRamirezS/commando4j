package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.NewsChannel;
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

public final class AnnouncementChannelArgument extends Argument<AnnouncementChannelArgument, NewsChannel> {

    public AnnouncementChannelArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.ANNOUNCEMENT_CHANNEL);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<NewsChannel> channel = event.getGuild().getNewsChannels().stream()
                    .filter(c -> c.getAsMention().equals(arg))
                    .findFirst();
            if (channel.isPresent())
                return oneOf(channel.get(), event, IMentionable::getAsMention, "Argument_AnnouncementChannel_OneOf");
            else return LocalizedFormat.format("Argument_AnnouncementChannel_NotFound");
        }
        List<NewsChannel> channels = event.getGuild().getNewsChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format("Argument_AnnouncementChannel_NotFound");
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, "Argument_AnnouncementChannel_OneOf");
        channels = event.getGuild().getNewsChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, "Argument_AnnouncementChannel_OneOf");
        return LocalizedFormat.format("Argument_AnnouncementChannel_TooMany", event);
    }

    @Override
    public @Nullable NewsChannel parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<#)?(\\d+)>?$")) {
            Optional<NewsChannel> channel = event.getGuild().getNewsChannels().stream()
                    .filter(c -> c.getAsMention().equals(arg))
                    .findFirst();
            if (channel.isPresent()) {
                return channel.get();
            }
        }
        List<NewsChannel> channels = event.getGuild().getNewsChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = event.getGuild().getNewsChannels().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
