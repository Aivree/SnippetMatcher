package study.date.java8;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateTimeFormatterSample {
	
	/**
	 * Java8
	 * refs : http://acro-engineer.hatenablog.com/entry/20130213/1360691391
	 */
	public static void main(String... args) {
		
		ln();
		pr("DateTimeFormatter");
		ln();
		
		LocalDateTime dateTime = LocalDateTime.of(2012, 2, 3, 21, 30, 15, 123);

		pr(dateTime.format(DateTimeFormatter.ISO_DATE_TIME));
		pr(dateTime.format(DateTimeFormatter.ISO_DATE));
		pr(dateTime.format(DateTimeFormatter.ISO_TIME));
		pr(dateTime.format(DateTimeFormatter.ISO_WEEK_DATE));
		
		ZonedDateTime znow = ZonedDateTime.now();
		pr(znow.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
		
		pr(dateTime.format(DateTimeFormatter.ofPattern("yyyy~~MM~~dd HH:mm:ss")));
		
		ln();
		pr("DateTimeFormatterBuilder");
		ln();
		
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR)
        .appendLiteral("/")
        .appendValue(ChronoField.MONTH_OF_YEAR)
        .appendLiteral("/")
        .appendValue(ChronoField.DAY_OF_MONTH)
        .appendLiteral(" ")
        .appendValue(ChronoField.HOUR_OF_DAY)
        .appendLiteral("---")
        .appendValue(ChronoField.MINUTE_OF_HOUR)
        .appendLiteral("~~~")
        .appendValue(ChronoField.SECOND_OF_MINUTE)
        .toFormatter();

		pr(dateTime.format(formatter));
		
    }
	
	static void pr(Object s){
		System.out.println(s);
	}
	
	static void ln(){
		System.out.println("");
	}

}
