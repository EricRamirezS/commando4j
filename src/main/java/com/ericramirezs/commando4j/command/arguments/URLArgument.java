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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

/**
 * Class to request an argument of type URL to the user.
 *
 * @see java.net.URL
 */
public class URLArgument extends Argument<URLArgument, URL> {
    protected URLArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.URL);
    }

    @Override
    public String validate(MessageReceivedEvent event, String s) {
        try {
            new URL(s);
            return null;
        } catch (Exception e) {
            return LocalizedFormat.format("Argument_URL_Invalid", event);
        }
    }

    @Override
    public URL parse(MessageReceivedEvent messageReceivedEvent, String s) throws Exception {
        return new URL(s);
    }
}
