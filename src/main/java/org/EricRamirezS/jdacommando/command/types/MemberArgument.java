package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MemberArgument extends Argument<Member> {

    public MemberArgument(@NotNull String name, @Nullable String prompt) {
        super(name, prompt, ArgumentTypes.MEMBER);
    }

    @Override
    public String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<@!?)?([0-9]+)>?$")){
            Optional<Member> member =event.getGuild().getMembers().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (member.isPresent()){
                return oneOf(member.get());
            } else {
                return MessageFormat.format("No he podido encontrar al {0} indicado", "miembro");
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
        if (members.size() == 0) return "No he podido encontrar al miembro indicado";
        if (members.size() == 1) return oneOf(members.get(0));
        members = event
                .getGuild()
                .getMembers()
                .stream()
                .filter(c -> c.getUser()
                        .getName().toLowerCase(Locale.ROOT)
                        .equals(arg.toLowerCase(Locale.ROOT)) ||
                        (c.getNickname() != null && c.getNickname().equals(arg.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        if (members.size() == 1) return oneOf(members.get(0));
        return "Se han encontrado multiples miembros, se más específico, por favor.";
    }

    @Override
    public @Nullable Member parse(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches ("^(?:<@!?)?([0-9]+)>?$")){
            Optional<Member> member =event.getGuild().getMembers().stream().filter(c -> c.getAsMention().equals(arg)).findFirst();
            if (member.isPresent()){
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

    private @Nullable String oneOf(Member member) {
        if (isOneOf(member))return null;
        return MessageFormat.format("Por favor, ingrese una de las siguientes opciones: \n{0}",
                getValidValues().stream().map(IMentionable::getAsMention).collect(Collectors.joining("\n")));
    }
}
