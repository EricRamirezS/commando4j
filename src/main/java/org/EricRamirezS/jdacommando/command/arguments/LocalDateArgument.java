package org.EricRamirezS.jdacommando.command.arguments;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.EricRamirezS.jdacommando.command.CommandEngine;
import org.EricRamirezS.jdacommando.command.customizations.LocalizedFormat;
import org.EricRamirezS.jdacommando.command.enums.ArgumentTypes;
import org.EricRamirezS.jdacommando.command.tools.DateTimeUtils;
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

public class LocalDateArgument extends LocalDateTimeArgument<LocalDateArgument, LocalDate> {

    public LocalDateArgument(@NotNull String name, @NotNull String prompt) {
        super(name, prompt, ArgumentTypes.DATE);
    }

    @Override
    public String validate(MessageReceivedEvent event, @NotNull String arg) {
        Locale locale = CommandEngine.getInstance().getLanguage(event);
        String pattern = CommandEngine.getInstance().getString("Argument_Date_Pattern", locale);

        if (arg.matches("<t:\\d*(:[tTdDfFR])?>")) {
            try {
                Matcher matcher = Pattern.compile("<t:(\\d+)(:[tTdDfFR])?>").matcher(arg);
                //noinspection ResultOfMethodCallIgnored
                matcher.find();
                String match = matcher.group(1);
                long epochSeconds = Long.parseLong(match);
                Instant date = Instant.ofEpochSecond(epochSeconds);
                return oneOf(LocalDate.ofInstant(date, ZoneOffset.UTC), event,
                        localDate -> localDate.format(DateTimeFormatter.ofPattern(DateTimeUtils.localeToDateFormat(locale))),
                        "Argument_Date_OneOf");
            } catch (Exception ignored) {
                return null; //if pattern is matched, then there should never be an exception.
            }
        }
        if (isForcedDiscordTag()){
            return LocalizedFormat.format("Argument_DateTime_TimestamptOnly",event, DateTimeUtils.toDiscordTimeStamp(LocalDateTime.now(ZoneOffset.UTC)));
        }
        try {
            // Parsing with locale's pattern
            LocalDate date = DateTimeUtils.stringToLocalDate(arg, locale);
            return oneOf(date, event,
                    localDate -> localDate.format(DateTimeFormatter.ofPattern(DateTimeUtils.localeToDateFormat(locale))),
                    "Argument_Date_OneOf");
        } catch (ParseException e) {
            try {
                // Parsing with pattern defined in bundle
                LocalDate date = DateTimeUtils.stringToLocalDate(arg, pattern);
                return oneOf(date, event,
                        localDate -> localDate.format(DateTimeFormatter.ofPattern(DateTimeUtils.localeToDateFormat(locale))),
                        "Argument_Date_OneOf");
            } catch (ParseException ex) {
                String localePattern = DateTimeUtils.localeToDateFormat(locale);
                if (localePattern.equals(pattern))
                    return LocalizedFormat.format("Argument_Date_Invalid", event, DateTimeUtils.localeToDateFormat(locale));
                return LocalizedFormat.format("Argument_Date_Invalid", event,
                        LocalizedFormat.format("NormalText_Or", event, localePattern + " ", " " + pattern)
                );
            }
        }
    }

    @Override
    public LocalDate parse(MessageReceivedEvent event, @NotNull String arg) {
        Locale locale = CommandEngine.getInstance().getLanguage(event);
        String pattern = CommandEngine.getInstance().getString("Argument_Date_Pattern", locale);

        if (arg.matches("<t:\\d*(:[tTdDfFR])?>")) {
            Matcher matcher = Pattern.compile("<t:(\\d+)(:[tTdDfFR])?>").matcher(arg);
            //noinspection ResultOfMethodCallIgnored
            matcher.find();
            String match = matcher.group(1);
            long epochSeconds = Long.parseLong(match);
            Instant date = Instant.ofEpochSecond(epochSeconds);
            return LocalDate.ofInstant(date, ZoneOffset.UTC);
        }
        try {
            // Parsing with locale's pattern
            return DateTimeUtils.stringToLocalDate(arg, locale);
        } catch (ParseException e) {
            // Parsing with pattern defined in bundle
            try {
                return DateTimeUtils.stringToLocalDate(arg, pattern);
            } catch (ParseException ignored) {
                // if validation was a success, this code is unreachable
            }
        }
        return null;
    }


}
