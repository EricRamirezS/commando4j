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

import com.ericramirezs.commando4j.Slash;
import com.ericramirezs.commando4j.arguments.BooleanArgument;
import com.ericramirezs.commando4j.arguments.IArgument;
import com.ericramirezs.commando4j.arguments.LocalDateArgument;
import com.ericramirezs.commando4j.arguments.LocalTimeArgument;
import com.ericramirezs.commando4j.arguments.StringArgument;
import com.ericramirezs.commando4j.command.Command;
import com.ericramirezs.commando4j.exceptions.DuplicatedArgumentNameException;
import com.ericramirezs.commando4j.util.DateTimeUtils;
import net.dv8tion.jda.api.events.Event;
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
                        .setDefaultValue(() -> Instant.now().atZone(ZoneOffset.UTC).toLocalDate()),
                new LocalTimeArgument("time", "time")
                        .setDefaultValue(() -> {
                            Instant instant = Instant.now();
                            return instant.minusNanos(instant.getNano()).atZone(ZoneOffset.UTC).toLocalTime();
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
        run((Event)event,args);
    }

    public void run(SlashCommandInteractionEvent event, Map<String, IArgument> args) {
        run((Event)event,args);
    }

    private void run(Event event, Map<String, IArgument> args) {
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
        ZoneOffset zoneOffset;
        switch (plus) {
            case "+":
                zoneOffset = ZoneOffset.ofHoursMinutes(offsetHours, offsetMinutes);
                break;
            case "-":
                zoneOffset = ZoneOffset.ofHoursMinutes(-offsetHours, -offsetMinutes);
                break;
            default:
                zoneOffset = ZoneOffset.UTC;
        }
        ;

        return OffsetDateTime.of(localDate, localTime, zoneOffset);
    }
}
