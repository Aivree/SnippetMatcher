package java.util;
public abstract class TimeZone
  implements java.io.Serializable, java.lang.Cloneable
{
public  TimeZone() { throw new RuntimeException("Stub!"); }
public  java.lang.Object clone() { throw new RuntimeException("Stub!"); }
public static synchronized  java.lang.String[] getAvailableIDs() { throw new RuntimeException("Stub!"); }
public static synchronized  java.lang.String[] getAvailableIDs(int offset) { throw new RuntimeException("Stub!"); }
public static synchronized  java.util.TimeZone getDefault() { throw new RuntimeException("Stub!"); }
public final  java.lang.String getDisplayName() { throw new RuntimeException("Stub!"); }
public final  java.lang.String getDisplayName(java.util.Locale locale) { throw new RuntimeException("Stub!"); }
public final  java.lang.String getDisplayName(boolean daylightTime, int style) { throw new RuntimeException("Stub!"); }
public  java.lang.String getDisplayName(boolean daylightTime, int style, java.util.Locale locale) { throw new RuntimeException("Stub!"); }
public  java.lang.String getID() { throw new RuntimeException("Stub!"); }
public  int getDSTSavings() { throw new RuntimeException("Stub!"); }
public  int getOffset(long time) { throw new RuntimeException("Stub!"); }
public abstract  int getOffset(int era, int year, int month, int day, int dayOfWeek, int time);
public abstract  int getRawOffset();
public static synchronized  java.util.TimeZone getTimeZone(java.lang.String name) { throw new RuntimeException("Stub!"); }
public  boolean hasSameRules(java.util.TimeZone zone) { throw new RuntimeException("Stub!"); }
public abstract  boolean inDaylightTime(java.util.Date time);
public static synchronized  void setDefault(java.util.TimeZone timezone) { throw new RuntimeException("Stub!"); }
public  void setID(java.lang.String name) { throw new RuntimeException("Stub!"); }
public abstract  void setRawOffset(int offset);
public abstract  boolean useDaylightTime();
public static final int SHORT = 0;
public static final int LONG = 1;
}
