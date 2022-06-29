package examples;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.EricRamirezS.jdacommando.command.arguments.BooleanArgument;
import org.EricRamirezS.jdacommando.command.arguments.IArgument;
import org.EricRamirezS.jdacommando.command.arguments.LocalDateArgument;
import org.EricRamirezS.jdacommando.command.arguments.LocalTimeArgument;
import org.EricRamirezS.jdacommando.command.arguments.StringArgument;
import org.EricRamirezS.jdacommando.command.command.Command;
import org.EricRamirezS.jdacommando.command.exceptions.DuplicatedArgumentNameException;
import org.EricRamirezS.jdacommando.command.tools.DateTimeUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

public class DateTimeToTagCommandExample extends Command {

    public DateTimeToTagCommandExample() throws DuplicatedArgumentNameException {
        super("datetime", "datetime", "creates a discord timestamp tag from a given date and time",
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
    public void run(@NotNull MessageReceivedEvent event, @NotNull Map<String, IArgument> args) {

        LocalDate localDate = (LocalDate) args.get("date").getValue();
        LocalTime localTime = (LocalTime) args.get("time").getValue();
        String plus = (String) args.get("Offset").getValue();
        LocalTime offsetTime = (LocalTime) args.get("offsetTime").getValue();
        Boolean asCode = (Boolean) args.get("TagCode").getValue();

        int offsetHours = offsetTime.getHour();
        int offsetMinutes = offsetTime.getMinute();

        ZoneOffset zoneOffset = switch (plus) {
            case "+" -> ZoneOffset.ofHoursMinutes(offsetHours, offsetMinutes);
            case "-" -> ZoneOffset.ofHoursMinutes(-offsetHours, -offsetMinutes);
            default -> ZoneOffset.UTC;
        };

        OffsetDateTime offDateTime = OffsetDateTime.of(localDate, localTime, zoneOffset);

        if (asCode) {
            sendReply(event, "`" + DateTimeUtils.toDiscordTimeStamp(offDateTime, TimeFormat.DATE_TIME_LONG) + "`");
        } else {
            sendReply(event, DateTimeUtils.toDiscordTimeStamp(offDateTime, TimeFormat.DATE_TIME_LONG));
        }
    }
}
