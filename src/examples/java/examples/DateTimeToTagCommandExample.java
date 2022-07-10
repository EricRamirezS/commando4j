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

package examples;

import com.ericramirezs.commando4j.command.Slash;
import com.ericramirezs.commando4j.command.arguments.BooleanArgument;
import com.ericramirezs.commando4j.command.arguments.IArgument;
import com.ericramirezs.commando4j.command.arguments.LocalDateArgument;
import com.ericramirezs.commando4j.command.arguments.LocalTimeArgument;
import com.ericramirezs.commando4j.command.arguments.StringArgument;
import com.ericramirezs.commando4j.command.command.Command;
import com.ericramirezs.commando4j.command.exceptions.DuplicatedArgumentNameException;
import com.ericramirezs.commando4j.command.util.DateTimeUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

public class DateTimeToTagCommandExample extends Command implements Slash {

    public DateTimeToTagCommandExample() throws DuplicatedArgumentNameException {
        super("datetime", "examples", "creates a discord timestamp tag from a given date and time",
                new LocalDateArgument("date", "date")
                        .setDefaultValue(() -> LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC)),
                new LocalTimeArgument("time", "time")
                        .setDefaultValue(() -> {
                            Instant instant = Instant.now();
                            return LocalTime.ofInstant(instant.minusNanos(instant.getNano()), ZoneOffset.UTC);
                        }),
                new StringArgument("Offset", "OffsetModifier")
                        .setDefaultValue("+")
                        .addValidValues("+", "-"),
                new LocalTimeArgument("offsetTime", "offset Time")
                        .setDefaultValue(LocalTime.of(0, 0)),
                new BooleanArgument("TagCode", "Get tag as code?")
                        .setDefaultValue(false)
        );
    }

    @Override
    public void run(MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {
        Boolean asCode = (Boolean) args.get("TagCode").getValue();

        OffsetDateTime offDateTime = parse(args);

        if (asCode) {
            sendReply(event, "`" + DateTimeUtils.toDiscordTimeStamp(offDateTime, TimeFormat.DATE_TIME_LONG) + "`");
        } else {
            sendReply(event, DateTimeUtils.toDiscordTimeStamp(offDateTime, TimeFormat.DATE_TIME_LONG));
        }
    }

    public void run(SlashCommandInteractionEvent event, Map<String, IArgument> args) {
        Boolean asCode = (Boolean) args.get("TagCode").getValue();

        OffsetDateTime offDateTime = parse(args);

        if (asCode) {
            sendReply(event, "`" + DateTimeUtils.toDiscordTimeStamp(offDateTime, TimeFormat.DATE_TIME_LONG) + "`");
        } else {
            sendReply(event, DateTimeUtils.toDiscordTimeStamp(offDateTime, TimeFormat.DATE_TIME_LONG));
        }
    }

    private OffsetDateTime parse(Map<String, IArgument> args) {
        LocalDate localDate = (LocalDate) args.get("date").getValue();
        LocalTime localTime = (LocalTime) args.get("time").getValue();
        String plus = (String) args.get("Offset").getValue();
        LocalTime offsetTime = (LocalTime) args.get("offsetTime").getValue();

        int offsetHours = offsetTime.getHour();
        int offsetMinutes = offsetTime.getMinute();

        ZoneOffset zoneOffset = switch (plus) {
            case "+" -> ZoneOffset.ofHoursMinutes(offsetHours, offsetMinutes);
            case "-" -> ZoneOffset.ofHoursMinutes(-offsetHours, -offsetMinutes);
            default -> ZoneOffset.UTC;
        };

        return OffsetDateTime.of(localDate, localTime, zoneOffset);
    }
}
