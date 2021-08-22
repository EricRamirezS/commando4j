package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TextChannelArgument extends Argument<TextChannel> {

    public TextChannelArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.TEXT_CHANNEL);
    }

    @Override
    public String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<#)?([0-9]+)>?$")){
            Optional<TextChannel> channel =event.getGuild().getTextChannels().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()){
                return oneOf(channel.get());
            } else {
                return MessageFormat.format("No he podido encontrar el {0} indicado", "canal");
            }
        }
        List<TextChannel> channels = event.getGuild().getTextChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 0) return "No he podido encontrar el canal indicado";
        if (channels.size() == 1) return oneOf(channels.get(0));
        channels = event.getGuild().getTextChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 1) return oneOf(channels.get(0));
        return "Se han encontrado multiples canales, se más específico, por favor.";
    }

    private @Nullable String oneOf(TextChannel textChannel) {
        if (isOneOf(textChannel))return null;
        return MessageFormat.format("Por favor, ingrese una de las siguientes opciones: \n{0}",
                getValidValues().stream().map(IMentionable::getAsMention).collect(Collectors.joining("\n")));

    }

    @Override
    public @Nullable TextChannel parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<#)?([0-9]+)>?$")){
            Optional<TextChannel> channel =event.getGuild().getTextChannels().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()){
                return channel.get();
            }
        }
        List<TextChannel> channels = event.getGuild().getTextChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 0) return null;
        if (channels.size() == 1) return channels.get(0);
        channels = event.getGuild().getTextChannels().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
