package org.EricRamirezS.jdacommando.command.command.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.Slash;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.arguments.StringArgument;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedArgumentNameException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class PrefixCommand extends Command implements Slash {
    public PrefixCommand() throws DuplicatedArgumentNameException {
        super("prefix", "util", "Command_Prefix_Description",
                new StringArgument("prefix", "What would you like to set the bot's prefix to?")
                        .setMax(15));
        addExamples("prefix",
                "prefix ~",
                "prefix omg!",
                "prefix default",
                "prefix none");
        setGuildOnly();
        addMemberPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public String getDescription() {
        return LocalizedFormat.format(super.getDescription());
    }

    @Override
    public String getDescription(Event event) {
        return LocalizedFormat.format(super.getDescription(), event);
    }

    @Override
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        String newPrefix = (String) args.get("prefix").getValue();
        if (newPrefix == null || newPrefix.length() == 0 || newPrefix.trim().length() == 0) {
            sendReply(event, CommandEngine.getInstance().getPrefix(event));
        } else {
            try {
                CommandEngine.getInstance().getRepository().setPrefix(event.getGuild().getId(), newPrefix.trim());
            } catch (Exception ex) {
                CommandEngine.getInstance().logError(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            }
        }
    }

    @Override
    public void run(@NotNull SlashCommandInteractionEvent event, @UnmodifiableView @NotNull Map<String, IArgument> args) {
        String newPrefix = (String) args.get("prefix").getValue();
        if (newPrefix == null || newPrefix.length() == 0 || newPrefix.trim().length() == 0) {
            Slash.sendReply(event, CommandEngine.getInstance().getPrefix(event));
        } else {
            try {
                CommandEngine.getInstance().getRepository()
                        .setPrefix(Objects.requireNonNull(event.getGuild()).getId(), newPrefix.trim());
            } catch (Exception ex) {
                CommandEngine.getInstance().logError(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            }
        }
    }
}
