package com.netcetera.ncau.threeten;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;

public class ApiDemoTest {

  @Test
  public void localTime() {
    LocalTime time1 = LocalTime.of(12, 15, 0);
    LocalTime time2 = LocalTime.parse("12:15:00");
    assertEquals(time1, time2);
  }

  @Test
  public void localTimeFormat() {
    LocalTime time = LocalTime.of(12, 15, 0);
    assertEquals("12:15:00", String.format("%tT", time));
  }

  @Test
  public void localDate() {
    LocalDate date1 = LocalDate.of(2014, 3, 30);
    date1 = LocalDate.of(2014, Month.MARCH, 30);
    LocalDate date2 = LocalDate.parse("2014-03-30");
    assertEquals(date1, date2);
  }

  @Test
  public void localDateTime() {
    LocalDate date = LocalDate.parse("2014-03-30");
    LocalTime time = LocalTime.parse("12:15:00");
    LocalDateTime dateTime = LocalDateTime.of(date, time);
    assertEquals(LocalDateTime.of(2014, 03, 30, 12, 15, 0), dateTime);
  }

  @Test
  public void truncate() {
    ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    assertEquals(0, now.getSecond());
    assertEquals(0, now.getNano());
  }

  @Test
  public void timeZones() {
    LocalDateTime dateTime = LocalDateTime.parse("2014-03-28T10:15:30");
    ZoneId zone = ZoneId.of("Europe/Zurich");
    ZoneOffset offset = ZoneOffset.of("+01:00");

    ZonedDateTime zonedDate = ZonedDateTime.of(dateTime, zone);
    OffsetDateTime offsetDate = OffsetDateTime.of(dateTime, offset);
    assertEquals(zonedDate.toInstant(), offsetDate.toInstant());
  }
  
  @Test
  public void conversionToLocalDate() {
    LocalDateTime dateTime = LocalDateTime.parse("2014-03-28T10:15:30");
    LocalDate date = LocalDate.parse("2014-03-28");
    assertEquals(date, dateTime.toLocalDate());
  }

  @Test
  public void parse() {
    LocalDate dateFromInts = LocalDate.of(2007, 12, 3);
    LocalDate dateFromString = LocalDate.parse("2007-12-03");
    DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
    LocalDate dateFromFormatter = LocalDate.parse("20071203", formatter);
    assertEquals(dateFromInts, dateFromString);
    assertEquals(dateFromInts, dateFromFormatter);
  }

  @Test
  public void format() {
    DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
    LocalDate date = LocalDate.parse("20071203", formatter);
    assertEquals("2007-12-03", date.toString());
    assertEquals("20071203", date.format(formatter));
  }

  @Test
  public void formatter() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.US);
    LocalDate date = LocalDate.of(2007, Month.DECEMBER, 3);
    assertEquals("3 Dec 2007", date.format(formatter));
  }

  @Test
  public void javaUtilFormatter() {
    LocalDate date = LocalDate.of(2007, Month.DECEMBER, 3);
    assertEquals("Duke's Birthday: 3 Dec 2007", String.format("Duke's Birthday: %1$te %1$tb %1$tY", date));
  }
  
  @Test
  public void conversionDateToDuration() {
    Date date1 = new Date(1L);
    Date date2 = new Date(100001L);
    Duration duration = Duration.between(date1.toInstant(), date2.toInstant());
    assertEquals(100L, duration.getSeconds());
    assertEquals(0L, duration.getNano());
    assertEquals(100L, duration.get(ChronoUnit.SECONDS));
    assertEquals(0L, duration.get(ChronoUnit.NANOS));
  }
  
  @Test
  public void conversionDateToZoned() {
    Date date = new Date(100001L);
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    assertEquals(1970, zonedDateTime.getYear());
  }
  
  @Test
  @Ignore("does not work")
  public void simpleDateFormat() {
    LocalDate date = LocalDate.of(2007, Month.DECEMBER, 3);
    SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy");
    assertEquals("Duke Birthday: 3 Dec 2007", format.format(date));
  }
  
  @Test
  @Ignore("does not work")
  public void messageFormat() {
    LocalDate date = LocalDate.of(2007, Month.DECEMBER, 3);
    String format = MessageFormat.format(
        "Duke Birthday: {0,date,d MMM yyyy}", date);
    assertEquals("Duke Birthday: 3 Dec 2007", format);
  }

  @Test
  public void formatterBuilder() {
    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
      .appendValue(HOUR_OF_DAY, 1, 2, SignStyle.NEVER)
        .optionalStart() // {
          .appendLiteral(":").appendValue(MINUTE_OF_HOUR, 2)
           .optionalStart() // {
             .appendLiteral(":").appendValue(SECOND_OF_MINUTE, 2)
            .optionalEnd() // }
        .optionalEnd() // }
        .parseDefaulting(MINUTE_OF_HOUR, 1)
        .parseDefaulting(SECOND_OF_MINUTE, 0) .toFormatter();
    LocalTime localTime = LocalTime.parse("8", formatter);
    assertEquals(LocalTime.of(8, 1, 0), localTime);
    localTime = LocalTime.parse("8:02", formatter);
    assertEquals(LocalTime.of(8, 2, 0), localTime);
    localTime = LocalTime.parse("8:03:04", formatter);
    assertEquals(LocalTime.of(8, 3, 4), localTime);
  }

  @Test
  public void adjuster() {
    ZonedDateTime dateTime = ZonedDateTime.parse("2014-02-11T22:02:57.291+01:00[Europe/Zurich]");
    ZonedDateTime endOfMonth = dateTime.with(TemporalAdjusters.lastDayOfMonth()).truncatedTo(SECONDS);
    ZonedDateTime wednesday = dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY)).truncatedTo(SECONDS);
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    assertEquals("2014-02-28T22:02:57", endOfMonth.format(formatter));
    assertEquals("2014-02-05T22:02:57", wednesday.format(formatter));
  }

}
