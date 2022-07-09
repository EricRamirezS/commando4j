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
