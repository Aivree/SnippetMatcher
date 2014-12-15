/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.modelinglab.ocl.ext.time;

import java.util.Arrays;
import java.util.List;
import org.threeten.bp.*;
import org.threeten.bp.chrono.*;
import org.threeten.bp.format.*;
import org.threeten.bp.temporal.*;
import org.threeten.bp.zone.*;

/**
 *
 */
class MappedClassProvider {
    private MappedClassProvider() {}
    
    public static List<Class<?>> getMappedClasses() {
        Class<?>[] classes = new Class<?>[] {
            //java.time package
            Clock.class,
            Duration.class,
            Instant.class,
            LocalDate.class,
            LocalDateTime.class,
            LocalTime.class,
            MonthDay.class,
            OffsetDateTime.class,
            OffsetTime.class,
            Period.class,
            Year.class,
            YearMonth.class,
            ZonedDateTime.class,
            ZoneId.class,
            ZoneOffset.class,
            DayOfWeek.class,
            Month.class,
                
            //java.time.chrono package
            ChronoLocalDate.class,
            ChronoLocalDateTime.class,
            ChronoZonedDateTime.class,
            Era.class,
            Chronology.class,
            HijrahChronology.class,
            HijrahDate.class,
            IsoChronology.class,
            JapaneseChronology.class,
            JapaneseDate.class,
            MinguoChronology.class,
            MinguoDate.class,
            ThaiBuddhistChronology.class,
            ThaiBuddhistDate.class,
            //not suported in org.threeten.bp
//            HijrahEra.class,
//            IsoEra.class,
//            MinguoEra.class,
//            ThaiBuddhistEra.class,
            
            //java.time.format package
            DateTimeFormatter.class,
            DateTimeFormatterBuilder.class,
//            DecimalStyle.class,
            FormatStyle.class,
//            ResolverStyle.class,
            SignStyle.class,
            TextStyle.class,
            
            //java.util.temporal package
            Temporal.class,
            TemporalAccessor.class,
            TemporalAdjuster.class,
            TemporalAmount.class,
            TemporalField.class,
            TemporalQueries.class,
            TemporalUnit.class,
            IsoFields.class,
            JulianFields.class,
            ValueRange.class,
            WeekFields.class,
            ChronoField.class,
            ChronoUnit.class
        };
        return Arrays.asList(classes);
    }
}
