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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class to request an argument of type Message to the user.
 *
 * @see net.dv8tion.jda.api.entities.Message
 */
public final class MessageArgument extends Argument<MessageArgument, Message> {

    public MessageArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.MESSAGE);
    }

    @Override
    public @Nullable String validate(@NotNull MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("^\\d+$"))
            return LocalizedFormat.format("Argument_Message_Invalid", event);
        if (event.getChannel().getHistory().getMessageById(arg) == null)
            return LocalizedFormat.format("Argument_Message_NotFound", event);
        return null;
    }

    @Override
    public Message parse(@NotNull MessageReceivedEvent event, String arg) {
        return event.getChannel().getHistory().getMessageById(arg);
    }
}
