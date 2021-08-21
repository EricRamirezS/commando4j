package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChannelArgument extends BaseArgument<GuildChannel> {

    protected ChannelArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.CHANNEL);
    }

    @Override
    public String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("/^(?:<#)?([0-9]+)>?$/")){
            Optional<GuildChannel> channel =event.getGuild().getChannels().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()){
                return null;
            } else {
                return MessageFormat.format("No he podido encontrar el {0} indicado", "canal");
            }
        }
        List<GuildChannel> channels = event.getGuild().getChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 0) return "No he podido encontrar el canal indicado";
        if (channels.size() == 1) return null;
        channels = event.getGuild().getChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 1) return null;
        return "Se han encontrado multiples canales, se más específico, por favor.";
    }

    @Override
    public GuildChannel parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("/^(?:<#)?([0-9]+)>?$/")){
            Optional<GuildChannel> channel =event.getGuild().getChannels().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()){
                return channel.get();
            }
        }
        List<GuildChannel> channels = event.getGuild().getChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = event.getGuild().getChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
