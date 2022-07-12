import com.ericramirezs.commando4j.util.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtilsTest {

    @Test
    public void DateNowToDiscordTag() {
        final Date date = Date.from(Instant.now());
        Assertions.assertNotNull(date);
        String tag = DateTimeUtils.toDiscordTimeStamp(date);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((date.getTime() / 1000) + "", tag);
    }

    @Test
    public void DateFixToDiscordTag() throws ParseException {
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//gmt
        final Date date = dateTimeFormat.parse("14-01-2020 13:12:01 UTC");

        String tag = DateTimeUtils.toDiscordTimeStamp(date);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals("1579007521", tag);
    }

    @Test
    public void OffsetDateTimeNowToDiscordTag() {
        final OffsetDateTime date = OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Assertions.assertNotNull(date);
        String tag = DateTimeUtils.toDiscordTimeStamp(date);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((date.toEpochSecond()) + "", tag);
    }

    @Test
    public void OffsetDateTimeFixToDiscordTag() {
        final OffsetDateTime date = OffsetDateTime.of(2020, 1, 14, 10, 12, 1, 0, ZoneOffset.ofHours(-3));
        Assertions.assertNotNull(date);
        String tag = DateTimeUtils.toDiscordTimeStamp(date);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals("1579007521", tag);
    }

    @Test
    public void ZonedDateTimeToDiscordTag() {
        final ZonedDateTime time = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((time.toEpochSecond()) + "", tag);
    }

    @Test
    public void LocalDateToDiscordTag() {
        final LocalDateTime time = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.ofHours(-3));
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((time.toEpochSecond(ZoneOffset.UTC)) + "", tag);
    }

    @Test
    public void LocalDateTimeNowToDiscordTag() {
        final LocalDateTime time = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals((time.toEpochSecond(ZoneOffset.UTC)) + "", tag);
    }

    @Test
    public void LocalDateTimeFixToDiscordTag() {
        final LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochSecond(1579018321), ZoneOffset.ofHours(-3));
        Assertions.assertNotNull(time);
        String tag = DateTimeUtils.toDiscordTimeStamp(time);
        Assertions.assertNotNull(tag);
        tag = tag.replace("<t:", "").replace(":f>", "");
        Assertions.assertEquals("1579007521", tag);
    }

    @Test
    public void TimestampToDiscordTag() {
        final Timestamp time = Timestamp.from(Instant.now());
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
