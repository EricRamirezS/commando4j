package org.EricRamirezS.jdacommando.command.customizations;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Locale;

public interface LocalizedFormat {

    static @NotNull String format(String key, Object... arguments) {
        return MessageFormat.format(CommandEngine.getInstance().getString(key), arguments);
    }

    static @NotNull String format(String key, Event event, Object... arguments) {
        return format(key, CommandEngine.getInstance().getLanguage(event), arguments);
    }

    static @NotNull String format(String key, MessageReceivedEvent event, Object... arguments) {
        return format(key, CommandEngine.getInstance().getLanguage(event), arguments);
    }

    static @NotNull String format(String key, SlashCommandInteractionEvent event, Object... arguments) {
        return format(key, CommandEngine.getInstance().getLanguage(event), arguments);
    }

    static @NotNull String format(String key, Event event) {
        return format(key, CommandEngine.getInstance().getLanguage(event));
    }

    static @NotNull String format(String key, MessageReceivedEvent event) {
        return format(key, CommandEngine.getInstance().getLanguage(event));
    }

    static @NotNull String format(String key, SlashCommandInteractionEvent event) {
        return format(key, CommandEngine.getInstance().getLanguage(event));
    }

    static @NotNull String format(String key, Locale locale, Object... arguments) {
        return MessageFormat.format(CommandEngine.getInstance().getString(key, locale), arguments);
    }
}
