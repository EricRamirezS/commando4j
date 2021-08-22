package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RoleArgument extends Argument<Role> {

    public RoleArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.ROLE);
    }

    @Override
    public String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<@&)?([0-9]+)>?$")){
            Optional<Role> channel =event.getGuild().getRoles().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent()){
                return oneOf(channel.get());
            } else {
                return MessageFormat.format("No he podido encontrar el {0} indicado", "rol");
            }
        }
        List<Role> channels = event.getGuild().getRoles().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 0) return "No he podido encontrar el rol indicado";
        if (channels.size() == 1) return oneOf(channels.get(0));
        channels = event.getGuild().getRoles().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (channels.size() == 1) return oneOf(channels.get(0));
        return "Se han encontrado multiples roles, se más específico, por favor.";
    }

    private @Nullable String oneOf(Role role) {
        if (isOneOf(role))return null;
        return MessageFormat.format("Por favor, ingrese una de las siguientes opciones: \n{0}",
                getValidValues().stream().map(IMentionable::getAsMention).collect(Collectors.joining("\n")));
    }

    @Override
    public @Nullable Role parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<@&)?([0-9]+)>?$")){
            Optional<Role> role =event.getGuild().getRoles().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (role.isPresent()){
                return role.get();
            }
        }
        List<Role> roles = event.getGuild().getRoles().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (roles.size() == 1) return roles.get(0);
        roles = event.getGuild().getRoles().stream().filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (roles.size() == 1) return roles.get(0);
        return null;
    }
}
