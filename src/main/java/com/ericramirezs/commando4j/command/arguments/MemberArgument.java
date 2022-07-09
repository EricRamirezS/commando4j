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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class to request an argument of type Member to the user.
 *
 * @see net.dv8tion.jda.api.entities.Member
 */
public final class MemberArgument extends Argument<MemberArgument, Member> {

    public MemberArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.MEMBER);
    }

    @Override
    public String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<@!?)?(\\d+)>?$")) {
            Optional<Member> member = event.getGuild().getMembers().stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (member.isPresent())
                return oneOf(member.get(), event, IMentionable::getAsMention, "Argument_Member_OneOf");
            else return LocalizedFormat.format("Argument_Member_NotFound", event);
        }
        List<Member> members = event
                .getGuild()
                .getMembers()
                .stream()
                .filter(c -> c.getUser()
                        .getName().toLowerCase(Locale.ROOT)
                        .contains(arg.toLowerCase(Locale.ROOT)) ||
                        (c.getNickname() != null && c.getNickname().contains(arg.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        if (members.size() == 0) return LocalizedFormat.format("Argument_Member_NotFound", event);
        if (members.size() == 1)
            return oneOf(members.get(0), event, IMentionable::getAsMention, "Argument_Member_OneOf");
        members = event.getGuild().getMembers().stream()
                .filter(c -> c.getUser()
                        .getName().toLowerCase(Locale.ROOT)
                        .equals(arg.toLowerCase(Locale.ROOT)) ||
                        (c.getNickname() != null && c.getNickname().equals(arg.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        if (members.size() == 1)
            return oneOf(members.get(0), event, IMentionable::getAsMention, "Argument_Member_OneOf");
        return LocalizedFormat.format("Argument_Member_TooMany", event);
    }

    @Override
    public @Nullable Member parse(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^(?:<@!?)?(\\d+)>?$")) {
            Optional<Member> member = event.getGuild().getMembers().stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (member.isPresent()) {
                return member.get();
            }
        }
        List<Member> members = event
                .getGuild()
                .getMembers()
                .stream()
                .filter(c -> c.getUser()
                        .getName().toLowerCase(Locale.ROOT)
                        .contains(arg.toLowerCase(Locale.ROOT)) ||
                        (c.getNickname() != null && c.getNickname().contains(arg.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        if (members.size() == 1) return members.get(0);
        members = event
                .getGuild()
                .getMembers()
                .stream()
                .filter(c -> c.getUser()
                        .getName().toLowerCase(Locale.ROOT)
                        .equals(arg.toLowerCase(Locale.ROOT)) ||
                        (c.getNickname() != null && c.getNickname().equals(arg.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        if (members.size() == 1) return members.get(0);
        return null;
    }
}
