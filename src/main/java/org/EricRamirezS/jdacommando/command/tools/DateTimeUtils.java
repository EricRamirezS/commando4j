package org.EricRamirezS.jdacommando.command.tools;

import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public interface DateTimeUtils {
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull Date date) {
        return toDiscordTimeStamp(date.getTime() / 1000L, TimeFormat.DEFAULT);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull OffsetDateTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(), TimeFormat.DEFAULT);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull OffsetTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(LocalDate.now()), TimeFormat.DEFAULT);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull ZonedDateTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(), TimeFormat.DEFAULT);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull LocalDate date) {
        return toDiscordTimeStamp(date.toEpochSecond(LocalTime.of(12, 0), ZoneOffset.UTC), TimeFormat.DATE_LONG);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull LocalTime time) {
        return toDiscordTimeStamp(time.toEpochSecond(LocalDate.now(), ZoneOffset.UTC), TimeFormat.TIME_LONG);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull LocalDateTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(ZoneOffset.UTC), TimeFormat.DEFAULT);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull Timestamp date) {
        return toDiscordTimeStamp(date.getTime() / 1000L, TimeFormat.DEFAULT);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(Long date) {
        return toDiscordTimeStamp(date, TimeFormat.DEFAULT);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull Date date, TimeFormat format) {
        return toDiscordTimeStamp(date.getTime() / 1000L, format);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull OffsetDateTime date, TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(), format);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull OffsetTime date, TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(LocalDate.now()), format);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull ZonedDateTime date, TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(), format);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull LocalDate date, TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(LocalTime.now(), ZoneOffset.UTC), format);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull LocalDateTime date, TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(ZoneOffset.UTC), format);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull Timestamp date, TimeFormat format) {
        return toDiscordTimeStamp(date.getTime() / 1000L, format);
    }

    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(Long date, @NotNull TimeFormat format) {
        return "<t:" + date + ":" + format.getStyle() + ">";
    }

    static @NotNull Date stringToDate(String string, Locale locale) throws ParseException {
        DateFormat dtf = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        dtf.setLenient(false);
        return dtf.parse(string);
    }

    static @NotNull Date stringToDate(String string, String pattern) throws ParseException {
        SimpleDateFormat dtf = new SimpleDateFormat(pattern);
        dtf.setLenient(false);
        return dtf.parse(string);
    }

    static @NotNull LocalDate stringToLocalDate(String string, Locale locale) throws ParseException {
        return LocalDate.ofInstant(stringToDate(string, locale).toInstant(), ZoneOffset.UTC);
    }

    static @NotNull LocalDate stringToLocalDate(String string, String pattern) throws ParseException {
        return LocalDate.ofInstant(stringToDate(string, pattern).toInstant(), ZoneOffset.UTC);
    }

    static @NotNull String localeToDateFormat(Locale locale) {
        DateFormat dtf = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        String patter = ((SimpleDateFormat) dtf).toLocalizedPattern();
        return patter.replaceAll("(d)+", "dd").replaceAll("(M)+", "MM").replaceAll("(y)+", "yyyy");
    }

    static @NotNull LocalTime stringToLocalTime(String string) {
        String[] format = string.split(":");
        if (format.length == 1) {
            if (string.length() == 1)
                    string = "0" + string;
            string += ":00:00";
        } else if (format.length == 2) {
            string += ":00";
        }
        return LocalTime.parse(string, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}