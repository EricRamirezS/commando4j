import com.ericramirezs.commando4j.command.util.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtilsTest {

    @Test
    public void DateNowToDiscordTag() {
        Date date = Date.from(Instant.now());
        Assertions.assertNotNull(date);
        String tag = DateTimeUtils.toDiscordTimeStamp(date);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((date.getTime() / 1000) + "", tag);
    }

    @Test
    public void DateFixToDiscordTag() throws ParseException {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//gmt
        Date date = dateTimeFormat.parse("14-01-2020 13:12:01 UTC");

        String tag = DateTimeUtils.toDiscordTimeStamp(date);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals("1579007521", tag);
    }

    @Test
    public void OffsetDateTimeNowToDiscordTag() {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Assertions.assertNotNull(date);
        String tag = DateTimeUtils.toDiscordTimeStamp(date);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((date.toEpochSecond()) + "", tag);
    }

    @Test
    public void OffsetDateTimeFixToDiscordTag() {
        OffsetDateTime date = OffsetDateTime.of(2020, 1, 14, 10, 12, 1, 0, ZoneOffset.ofHours(-3));
        Assertions.assertNotNull(date);
        String tag = DateTimeUtils.toDiscordTimeStamp(date);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals("1579007521", tag);
    }

    @Test
    public void OffsetTimeNowToDiscordTag() {
        OffsetTime time = OffsetTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((time.toEpochSecond(LocalDate.now())) + "", tag);
    }

    @Test
    public void ZonedDateTimeToDiscordTag() {
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((time.toEpochSecond()) + "", tag);
    }

    @Test
    public void LocalDateToDiscordTag() {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.ofHours(-3));
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((time.toEpochSecond(ZoneOffset.UTC)) + "", tag);
    }

    @Test
    public void LocalTimeToDiscordTag() {
        LocalTime time = LocalTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":T>", "");
        Assertions.assertEquals((time.toEpochSecond(LocalDate.now(), ZoneOffset.UTC)) + "", tag);
    }

    @Test
    public void LocalDateTimeNowToDiscordTag() {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((time.toEpochSecond(ZoneOffset.UTC)) + "", tag);
    }

    @Test
    public void LocalDateTimeFixToDiscordTag() {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochSecond(1579018321), ZoneOffset.ofHours(-3));
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals("1579007521", tag);
    }

    @Test
    public void TimestampToDiscordTag() {
        Timestamp time = Timestamp.from(Instant.now());
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((time.toInstant().toEpochMilli() / 1000L) + "", tag);
    }

    @Test
    public void EpochLongToDiscordTag() {
        String tag = DateTimeUtils.toDiscordTimeStamp(1579018321L);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals("1579018321", tag);

    }
}
