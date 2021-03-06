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

package com.ericramirezs.commando4j.command.util;

import com.ericramirezs.commando4j.Slash;
import com.ericramirezs.commando4j.arguments.IArgument;
import com.ericramirezs.commando4j.command.Command;
import com.ericramirezs.commando4j.util.LocalizedFormat;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public class PingCommandExample extends Command implements Slash {

    public PingCommandExample() throws Exception {
        super(
                "ping",
                "util",
                "Command_Ping_Description");
        addClientPermissions(Permission.MESSAGE_SEND);
    }

    @Override
    public String getDescription() {
        return LocalizedFormat.format(super.getDescription());
    }

    @Override
    public String getDescription(final Event event) {
        return LocalizedFormat.format(super.getDescription(), event);
    }

    private static void ping(@NotNull final MessageChannel channel) {
        final long time = System.currentTimeMillis();
        channel.sendMessage("Pong!") /* => RestAction<Message> */
                .queue(response /* => Message */ -> response
                        .editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue());
    }

    @Override
    public void run(@NotNull final MessageReceivedEvent event, @NotNull final Map<String, IArgument> args) {
        ping(event.getChannel());
    }

    @Override
    public void run(@NotNull final SlashCommandInteractionEvent event, @UnmodifiableView @NotNull final Map<String, IArgument> args) {
        ping(event.getChannel());
    }
}
