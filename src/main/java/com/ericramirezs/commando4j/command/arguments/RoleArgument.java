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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Class to request an argument of type Role to the user.
 *
 * @see net.dv8tion.jda.api.entities.Role
 */
public final class RoleArgument extends Argument<RoleArgument, Role> {

    public RoleArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.ROLE);
    }

    @Override
    public String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<@&)?(\\d+)>?$")) {
            Optional<Role> channel = event.getGuild().getRoles().stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (channel.isPresent())
                return oneOf(channel.get(), event, IMentionable::getAsMention, "Argument_Role_OneOf");
            else return LocalizedFormat.format("Argument_Role_NotFound", event);
        }
        List<Role> channels = event.getGuild().getRoles().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 0) return LocalizedFormat.format("Argument_Role_NotFound", event);
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, "Argument_Role_OneOf");
        channels = event.getGuild().getRoles().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (channels.size() == 1)
            return oneOf(channels.get(0), event, IMentionable::getAsMention, "Argument_Role_OneOf");
        return LocalizedFormat.format("Argument_Role_TooMany", event);
    }

    @Override
    public @Nullable Role parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<@&)?(\\d+)>?$")) {
            Optional<Role> role = event.getGuild().getRoles().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (role.isPresent()) {
                return role.get();
            }
        }
        List<Role> roles = event.getGuild().getRoles().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).contains(arg.toLowerCase(Locale.ROOT))).toList();
        if (roles.size() == 1) return roles.get(0);
        roles = event.getGuild().getRoles().stream()
                .filter(c -> c.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))).toList();
        if (roles.size() == 1) return roles.get(0);
        return null;
    }
}
