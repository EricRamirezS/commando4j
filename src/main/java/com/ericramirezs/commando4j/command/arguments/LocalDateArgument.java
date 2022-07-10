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

import com.ericramirezs.commando4j.command.CommandEngine;
import com.ericramirezs.commando4j.command.enums.ArgumentTypes;
import com.ericramirezs.commando4j.command.util.DateTimeUtils;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to request an argument of type LocalDate to the user.
 *
 * @see java.time.LocalDate
 */
public final class LocalDateArgument extends LocalDateTimeArgument<LocalDateArgument, LocalDate> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public LocalDateArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.DATE);
    }

    private String validate(final Event event, @NotNull final String arg) {
        final Locale locale = CommandEngine.getInstance().getLanguage(event);
        final String pattern = CommandEngine.getInstance().getString("Argument_Date_Pattern", locale);

        if (arg.matches("<t:\\d*(:[tTdDfFR])?>")) {
            try {
                final Matcher matcher = Pattern.compile("<t:(\\d+)(:[tTdDfFR])?>").matcher(arg);
                //noinspection ResultOfMethodCallIgnored
                matcher.find();
                final String match = matcher.group(1);
                final long epochSeconds = Long.parseLong(match);
                final Instant date = Instant.ofEpochSecond(epochSeconds);
                return oneOf(LocalDate.ofInstant(date, ZoneOffset.UTC), event,
                        localDate -> localDate.format(DateTimeFormatter.ofPattern(DateTimeUtils.localeToDateFormat(locale))),
                        "Argument_Date_OneOf");
            } catch (final Exception ignored) {
                return null; //if pattern is matched, then there should never be an exception.
            }
        }
        if (isForcedDiscordTag()) {
            return LocalizedFormat.format("Argument_DateTime_TimestampOnly", event, DateTimeUtils.toDiscordTimeStamp(LocalDateTime.now(ZoneOffset.UTC)));
        }
        try {
            // Parsing with locale's pattern
            final LocalDate date = DateTimeUtils.stringToLocalDate(arg, locale);
            return oneOf(date, event,
                    localDate -> localDate.format(DateTimeFormatter.ofPattern(DateTimeUtils.localeToDateFormat(locale))),
                    "Argument_Date_OneOf");
        } catch (final ParseException e) {
            try {
                // Parsing with pattern defined in bundle
                final LocalDate date = DateTimeUtils.stringToLocalDate(arg, pattern);
                return oneOf(date, event,
                        localDate -> localDate.format(DateTimeFormatter.ofPattern(DateTimeUtils.localeToDateFormat(locale))),
                        "Argument_Date_OneOf");
            } catch (final ParseException ex) {
                final String localePattern = DateTimeUtils.localeToDateFormat(locale);
                if (localePattern.equals(pattern))
                    return LocalizedFormat.format("Argument_Date_Invalid", event, DateTimeUtils.localeToDateFormat(locale));
                return LocalizedFormat.format("Argument_Date_Invalid", event,
                        LocalizedFormat.format("NormalText_Or", event, localePattern + " ", " " + pattern)
                );
            }
        }
    }

    @Override
    public String validate(final MessageReceivedEvent event, final String arg) {
        return validate((Event) event, arg);
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        return validate((Event) event, arg);
    }

    private LocalDate parse(final Event event, @NotNull final String arg) {
        final Locale locale = CommandEngine.getInstance().getLanguage(event);
        final String pattern = CommandEngine.getInstance().getString("Argument_Date_Pattern", locale);

        if (arg.matches("<t:\\d*(:[tTdDfFR])?>")) {
            final Matcher matcher = Pattern.compile("<t:(\\d+)(:[tTdDfFR])?>").matcher(arg);
            //noinspection ResultOfMethodCallIgnored
            matcher.find();
            final String match = matcher.group(1);
            final long epochSeconds = Long.parseLong(match);
            final Instant date = Instant.ofEpochSecond(epochSeconds);
            return LocalDate.ofInstant(date, ZoneOffset.UTC);
        }
        try {
            // Parsing with locale's pattern
            return DateTimeUtils.stringToLocalDate(arg, locale);
        } catch (final ParseException e) {
            // Parsing with pattern defined in bundle
            try {
                return DateTimeUtils.stringToLocalDate(arg, pattern);
            } catch (final ParseException ignored) {
                // if validation was a success, this code is unreachable
            }
        }
        return null;
    }

    @Override
    public LocalDate parse(final MessageReceivedEvent event, final String arg) {
        return parse((Event) event, arg);
    }

    @Override
    public LocalDate parse(final SlashCommandInteractionEvent event, final String arg) {
        return parse((Event) event, arg);
    }
}
