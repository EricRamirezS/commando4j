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

package com.ericramirezs.commando4j.command.util;

import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public interface DateTimeUtils {

    long SECONDS_PER_DAY = 86_400;

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final Date date) {
        return toDiscordTimeStamp(date.getTime() / 1000L, TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final OffsetDateTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(), TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param time OffsetTime Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final OffsetTime time) {
        return toDiscordTimeStamp(toEpochSecond(time, LocalDate.now()), TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final ZonedDateTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(), TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final LocalDate date) {
        return toDiscordTimeStamp(toEpochSecond(date, LocalTime.of(12, 0), ZoneOffset.UTC), TimeFormat.DATE_LONG);
    }

    static long toEpochSecond(final OffsetTime time, final LocalDate date) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(time, "time");
        final long epochDay = date.toEpochDay();
        long secs = epochDay * SECONDS_PER_DAY + time.toLocalTime().toSecondOfDay();
        secs -= time.getOffset().getTotalSeconds();
        return secs;
    }

    static long toEpochSecond(final OffsetTime time, final LocalDate date, final ZoneOffset offset) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(time, "time");
        Objects.requireNonNull(offset, "offset");
        final long epochDay = date.toEpochDay();
        long secs = epochDay * SECONDS_PER_DAY + time.toLocalTime().toSecondOfDay();
        secs -= time.getOffset().getTotalSeconds();
        return secs;
    }

    static long toEpochSecond(final LocalTime time, final LocalDate date, final ZoneOffset offset) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(time, "time");
        Objects.requireNonNull(offset, "offset");
        long secs = date.toEpochDay() * SECONDS_PER_DAY + time.toSecondOfDay();
        secs -= offset.getTotalSeconds();
        return secs;
    }

    static long toEpochSecond(final LocalDate date, final LocalTime time, final ZoneOffset offset) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(time, "time");
        Objects.requireNonNull(offset, "offset");
        long secs = date.toEpochDay() * SECONDS_PER_DAY + time.toSecondOfDay();
        secs -= offset.getTotalSeconds();
        return secs;
    }

    /**
     * Converts most Time object types to a Discord Timestamp
     *
     * @param time time Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final LocalTime time) {
        return toDiscordTimeStamp(toEpochSecond(time, LocalDate.now(), ZoneOffset.UTC), TimeFormat.TIME_LONG);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final LocalDateTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(ZoneOffset.UTC), TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final Timestamp date) {
        return toDiscordTimeStamp(date.getTime() / 1000L, TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(final Long date) {
        return toDiscordTimeStamp(date, TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final Date date, final TimeFormat format) {
        return toDiscordTimeStamp(date.getTime() / 1000L, format);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final OffsetDateTime date, final TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(), format);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final OffsetTime date, final TimeFormat format) {
        return toDiscordTimeStamp(toEpochSecond(date, LocalDate.now()), format);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final ZonedDateTime date, final TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(), format);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final LocalDate date, final TimeFormat format) {
        return toDiscordTimeStamp(toEpochSecond(date, LocalTime.now(), ZoneOffset.UTC), format);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final LocalDateTime date, final TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(ZoneOffset.UTC), format);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull final Timestamp date, final TimeFormat format) {
        return toDiscordTimeStamp(date.getTime() / 1000L, format);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(final Long date, @NotNull final TimeFormat format) {
        return "<t:" + date + ":" + format.getStyle() + ">";
    }

    /**
     * Parses a String to a Date object, using the pattern related to a locale object
     *
     * @param string String representation of a date
     * @param locale Localization object to extract date pattern
     * @return A Date object from parsed string
     * @throws ParseException Thrown when the parse fails.
     */
    static @NotNull Date stringToDate(final String string, final Locale locale) throws ParseException {
        final DateFormat dtf = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        dtf.setLenient(false);
        return dtf.parse(string);
    }

    /**
     * Parses a String to a Date object, using the pattern related to a locale object
     *
     * @param string  String representation of a date
     * @param pattern String Date's pattern
     * @return A Date object from parsed string
     * @throws ParseException Thrown when the parse fails.
     */
    static @NotNull Date stringToDate(final String string, final String pattern) throws ParseException {
        final SimpleDateFormat dtf = new SimpleDateFormat(pattern);
        dtf.setLenient(false);
        return dtf.parse(string);
    }

    /**
     * Parses a String to a LocalDate object, using the pattern related to a locale object
     *
     * @param string String representation of a date
     * @param locale Localization object to extract date pattern
     * @return A LocalDate object from parsed string
     * @throws ParseException Thrown when the parse fails.
     */
    static @NotNull LocalDate stringToLocalDate(final String string, final Locale locale) throws ParseException {
        return LocalDateOfInstant(stringToDate(string, locale).toInstant(), ZoneOffset.UTC);
    }

    /**
     * Parses a String to a LocalDate object, using the pattern related to a locale object
     *
     * @param string  String representation of a date
     * @param pattern String Date's pattern
     * @return A LocalDate object from parsed string
     * @throws ParseException Thrown when the parse fails.
     */
    static @NotNull LocalDate stringToLocalDate(final String string, final String pattern) throws ParseException {
        return LocalDateOfInstant(stringToDate(string, pattern).toInstant(), ZoneOffset.UTC);
    }

    static LocalDate LocalDateOfInstant(final Instant instant, final ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
        final ZoneRules rules = zone.getRules();
        final ZoneOffset offset = rules.getOffset(instant);
        final long localSecond = instant.getEpochSecond() + offset.getTotalSeconds();
        final long localEpochDay = Math.floorDiv(localSecond, SECONDS_PER_DAY);
        return LocalDate.ofEpochDay(localEpochDay);
    }

    /**
     * Get the Date's pattern from a Locale object
     *
     * @param locale Locale target
     * @return Date's pattern
     */
    static @NotNull String localeToDateFormat(final Locale locale) {
        final DateFormat dtf = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        final String patter = ((SimpleDateFormat) dtf).toLocalizedPattern();
        return patter.replaceAll("(d)+", "dd").replaceAll("(M)+", "MM").replaceAll("(y)+", "yyyy");
    }

    /**
     * Converts a String into a LocalTime object
     *
     * @param string String representation of a LocalTime
     * @return Parsed LocalTime
     */
    static @NotNull LocalTime stringToLocalTime(@NotNull String string) {
        final String[] format = string.split(":");
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