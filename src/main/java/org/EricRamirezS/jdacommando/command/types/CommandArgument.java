package org.EricRamirezS.jdacommando.command.types;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandArgument extends BaseArgument<Command> {
    protected CommandArgument(@NotNull String name, @Nullable String prompt, @NotNull ArgumentTypes type) {
        super(name, prompt, type);
    }

    @Override
    public String validate(@NotNull GuildMessageReceivedEvent event, @NotNull String arg) {
        return null;
    }

    @Override
    public Command parse(@NotNull GuildMessageReceivedEvent event, String arg) {
        return null;
    }
}
