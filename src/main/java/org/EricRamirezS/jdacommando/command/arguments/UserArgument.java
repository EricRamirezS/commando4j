package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.User;
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

public final class UserArgument extends Argument<UserArgument, User> {

    public UserArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.USER);
    }

    @Override
    public String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<@!?)?(\\d+)>?$")){
            Optional<User> user =event.getJDA().getUsers().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (user.isPresent()){
                return oneOf(user.get(), event, IMentionable::getAsMention, "Argument_User_OneOf");
            } else {
                return LocalizedFormat.format("Argument_User_NotFound", event);
            }
        }
        List<User> users = event
                .getJDA()
                .getUsers()
                .stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (users.size() == 0) return LocalizedFormat.format("Argument_User_NotFound", event);
        if (users.size() == 1) return oneOf(users.get(0), event, IMentionable::getAsMention, "Argument_User_OneOf");
        users = event
                .getJDA()
                .getUsers()
                .stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        if (users.size() == 1) return oneOf(users.get(0), event, IMentionable::getAsMention, "Argument_User_OneOf");
        return LocalizedFormat.format("Argument_User_TooMany", event);
    }

    @Override
    public @Nullable User parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<@!?)?(\\d+)>?$")){
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
