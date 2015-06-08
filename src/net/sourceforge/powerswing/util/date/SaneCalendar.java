package net.sourceforge.powerswing.util.date;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class SaneCalendar extends GregorianCalendar{
    public final static int MONDAY = 1;

    public final static int TUESDAY = 2;

    public final static int WEDNESDAY = 3;

    public final static int THURSDAY = 4;

    public final static int FRIDAY = 5;

    public final static int SATURDAY = 6;

    public final static int SUNDAY = 7;

    public final static int JANUARY = 1;

    public final static int FEBRUARY = 2;

    public final static int MARCH = 3;

    public final static int APRIL = 4;

    public final static int MAY = 5;

    public final static int JUNE = 6;

    public final static int JULY = 7;

    public final static int AUGUST = 8;

    public final static int SEPTEMBER = 9;

    public final static int OCTOBER = 10;

    public final static int NOVEMBER = 11;

    public final static int DECEMBER = 12;

    static private TimeZone utc;

    static private Date minDate;

    static {
        utc = TimeZone.getTimeZone("UTC");
        minDate = new Date(Long.MIN_VALUE);
    }

    public SaneCalendar() {
        this(utc);
    }

    public SaneCalendar(TimeZone tz) {
        super(tz);
        setLenient(false);
        setGregorianChange(minDate);
    }

    public SaneCalendar(long millis) {
        this(utc, millis);
    }

    public SaneCalendar(TimeZone tz, long millis) {
        super(tz);
        setLenient(false);
        setGregorianChange(minDate);

        setTimeInMillis(millis);
    }

    public SaneCalendar(SaneCalendar cal) {
        super(cal.getTimeZone());
        setLenient(false);
        setGregorianChange(cal.getGregorianChange());

        setTime(cal.getTime());
    }

    public SaneCalendar(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean lenient) {
        this(utc, year, month, day, hour, minute, second, millisecond, lenient);
    }

    public SaneCalendar(TimeZone tz, int year, int month, int day, int hour, int minute, int second, int millisecond, boolean lenient) {
        super(tz);
        setLenient(lenient);
        setGregorianChange(minDate);

        setYear(year);
        setMonth(month);
        setDayOfMonth(day);
        setHour24(hour);
        setMinute(minute);
        setSecond(second);
        setMillisecond(millisecond);
        if (!lenient) {
            computeTime(); // force GregorianCalendar to check if date is legal
        }
        setLenient(false);
    }

    public SaneCalendar(TimeStamp cal) {
        this(cal.getTimeInMillis());
    }

    public SaneCalendar(TimeZone tz, TimeStamp cal) {
        this(tz, cal.getTimeInMillis());
    }

    public SaneCalendar(int year, int month, int day, boolean lenient) {
        this(year, month, day, 0, 0, 0, 0, lenient);
    }

    public SaneCalendar(int year, int month, int day) {
        this(year, month, day, 0, 0, 0, 0, false);
    }

    public SaneCalendar(int year, int month, int day, int hour, int minute, boolean lenient) {
        this(year, month, day, hour, minute, 0, 0, lenient);
    }

    public SaneCalendar(int year, int month, int day, int hour, int minute) {
        this(year, month, day, hour, minute, 0, 0, false);
    }

    public SaneCalendar(int year, int month, int day, int hour, int minute, int second, boolean lenient) {
        this(year, month, day, hour, minute, second, 0, lenient);
    }

    public SaneCalendar(int year, int month, int day, int hour, int minute, int second) {
        this(year, month, day, hour, minute, second, 0, false);
    }

    public SaneCalendar(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this(year, month, day, hour, minute, second, millisecond, false);
    }

    public SaneCalendar(TimeZone tz, int year, int month, int day, boolean lenient) {
        this(tz, year, month, day, 0, 0, 0, 0, lenient);
    }

    public SaneCalendar(TimeZone tz, int year, int month, int day) {
        this(tz, year, month, day, 0, 0, 0, 0, false);
    }

    public SaneCalendar(TimeZone tz, int year, int month, int day, int hour, int minute, boolean lenient) {
        this(tz, year, month, day, hour, minute, 0, 0, lenient);
    }

    public SaneCalendar(TimeZone tz, int year, int month, int day, int hour, int minute) {
        this(tz, year, month, day, hour, minute, 0, 0, false);
    }

    public SaneCalendar(TimeZone tz, int year, int month, int day, int hour, int minute, int second, boolean lenient) {
        this(tz, year, month, day, hour, minute, second, 0, lenient);
    }

    public SaneCalendar(TimeZone tz, int year, int month, int day, int hour, int minute, int second) {
        this(tz, year, month, day, hour, minute, second, 0, false);
    }

    public SaneCalendar(TimeZone tz, int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this(tz, year, month, day, hour, minute, second, millisecond, false);
    }

    public int getYear() {
        return get(Calendar.YEAR);
    }

    public void setYear(int year) {
        set(Calendar.YEAR, year);
    }

    public void rollYear(int year) {
        roll(Calendar.YEAR, year);
    }

    public void addYear(int year) {
        add(Calendar.YEAR, year);
    }

    public int getMonth() {
        return get(Calendar.MONTH) + 1;
    }

    public int getMonth0() {
        return get(Calendar.MONTH);
    }

    public void setMonth(int month) {
        set(Calendar.MONTH, month - 1);
    }

    public void setMonth0(int month) {
        set(Calendar.MONTH, month);
    }

    public void rollMonth(int month) {
        roll(Calendar.MONTH, month);
    }

    public void addMonth(int month) {
        add(Calendar.MONTH, month);
    }

    public int getDayOfMonth() {
        return get(Calendar.DAY_OF_MONTH);
    }

    public void setDayOfMonth(int dayofmonth) {
        set(Calendar.DAY_OF_MONTH, dayofmonth);
    }

    public void rollDayOfMonth(int dayofmonth) {
        roll(Calendar.DAY_OF_MONTH, dayofmonth);
    }

    public void addDay(int day) {
        add(Calendar.DAY_OF_MONTH, day);
    }

    public int getDayOfWeek() {
        return ((get(Calendar.DAY_OF_WEEK) + 5) % 7) + 1;
    }

    public void setDayOfWeek(int dayofweek) {
        set(Calendar.DAY_OF_WEEK, (dayofweek % 7) + 1);
    }

    public void rollDayOfWeek(int dayofweek) {
        roll(Calendar.DAY_OF_WEEK, dayofweek);
    }

    public int getHour12() {
        return get(Calendar.HOUR);
    }

    public void setHour12(int hour12) {
        set(Calendar.HOUR, hour12);
    }

    public void rollHour12(int hour12) {
        roll(Calendar.HOUR, hour12);
    }

    public int getHour24() {
        return get(Calendar.HOUR_OF_DAY);
    }

    public void setHour24(int hour24) {
        set(Calendar.HOUR_OF_DAY, hour24);
    }

    public void rollHour24(int hour24) {
        roll(Calendar.HOUR_OF_DAY, hour24);
    }

    public void addHour(int hour) {
        add(Calendar.HOUR_OF_DAY, hour);
    }

    public int getAmPm() {
        return get(Calendar.AM_PM);
    }

    public boolean isAm() {
        return get(Calendar.AM_PM) == Calendar.AM;
    }

    public boolean isPm() {
        return get(Calendar.AM_PM) == Calendar.PM;
    }

    public void setAmPm(int ampm) {
        set(Calendar.AM_PM, ampm);
    }

    public void setAm() {
        set(Calendar.AM_PM, Calendar.AM);
    }

    public void setPm() {
        set(Calendar.AM_PM, Calendar.PM);
    }

    public void rollAmPm(int ampm) {
        roll(Calendar.AM_PM, ampm);
    }

    public void addAmPm(int ampm) {
        add(Calendar.AM_PM, ampm);
    }

    public int getMinute() {
        return get(Calendar.MINUTE);
    }

    public void setMinute(int minute) {
        set(Calendar.MINUTE, minute);
    }

    public void rollMinute(int minute) {
        roll(Calendar.MINUTE, minute);
    }

    public void addMinute(int minute) {
        add(Calendar.MINUTE, minute);
    }

    public int getSecond() {
        return get(Calendar.SECOND);
    }

    public void setSecond(int second) {
        set(Calendar.SECOND, second);
    }

    public void rollSecond(int second) {
        roll(Calendar.SECOND, second);
    }

    public void addSecond(int second) {
        add(Calendar.SECOND, second);
    }

    public int getMillisecond() {
        return get(Calendar.MILLISECOND);
    }

    public void setMillisecond(int millisecond) {
        set(Calendar.MILLISECOND, millisecond);
    }

    public void rollMillisecond(int millisecond) {
        roll(Calendar.MILLISECOND, millisecond);
    }

    public void addMillisecond(int millisecond) {
        add(Calendar.MILLISECOND, millisecond);
    }

    /*
     * public int getWeekOfYear() { return get(Calendar.WEEK_OF_YEAR); } public
     * void setWeekOfYear(int weekofyear) { set(Calendar.WEEK_OF_YEAR,
     * weekofyear); } public void rollWeekOfYear(int weekofyear) {
     * roll(Calendar.WEEK_OF_YEAR, weekofyear); }
     */
    public void addWeek(int weekofyear) {
        add(Calendar.WEEK_OF_YEAR, weekofyear);
    }

    /*
     * public int dayDifference(SaneCalendar c) { return
     * (int)((Math.abs(getTimeInMillis() -
     * c.getTimeInMillis()))/(1000.0*60.0*60.0*24.0)); }
     */

    // make these methods public coz they're useful!
    public long getTimeInMillis() {
        return super.getTimeInMillis();
    }

    public void setTimeInMillis(long millis) {
        super.setTimeInMillis(millis);
    }

    public void addMillis(int millis) {
        add(Calendar.MILLISECOND, millis);
    }

    public int compareDay(SaneCalendar c) {
        int myYear = getYear();
        int theirYear = c.getYear();
        if (myYear < theirYear)
            return -1;
        if (myYear > theirYear)
            return 1;

        int myMonth = getMonth();
        int theirMonth = c.getMonth();
        if (myMonth < theirMonth)
            return -1;
        if (myMonth > theirMonth)
            return 1;

        int myDay = getDayOfMonth();
        int theirDay = c.getDayOfMonth();
        if (myDay < theirDay)
            return -1;
        if (myDay > theirDay)
            return 1;

        return 0;
    }

    // there could be problems if we change the behaviour of before() from the
    // superclass
    public boolean before(Object other) {
        return super.before(other);
    }

    // there could be problems if we change the behaviour of after() from the
    // superclass
    public boolean after(Object other) {
        return super.after(other);
    }

    public String toString() {
        String offsetSign;
        int offsetMillis = get(Calendar.ZONE_OFFSET) + get(Calendar.DST_OFFSET);
        if (offsetMillis < 0) {
            offsetSign = "-";
            offsetMillis = -offsetMillis;
        }
        else {
            offsetSign = "+";
        }
        int offsetHours = (int) (offsetMillis / Milliseconds.ONE_HOUR);
        int offsetMinutes = (int) (offsetMillis / Milliseconds.ONE_MINUTE) % 60;
        return getYear() + "-" + pad2(getMonth()) + "-" + pad2(getDayOfMonth()) + " " + pad2(getHour24()) + ":" + pad2(getMinute()) + ":" + pad2(getSecond()) + "." + pad3(getMillisecond()) + " " + offsetSign + pad2(offsetHours) + pad2(offsetMinutes);
    }

    static private String pad2(int n) {
        if (n <= 9) {
            return "0" + n;
        }
        return "" + n;
    }

    static private String pad3(int n) {
        if (n <= 9) {
            return "00" + n;
        }
        else if (n <= 99) {
            return "0" + n;
        }
        else {
            return "" + n;
        }
    }
}
