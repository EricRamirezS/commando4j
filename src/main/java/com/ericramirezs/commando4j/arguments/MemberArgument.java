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

package com.ericramirezs.commando4j.arguments;

import com.ericramirezs.commando4j.enums.ArgumentTypes;
import com.ericramirezs.commando4j.util.LocalizedFormat;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class to request an argument of type Member to the user.
 *
 * @see net.dv8tion.jda.api.entities.Member
 */
public final class MemberArgument extends Argument<MemberArgument, Member> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public MemberArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.MEMBER);
    }

    private String validate(
            final List<Member> data,
            final String arg,
            final Event event,
            final String oneOfKey,
            final String notFoundKey,
            final String tooManyKey) {
        if (arg.matches("^(?:<@!?)?(\\d+)>?$")) {
            final Optional<Member> member = data.stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (member.isPresent())
                return oneOf(member.get(), event, IMentionable::getAsMention, oneOfKey);
            else return LocalizedFormat.format(notFoundKey, event);
        }
        List<Member> members = data.stream().filter(c -> c.getUser()
                        .getName().toLowerCase(Locale.ROOT)
                        .contains(arg.toLowerCase(Locale.ROOT)) ||
                        (c.getNickname() != null && c.getNickname().contains(arg.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        if (members.size() == 0) return LocalizedFormat.format(notFoundKey, event);
        if (members.size() == 1)
            return oneOf(members.get(0), event, IMentionable::getAsMention, oneOfKey);
        members = data.stream()
                .filter(c -> c.getUser()
                        .getName().toLowerCase(Locale.ROOT)
                        .equals(arg.toLowerCase(Locale.ROOT)) ||
                        (c.getNickname() != null && c.getNickname().equals(arg.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        if (members.size() == 1)
            return oneOf(members.get(0), event, IMentionable::getAsMention, oneOfKey);
        return LocalizedFormat.format(tooManyKey, event);
    }

    @Override
    public String validate(@NotNull final MessageReceivedEvent event, @NotNull final String arg) {
        final List<Member> data = event.getGuild().getMembers();
        return validate(data, arg, event,
                "Argument_Member_OneOf",
                "Argument_Member_NotFound",
                "Argument_Member_TooMany");
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        final List<Member> data = Objects.requireNonNull(event.getGuild()).getMembers();
        return validate(data, arg, event,
                "Argument_Member_OneOf",
                "Argument_Member_NotFound",
                "Argument_Member_TooMany");
    }

    private @Nullable Member parse(final List<Member> data, @NotNull final String arg) {
        if (arg.matches("^(?:<@!?)?(\\d+)>?$")) {
            final Optional<Member> member = data.stream()
                    .filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (member.isPresent()) {
                return member.get();
            }
        }
        List<Member> members = data.stream()
                .filter(c -> c.getUser()
                        .getName().toLowerCase(Locale.ROOT)
                        .contains(arg.toLowerCase(Locale.ROOT)) ||
                        (c.getNickname() != null && c.getNickname().contains(arg.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        if (members.size() == 1) return members.get(0);
        members = data.stream()
                .filter(c -> c.getUser()
                        .getName().toLowerCase(Locale.ROOT)
                        .equals(arg.toLowerCase(Locale.ROOT)) ||
                        (c.getNickname() != null && c.getNickname().equals(arg.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        if (members.size() == 1) return members.get(0);
        return null;
    }

    @Override
    public Member parse(final MessageReceivedEvent event, final String arg) {
        final List<Member> data = event.getGuild().getMembers();
        return parse(data, arg);
    }

    @Override
    public Member parse(final SlashCommandInteractionEvent event, final String arg) {
        final List<Member> data = Objects.requireNonNull(event.getGuild()).getMembers();
        return parse(data, arg);
    }

    @Override
    public MemberArgument clone() {
        return clone(new MemberArgument(getName(), getPrompt()));
    }
}
