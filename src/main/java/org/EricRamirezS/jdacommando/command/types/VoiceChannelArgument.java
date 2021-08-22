package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.AbstractChannel;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class VoiceChannelArgument extends Argument<VoiceChannel> {
    public VoiceChannelArgument(@NotNull String name, @Nullable String prompt, @NotNull ArgumentTypes type) {
        super(name, prompt, type);
    }

    @Override
    public String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<#)?([0-9]+)>?$")){
            Optional<VoiceChannel> channel =event.getGuild().getVoiceChannels().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()){
                return oneOf(channel.get());
            } else {
                return MessageFormat.format("No he podido encontrar el {0} indicado", "canal");
            }
        }
        List<VoiceChannel> channels = event.getGuild().getVoiceChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 0) return "No he podido encontrar el canal indicado";
        if (channels.size() == 1) return oneOf(channels.get(0));
        channels = event.getGuild().getVoiceChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 1) return oneOf(channels.get(0));
        return "Se han encontrado multiples canales, se más específico, por favor.";
    }

    private @Nullable String oneOf(VoiceChannel voiceChannel) {
        if (isOneOf(voiceChannel))return null;
        return MessageFormat.format("Por favor, ingrese una de las siguientes opciones: \n{0}",
                getValidValues().stream().map(AbstractChannel::getName).collect(Collectors.joining("\n")));
    }

    @Override
    public @Nullable VoiceChannel parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<#)?([0-9]+)>?$")){
            Optional<VoiceChannel> channel =event.getGuild().getVoiceChannels().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()){
                return channel.get();
            }
        }
        List<VoiceChannel> channels = event.getGuild().getVoiceChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = event.getGuild().getVoiceChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
