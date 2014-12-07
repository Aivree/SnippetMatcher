DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder();
dtfb.append(DateTimeFormatter.ISO_LOCAL_DATE);
dtfb.appendLiteral(' ');
dtfb.append(DateTimeFormatter.ISO_LOCAL_TIME);
DateTimeFormatter dtf = dtfb.toFormatter();
ZoneId shanghai = ZoneId.of("Asia/Shanghai");

String str3 = "1927-12-31 23:54:07";
String str4 = "1927-12-31 23:54:08";

ZonedDateTime zdt3 = LocalDateTime.parse(str3, dtf).atZone(shanghai);
ZonedDateTime zdt4 = LocalDateTime.parse(str4, dtf).atZone(shanghai);

Duration durationAtEarlierOffset = Duration.between(zdt3.withEarlierOffsetAtOverlap(), zdt4.withEarlierOffsetAtOverlap());

Duration durationAtLaterOffset = Duration.between(zdt3.withLaterOffsetAtOverlap(), zdt4.withLaterOffsetAtOverlap());