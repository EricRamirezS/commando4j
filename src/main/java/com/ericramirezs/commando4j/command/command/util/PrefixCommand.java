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
import com.ericramirezs.commando4j.command.arguments.IArgument;
import com.ericramirezs.commando4j.command.arguments.StringArgument;
import com.ericramirezs.commando4j.command.command.Command;
import com.ericramirezs.commando4j.command.enums.Emoji;
import com.ericramirezs.commando4j.command.exceptions.DuplicatedArgumentNameException;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class PrefixCommand extends Command implements Slash {
    public PrefixCommand() throws DuplicatedArgumentNameException {
        super("prefix", "util", "Command_Prefix_Description",
                new StringArgument("prefix", "Command_Prefix_ArgumentDescription")
                        .setMax(15));
        addExamples("prefix",
                "prefix ~",
                "prefix omg!",
                "prefix default",
                "prefix none");
        final StringArgument arg = (StringArgument) getArguments().get(0);
        arg.setPromptParser(x -> localizePrompt(arg, x));

        setGuildOnly();
        addMemberPermissions(Permission.ADMINISTRATOR);
    }

    private String localizePrompt(final StringArgument arg, final Event event) {
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

    @Override
    public void run(@NotNull final MessageReceivedEvent event, @NotNull final Map<String, IArgument> args) {
        final String newPrefix = (String) args.get("prefix").getValue();
        if (newPrefix == null || newPrefix.length() == 0 || newPrefix.trim().length() == 0) {
            sendReply(event, CommandEngine.getInstance().getPrefix(event));
        } else {
            try {
                CommandEngine.getInstance().getRepository().setPrefix(event.getGuild().getId(), newPrefix.trim());
                sendReply(event, Emoji.THUMBS_UP);
            } catch (final Exception ex) {
                CommandEngine.getInstance().logError(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
                sendReply(event, Emoji.THUMBS_DOWN);
            }
        }
    }

    @Override
    public void run(@NotNull final SlashCommandInteractionEvent event, @UnmodifiableView @NotNull final Map<String, IArgument> args) {
        final String newPrefix = (String) args.get("prefix").getValue();
        if (newPrefix == null || newPrefix.length() == 0 || newPrefix.trim().length() == 0) {
            sendReply(event, CommandEngine.getInstance().getPrefix(event));
        } else {
            try {
                CommandEngine.getInstance().getRepository()
                        .setPrefix(Objects.requireNonNull(event.getGuild()).getId(), newPrefix.trim());
                sendReply(event, Emoji.THUMBS_UP);
            } catch (final Exception ex) {
                CommandEngine.getInstance().logError(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
                sendReply(event, Emoji.THUMBS_DOWN);
            }
        }
    }
}
