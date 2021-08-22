package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class UserArgument extends Argument<User> {

    public UserArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.USER);
    }

    @Override
    public String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<@!?)?([0-9]+)>?$")){
            Optional<User> user =event.getJDA().getUsers().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (user.isPresent()){
                return oneOf(user.get());
            } else {
                return MessageFormat.format("No he podido encontrar al {0} indicado", "usuario");
            }
        }
        List<User> users = event
                .getJDA()
                .getUsers()
                .stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (users.size() == 0) return "No he podido encontrar al usuario indicado";
        if (users.size() == 1) return oneOf(users.get(0));
        users = event
                .getJDA()
                .getUsers()
                .stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (users.size() == 1) return oneOf(users.get(0));
        return "Se han encontrado multiples usuarios, se más específico, por favor.";
    }

    private @Nullable String oneOf(User user) {
        if (isOneOf(user))return null;
        return MessageFormat.format("Por favor, ingrese una de las siguientes opciones: \n{0}",
                getValidValues().stream().map(IMentionable::getAsMention).collect(Collectors.joining("\n")));
    }

    @Override
    public @Nullable User parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<@!?)?([0-9]+)>?$")){
            Optional<User> user =event.getJDA().getUsers().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (user.isPresent()){
                return user.get();
            }
        }
        List<User> users = event
                .getJDA()
                .getUsers()
                .stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (users.size() == 1) return users.get(0);
        users = event
                .getJDA()
                .getUsers()
                .stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (users.size() == 1) return users.get(0);
        return null;
    }
}
