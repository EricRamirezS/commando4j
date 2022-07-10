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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Class to request an argument of type Role to the user.
 *
 * @see net.dv8tion.jda.api.entities.Role
 */
public final class RoleArgument extends Argument<RoleArgument, Role> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public RoleArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.ROLE);
    }

    private String validate(
            final List<Role> data,
            final String arg,
            final Event event,
            final String oneOfKey,
            final String notFoundKey,
            final String tooManyKey) {
        if (arg.matches("^(?:<@&)?(\\d+)>?$")) {
            final Optional<Role> channel = data.stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent())
                return oneOf(channel.get(), event, IMentionable::getAsMention, oneOfKey);
            else return LocalizedFormat.format(notFoundKey, event);
        }
        List<Role> channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format(notFoundKey, event);
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, oneOfKey);
        channels = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, oneOfKey);
        return LocalizedFormat.format(tooManyKey, event);
    }

    @Override
    public String validate(final MessageReceivedEvent event, final String arg) {
        final List<Role> data = Objects.requireNonNull(event.getGuild()).getRoles();
        return validate(data, arg, event,
                "Argument_Role_OneOf",
                "Argument_Role_NotFound",
                "Argument_Role_TooMany");
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        final List<Role> data = Objects.requireNonNull(event.getGuild()).getRoles();
        return validate(data, arg, event,
                "Argument_Role_OneOf",
                "Argument_Role_NotFound",
                "Argument_Role_TooMany");
    }

    private @Nullable Role parse(final List<Role> data,
                                 final String arg) {
        if (arg.matches("^(?:<@&)?(\\d+)>?$")) {
            final Optional<Role> role = data.stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (role.isPresent()) {
                return role.get();
            }
        }
        List<Role> roles = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (roles.size() == 1) return roles.get(0);
        roles = data.stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (roles.size() == 1) return roles.get(0);
        return null;
    }

    @Override
    public @Nullable Role parse(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        final List<Role> data = Objects.requireNonNull(event.getGuild()).getRoles();
        return parse(data, arg);
    }

    @Override
    public Role parse(final SlashCommandInteractionEvent event, final String arg) {
        final List<Role> data = Objects.requireNonNull(event.getGuild()).getRoles();
        return parse(data, arg);
    }
}
