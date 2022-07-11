/*
 *
 *    Copyright 2022 Eric Bastian Ramírez Santis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ericramirezs.commando4j.command.arguments;

import com.ericramirezs.commando4j.command.enums.ArgumentTypes;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class to request an argument of type User to the user.
 *
 * @see net.dv8tion.jda.api.entities.User
 */
public final class UserArgument extends Argument<UserArgument, User> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public UserArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.USER);
    }

    private String validate(@NotNull final Event event, @NotNull final String arg) {
        if (arg.matches("^(?:<@!?)?(\\d+)>?$")) {
            final Optional<User> user = event.getJDA().getUsers().stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (user.isPresent()) {
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
    public String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        return validate((Event) event, arg);
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        return validate((Event) event, arg);
    }

    private @Nullable User parse(@NotNull final Event event, @NotNull final String arg) {
        if (arg.matches("^(?:<@!?)?(\\d+)>?$")) {
            final Optional<User> user = event.getJDA().getUsers().stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (user.isPresent()) {
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

    @Override
    public User parse(final MessageReceivedEvent event, final String arg) {
        return parse((Event) event, arg);
    }

    @Override
    public User parse(final SlashCommandInteractionEvent event, final String arg) {
        return parse((Event) event, arg);
    }

    @Override
    public UserArgument clone() {
        return clone(new UserArgument(getName(), getPrompt()));
    }
}
