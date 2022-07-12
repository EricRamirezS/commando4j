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

package com.ericramirezs.commando4j.arguments;

import com.ericramirezs.commando4j.enums.ArgumentTypes;
import com.ericramirezs.commando4j.util.DateTimeUtils;
import com.ericramirezs.commando4j.util.LocalizedFormat;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to request an argument of type LocalTime to the user.
 *
 * @see java.time.LocalTime
 */
public final class LocalTimeArgument extends LocalDateTimeArgument<LocalTimeArgument, LocalTime> {

    /**
     * Creates an instance of this Argument implementation
     *
     * @param name   Readable name to display to the final
     * @param prompt Hint to indicate the user the expected value to be passed to this argument.
     */
    public LocalTimeArgument(@NotNull final String name, @NotNull final String prompt) {
        super(name, prompt, ArgumentTypes.TIME);
    }

    private String validate(final Event event, @NotNull final String arg) {
        if (arg.matches("<t:\\d*(:[tTdDfFR])?>")) { //Specific Discord Datetime tags
            try {
                final Matcher matcher = Pattern.compile("<t:(\\d+)(:[tTdDfFR])?>").matcher(arg);
                //noinspection ResultOfMethodCallIgnored
                matcher.find();
                final String match = matcher.group(1);
                final long epochSeconds = Long.parseLong(match);
                final Instant date = Instant.ofEpochSecond(epochSeconds);
                return oneOf(date.atZone(ZoneOffset.UTC).toLocalTime(), event,
                        localTime -> localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        "Argument_Time_OneOf");
            } catch (final Exception ignored) {
                return null; //if pattern is matched, then there should never be an exception.
            }
        }
        if (isForcedDiscordTag()) {
            return LocalizedFormat.format("Argument_DateTime_TimestampOnly", event, DateTimeUtils.toDiscordTimeStamp(LocalDateTime.now(ZoneOffset.UTC)));
        }
        try {
            // Parsing with locale's pattern
            final LocalTime date = DateTimeUtils.stringToLocalTime(arg);
            return oneOf(date, event,
                    localTime -> localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    "Argument_Time_OneOf");
        } catch (final Exception e) {
            return LocalizedFormat.format("Argument_Time_Invalid", event);
        }
    }

    private LocalTime parse(@NotNull final String arg) {
        if (arg.matches("<t:\\d*(:[tTdDfFR])?>")) {
            final Matcher matcher = Pattern.compile("<t:(\\d+)(:[tTdDfFR])?>").matcher(arg);
            //noinspection ResultOfMethodCallIgnored
            matcher.find();
            final String match = matcher.group(1);
            final long epochSeconds = Long.parseLong(match);
            final Instant date = Instant.ofEpochSecond(epochSeconds);
            return date.atZone(ZoneOffset.UTC).toLocalTime();
        }
        return DateTimeUtils.stringToLocalTime(arg);
    }

    @Override
    public String validate(final MessageReceivedEvent event, final String arg) {
        return validate((Event) event, arg);
    }

    @Override
    public String validate(final SlashCommandInteractionEvent event, final String arg) {
        return validate((Event) event, arg);
    }

    @Override
    public LocalTime parse(final MessageReceivedEvent event, final String arg) {
        return parse(arg);
    }

    @Override
    public LocalTime parse(final SlashCommandInteractionEvent event, final String arg) {
        return parse(arg);
    }

    @Override
    public LocalTimeArgument clone() {
        return clone(new LocalTimeArgument(getName(), getPrompt())).setForcedTag(isForcedDiscordTag());
    }
}
