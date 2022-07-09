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
import com.ericramirezs.commando4j.command.util.DateTimeUtils;
import com.ericramirezs.commando4j.command.util.LocalizedFormat;
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
public class LocalTimeArgument extends LocalDateTimeArgument<LocalTimeArgument, LocalTime> {

    public LocalTimeArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.DATE);
    }

    @Override
    public String validate(MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("<t:\\d*(:[tTdDfFR])?>")) { //Specific Discord Datetime tags
            try {
                Matcher matcher = Pattern.compile("<t:(\\d+)(:[tTdDfFR])?>").matcher(arg);
                //noinspection ResultOfMethodCallIgnored
                matcher.find();
                String match = matcher.group(1);
                long epochSeconds = Long.parseLong(match);
                Instant date = Instant.ofEpochSecond(epochSeconds);
                return oneOf(LocalTime.ofInstant(date, ZoneOffset.UTC), event,
                        localTime -> localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        "Argument_Time_OneOf");
            } catch (Exception ignored) {
                return null; //if pattern is matched, then there should never be an exception.
            }
        }
        if (isForcedDiscordTag()) {
            return LocalizedFormat.format("Argument_DateTime_TimestamptOnly", event, DateTimeUtils.toDiscordTimeStamp(LocalDateTime.now(ZoneOffset.UTC)));
        }
        try {
            // Parsing with locale's pattern
            LocalTime date = DateTimeUtils.stringToLocalTime(arg);
            return oneOf(date, event,
                    localTime -> localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    "Argument_Time_OneOf");
        } catch (Exception e) {
            return LocalizedFormat.format("Argument_Time_Invalid", event);
        }
    }

    @Override
    public LocalTime parse(MessageReceivedEvent event, @NotNull String arg) {
        if (arg.matches("<t:\\d*(:[tTdDfFR])?>")) {
            Matcher matcher = Pattern.compile("<t:(\\d+)(:[tTdDfFR])?>").matcher(arg);
            //noinspection ResultOfMethodCallIgnored
            matcher.find();
            String match = matcher.group(1);
            long epochSeconds = Long.parseLong(match);
            Instant date = Instant.ofEpochSecond(epochSeconds);
            return LocalTime.ofInstant(date, ZoneOffset.UTC);
        }
        return DateTimeUtils.stringToLocalTime(arg);
    }
}
