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

package com.ericramirezs.commando4j.command.command.util;

import com.ericramirezs.commando4j.command.CommandEngine;
import com.ericramirezs.commando4j.command.Slash;
import com.ericramirezs.commando4j.command.arguments.CommandArgument;
import com.ericramirezs.commando4j.command.arguments.IArgument;
import com.ericramirezs.commando4j.command.arguments.StringArgument;
import com.ericramirezs.commando4j.command.arguments.UnionArgument;
import com.ericramirezs.commando4j.command.command.Command;
import com.ericramirezs.commando4j.command.command.ICommand;
import com.ericramirezs.commando4j.command.exceptions.DuplicatedArgumentNameException;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HelpCommand extends Command implements Slash {
    private static SampleCommand sampleCommand;

    static {
        try {
            sampleCommand = new SampleCommand();
        } catch (final DuplicatedArgumentNameException ignored) {
            //Command has no arguments
        }
    }

    private boolean simpleList;

    public HelpCommand() throws DuplicatedArgumentNameException {
        super("help",
                "util",
                "Command_Help_Description",
                new UnionArgument("commandName", "Command_Help_Argument_CommandNamePrompt")
                        .addArgumentType(new StringArgument("", "")
                                .addValidValues("all"))
                        .addArgumentType(new CommandArgument("", ""))
        );
        final UnionArgument arg = (UnionArgument) getArguments().get(0);
        arg.setPromptParser(x -> localizePrompt(arg, x));

        addExamples("help",
                "help all",
                "help prefix");
    }

    private @NotNull String localizePrompt(final UnionArgument arg, final Event event) {
        if (event == null) return LocalizedFormat.format(arg.getPromptRaw());
        return LocalizedFormat.format(arg.getPromptRaw(), event);
    }

    @Override
    public String getDescription() {
        return LocalizedFormat.format(super.getDescription());
    }

    @Override
    public String getDescription(final Event event) {
        return LocalizedFormat.format(super.getDescription(), event);
    }

    public HelpCommand useSimpleCommandList() {
        simpleList = true;
        return this;
    }

    @Override
    public void run(@NotNull final SlashCommandInteractionEvent event,
                    @UnmodifiableView @NotNull final Map<String, IArgument> args) {
        Guild guild = null;
        if (event.isFromGuild()) guild = event.getGuild();
        run(event, args, guild);
    }

    @Override
    public void run(@NotNull final MessageReceivedEvent event,
                    @NotNull final Map<String, IArgument> args) {
        Guild guild = null;
        if (event.isFromGuild()) guild = event.getGuild();
        run(event, args, guild);
    }

    private void run(@NotNull final Event event,
                     @NotNull final Map<String, IArgument> args,
                     @Nullable final Guild guild) {
        final UnionArgument argument = (UnionArgument) args.get("commandName");
        ICommand command = null;
        StringBuilder baseMessage = new StringBuilder();
        if (argument.getValue() instanceof CommandArgument)
            command = ((CommandArgument) argument.getValue()).getValue();
        final boolean showAll = argument.getValue() instanceof StringArgument;

        if (command != null) {
            baseMessage = new StringBuilder(LocalizedFormat.format("Help_CommandSingle", event,
                    command.getName(event),
                    command.getDescription(event),
                    usageLocationLimit(command, event),
                    command.isNsfw() ? LocalizedFormat.format("Help_NSFW", event) : "",
                    command.anyUsage(event)));
            if (command.getAliases().size() > 0)
                baseMessage.append("\n").append(LocalizedFormat.format("Help_Aliases",
                        event,
                        String.join(", ", command.getAliases())));

            baseMessage.append("\n").append(LocalizedFormat.format("Help_Group", event, command.getGroup(event)));

            if (command.getDetails(event) != null)
                baseMessage.append("\n").append(LocalizedFormat.format("Help_Details", event, command.getDetails(event)));
            if (command.getExamples().size() > 0)
                baseMessage.append("\n").append(LocalizedFormat.format("Help_Examples",
                        event,
                        String.join("\n", command.getExamples())));
        } else {
            List<ICommand> commands = CommandEngine.getInstance().getCommands();
            if (!showAll) {
                commands = commands.stream().filter(c -> c.checkPermissions(event) == null)
                        .collect(Collectors.toList());
            }
            final List<String> groups = commands.stream().map(ICommand::getGroup).distinct().sorted()
                    .collect(Collectors.toList());

            baseMessage.append(LocalizedFormat.format("Help_CommandList", event,
                    guild != null ? guild.getName()
                            : LocalizedFormat.format("Help_AnyServer", event),
                    sampleCommand.anyUsage(event),
                    Objects.requireNonNull(CommandEngine.getInstance().getCommand("prefix"))
                            .usage("~", event)
            ));
            if (guild == null)
                baseMessage.append("\n")
                        .append(LocalizedFormat.format("Help_DirectMessage", event, sampleCommand.getName(event)));

            baseMessage.append("\n\n")
                    .append(LocalizedFormat.format("Help_DetailedExample", event,
                            usage("<" + sampleCommand.getName(event) + ">", event)));

            if (!showAll)
                baseMessage.append("\n")
                        .append(LocalizedFormat.format("Help_UseAll", event, usage("all", event)));

            baseMessage.append("\n\n")
                    .append("__**")
                    .append(showAll ? LocalizedFormat.format("Help_AllCommands", event) :
                            LocalizedFormat.format("Help_Available", event,
                                    guild != null ? guild.getName()
                                            : LocalizedFormat.format("Help_ThisDm", event)))
                    .append("**__")
                    .append("\n");
            for (final String groupName : groups) {
                final List<ICommand> groupCommands = commands.stream().filter(c -> Objects.equals(c.getGroup(), groupName))
                        .collect(Collectors.toList());
                if (simpleList) {
                    baseMessage.append(String.format("\n`%s`:\n`%s`\n",
                            groupName,
                            groupCommands.stream().map(c -> getName(event))
                                    .collect(Collectors.joining("`, `"))));
                } else {
                    baseMessage.append(String.format("\n`%s`:\n%s\n",
                            groupName,
                            groupCommands.stream()
                                    .map(c -> String.format("**%s**: %s %s", c.getName(event)
                                                    , c.getDescription(event)
                                                    , c.isNsfw() ? '*' + LocalizedFormat.format("Help_NSFW", event) + '*' : "")
                                            .trim()
                                            .replace("\n", "\n\t"))
                                    .collect(Collectors.joining("\n"))));
                }
            }
        }
        final String[] chunks = baseMessage.toString().trim().split("(?<=\\G.{2000})");
        for (final String message : chunks) {
            sendReply(event, message);
        }
    }

    private @NotNull String usageLocationLimit(@NotNull final ICommand c, final Event event) {
        if (c.isThreadOnly()) {
            return "(" + LocalizedFormat.format("Help_ThreadOnly", event) + ")";
        }
        if (c.isGuildOnly()) {
            return "(" + LocalizedFormat.format("Help_GuildOnly", event) + ")";
        }
        if (c.isPrivateUseOnly()) {
            return "(" + LocalizedFormat.format("Help_UserOnly", event) + ")";
        }
        return "";
    }
}

final class SampleCommand extends Command {
    SampleCommand() throws DuplicatedArgumentNameException {
        super("command", "utils", "");
    }

    @Override
    public @NotNull String getName(final Event event) {
        return CommandEngine.getInstance().getString("NormalText_Command", CommandEngine.getInstance().getLanguage(event));
    }

    @Override
    public void run(@NotNull final MessageReceivedEvent event, @NotNull final Map<String, IArgument> args) {
        //do nothing
    }
}