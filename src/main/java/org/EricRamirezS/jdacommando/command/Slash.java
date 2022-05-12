package org.EricRamirezS.jdacommando.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.EricRamirezS.jdacommando.command.types.Argument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public interface Slash {

    void runSlash(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, Argument> args);

}
