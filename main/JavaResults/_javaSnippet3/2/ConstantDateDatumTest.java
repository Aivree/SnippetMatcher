package org.ak.datagen.data;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Date;

public class ConstantDateDatumTest {

    @Test
    public void testThatWhenConstantDateDatumConstructedWithDateObjectWhenGenerateIsCalledThatDateObjectIsReturned() {
        ZonedDateTime expectedDate = ZonedDateTime.now();
        ConstantDateDatum constantDateDatum = new ConstantDateDatum("name", expectedDate, DateTimeFormatter.ISO_DATE);

        assertThat(constantDateDatum.generate(), is(expectedDate));
    }

    @Test
    public void testWhenAConstantDateDatumIsConstructedWithALocalDateTimeThenTheDefaultTimeZoneForTheDefaultLocaleIsUsedAsTimeZoneOffset() {

        LocalDateTime localDateTime = LocalDateTime.now();
        ConstantDateDatum constantDateDatum = new ConstantDateDatum("name", localDateTime, DateTimeFormatter.ISO_DATE);

        assertThat(constantDateDatum.generate().getZone(), is(equalTo(ZoneId.systemDefault())));
    }

    @Test
    public void testWhenAConstantDateDatumIsConstructedWithOnlyLocalDateThenTheStartOfTheDayIsUsedAsTheTime() {

        LocalDate localDate = LocalDate.now();
        ConstantDateDatum constantDateDatum = new ConstantDateDatum("name", localDate, DateTimeFormatter.ISO_DATE);

        assertThat(constantDateDatum.generate().getHour(),  is(0));
        assertThat(constantDateDatum.generate().getMinute(),is(0));
        assertThat(constantDateDatum.generate().getSecond(),is(0));
        assertThat(constantDateDatum.generate().getNano(),  is(0));
    }

}