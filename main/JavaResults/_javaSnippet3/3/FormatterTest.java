package com.techielunch.dateTimeAPI;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.techielunch.dateTimeAPI.testhelper.PrintHelper.printJava8;
import static com.techielunch.dateTimeAPI.testhelper.PrintHelper.printMethodSeparator;

public class FormatterTest {
    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() throws Exception {
        printMethodSeparator(name.getMethodName());
    }

    @Test
    public void testSimpleDateFormat() throws Exception {
        // DateFormat ist weder ThreadSave noch Immutable

        final String pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX'['z']'";
        final String zonedDateTimeAsString;
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
            calendar.set(2014, Calendar.JUNE, 26, 12, 0, 0);
            simpleDateFormat.setCalendar(calendar);
            // oder
            //simpleDateFormat.setTimeZone(calendar.getTimeZone());
            // String erzeugen
            zonedDateTimeAsString = simpleDateFormat.format(calendar.getTime());
        }
        printJava8(zonedDateTimeAsString);

        // parse
        final SimpleDateFormat simpleDateFormatNew = new SimpleDateFormat(pattern);
        printJava8(simpleDateFormatNew.getTimeZone().getID());
        final Date parse = simpleDateFormatNew.parse(zonedDateTimeAsString);
        printJava8(parse);
        printJava8(simpleDateFormatNew.getTimeZone().getID());
        final GregorianCalendar calendar = new GregorianCalendar(simpleDateFormatNew.getTimeZone());
        calendar.setTime(parse);
        printJava8(simpleDateFormatNew.format(calendar.getTime()));
    }

    @Test
    public void testDateTimeFormatter() throws Exception {
        final String pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX'['z']'";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2014, 6, 26, 12, 0, 0, 0, ZoneId.of("America/Los_Angeles"));
        printJava8(zonedDateTime);
        String zonedDateTimeAsString = zonedDateTime.format(dateTimeFormatter);

        printJava8(zonedDateTimeAsString);

        // parse
        printJava8(ZonedDateTime.parse(zonedDateTimeAsString, dateTimeFormatter));
    }

    @Test
    public void testFormatterBuilder() throws Exception {
        DateTimeFormatter myFormatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.FULL)
                .appendLiteral(" ")
                .appendValue(ChronoField.YEAR)
                .appendLiteral(" ")
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral(":")
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(" Uhr")
                .appendOptional(new DateTimeFormatterBuilder()
                        .appendLiteral(" (")
                        .appendZoneOrOffsetId()
                        .appendLiteral(")")
                        .toFormatter())
                .toFormatter();

        printJava8(ZonedDateTime.now().format(myFormatter));
        printJava8(OffsetDateTime.now().format(myFormatter));
        printJava8(LocalDateTime.now().format(myFormatter));
    }
}
