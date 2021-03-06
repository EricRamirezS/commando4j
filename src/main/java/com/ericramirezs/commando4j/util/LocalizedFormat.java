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

package com.ericramirezs.commando4j.util;

import com.ericramirezs.commando4j.CommandEngine;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Locale;

public class LocalizedFormat {

    private LocalizedFormat() {
    }

    public static @NotNull String format(final String key, final Object... arguments) {
        return MessageFormat.format(CommandEngine.getInstance().getString(key), arguments);
    }

    public static @NotNull String format(final String key, final Event event, final Object... arguments) {
        return format(key, CommandEngine.getInstance().getLanguage(event), arguments);
    }

    public static @NotNull String format(final String key, final MessageReceivedEvent event, final Object... arguments) {
        return format(key, CommandEngine.getInstance().getLanguage(event), arguments);
    }

    public static @NotNull String format(final String key, final SlashCommandInteractionEvent event, final Object... arguments) {
        return format(key, CommandEngine.getInstance().getLanguage(event), arguments);
    }

    public static @NotNull String format(final String key, final Event event) {
        return format(key, CommandEngine.getInstance().getLanguage(event));
    }

    public static @NotNull String format(final String key, final MessageReceivedEvent event) {
        return format(key, CommandEngine.getInstance().getLanguage(event));
    }

    public static @NotNull String format(final String key, final SlashCommandInteractionEvent event) {
        return format(key, CommandEngine.getInstance().getLanguage(event));
    }

    public static @NotNull String format(final String key, final Locale locale, final Object... arguments) {
        return MessageFormat.format(CommandEngine.getInstance().getString(key, locale), arguments);
    }
}
