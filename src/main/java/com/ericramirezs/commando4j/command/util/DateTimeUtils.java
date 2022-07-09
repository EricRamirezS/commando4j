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
    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull Date date) {
        return toDiscordTimeStamp(date.getTime() / 1000L, TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull OffsetDateTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(), TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull OffsetTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(LocalDate.now()), TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull ZonedDateTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(), TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull LocalDate date) {
        return toDiscordTimeStamp(date.toEpochSecond(LocalTime.of(12, 0), ZoneOffset.UTC), TimeFormat.DATE_LONG);
    }

    /**
     * Converts most Time object types to a Discord Timestamp
     *
     * @param time time Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull LocalTime time) {
        return toDiscordTimeStamp(time.toEpochSecond(LocalDate.now(), ZoneOffset.UTC), TimeFormat.TIME_LONG);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull LocalDateTime date) {
        return toDiscordTimeStamp(date.toEpochSecond(ZoneOffset.UTC), TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull Timestamp date) {
        return toDiscordTimeStamp(date.getTime() / 1000L, TimeFormat.DEFAULT);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date date Object
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(Long date) {
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
    static @NotNull String toDiscordTimeStamp(@NotNull Date date, TimeFormat format) {
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
    static @NotNull String toDiscordTimeStamp(@NotNull OffsetDateTime date, TimeFormat format) {
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
    static @NotNull String toDiscordTimeStamp(@NotNull OffsetTime date, TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(LocalDate.now()), format);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull ZonedDateTime date, TimeFormat format) {
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
    static @NotNull String toDiscordTimeStamp(@NotNull LocalDate date, TimeFormat format) {
        return toDiscordTimeStamp(date.toEpochSecond(LocalTime.now(), ZoneOffset.UTC), format);
    }

    /**
     * Converts most Date object types to a Discord Timestamp
     *
     * @param date   date Object
     * @param format Markdown styles for timestamps used to represent a unix epoch timestamp in different formats.
     * @return discord timestamp tag
     */
    @Contract(pure = true)
    static @NotNull String toDiscordTimeStamp(@NotNull LocalDateTime date, TimeFormat format) {
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
    static @NotNull String toDiscordTimeStamp(@NotNull Timestamp date, TimeFormat format) {
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
    static @NotNull String toDiscordTimeStamp(Long date, @NotNull TimeFormat format) {
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
    static @NotNull Date stringToDate(String string, Locale locale) throws ParseException {
        DateFormat dtf = DateFormat.getDateInstance(DateFormat.SHORT, locale);
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
    static @NotNull Date stringToDate(String string, String pattern) throws ParseException {
        SimpleDateFormat dtf = new SimpleDateFormat(pattern);
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
    static @NotNull LocalDate stringToLocalDate(String string, Locale locale) throws ParseException {
        return LocalDate.ofInstant(stringToDate(string, locale).toInstant(), ZoneOffset.UTC);
    }

    /**
     * Parses a String to a LocalDate object, using the pattern related to a locale object
     *
     * @param string  String representation of a date
     * @param pattern String Date's pattern
     * @return A LocalDate object from parsed string
     * @throws ParseException Thrown when the parse fails.
     */
    static @NotNull LocalDate stringToLocalDate(String string, String pattern) throws ParseException {
        return LocalDate.ofInstant(stringToDate(string, pattern).toInstant(), ZoneOffset.UTC);
    }

    /**
     * Get the Date's pattern from a Locale object
     *
     * @param locale Locale target
     * @return Date's pattern
     */
    static @NotNull String localeToDateFormat(Locale locale) {
        DateFormat dtf = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        String patter = ((SimpleDateFormat) dtf).toLocalizedPattern();
        return patter.replaceAll("(d)+", "dd").replaceAll("(M)+", "MM").replaceAll("(y)+", "yyyy");
    }

    /**
     * Converts a String into a LocalTime object
     *
     * @param string String representation of a LocalTime
     * @return Parsed LocalTime
     */
    static @NotNull LocalTime stringToLocalTime(@NotNull String string) {
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