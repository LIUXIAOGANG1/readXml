package net.sourceforge.powerswing.util.date;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

/**
 * Represents an instant of time in UTC.
 * <p>
 * 
 * TimeStamps are immutable so you can pass them around, use them for keys in
 * HashMaps etc and not worry about them changing under you.
 * <p>
 * 
 * TimeStamps are better than Date's and GregorianCalendar's because:
 * <ul>
 * <li>TimeStamps are immutable
 * <li>because Date's timezone support is broken all its useful methods are
 * deprecated and if you use them anyway it will internally create a
 * GregorianCalendar
 * <li>GregorianCalendars are really heavyweight! Every GregorianCalendar
 * contains 2 int[]'s, 1 boolean[], 5 ints, 4 booleans and 3 longs!!
 * <li>GregorianCalendar uses 0 for January, 1 for Febraury etc which is stupid
 * </ul>
 * 
 * Note that TimeStamp currently only supports UTC. If you have a local time you
 * want to store in a TimeStamp you should store it in a Date or Calendar first
 * and pass that into TimeStamp's constructor.
 * <p>
 * 
 * @author Paul McClave
 */

public class TimeStamp implements Comparable, TimeConstants {
    private final long millis;

    public TimeStamp(long millis) {
        if (millis < 0) {
            // this saves me from checking if all the modulo arithmetic makes
            // sense for negative numbers
            millis = 0;
            throw new IllegalArgumentException("TimeStamp cannot represent dates before " + this);
        }
        this.millis = millis;
    }

    public TimeStamp() {
        this(System.currentTimeMillis());
    }

    public TimeStamp(String string) throws BadFormatException {
        this(Milliseconds.string2millis(stripUTC(string)));
    }

    private static String stripUTC(String string) {
        if (string.endsWith(" UTC")) {
            return string.substring(0, string.length() - 4);
        }
        return string;
    }

    static public final char DAYNUMBER = 20202;

    public TimeStamp(long daynum, char magic) {
        this(Milliseconds.dayNumber2millis(daynum));
        if (magic != DAYNUMBER) {
            throw new IllegalArgumentException();
        }
    }

    public TimeStamp(Calendar calendar) {
        this(calendar.getTime().getTime());
    }

    public TimeStamp(Date date) {
        this(date.getTime());
    }

    public TimeStamp(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean lenient) {
        this(Milliseconds.ymdhmsm2millis(year, month, day, hour, minute, second, millisecond, lenient));
    }

    public TimeStamp(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean ofsPlus, int ofsHours, int ofsMinutes, boolean lenient) {
        this(Milliseconds.ymdhmsm2millis(year, month, day, hour, minute, second, millisecond, lenient) + (ofsPlus ? -1 : +1) * (ofsHours * Milliseconds.ONE_HOUR + ofsMinutes * Milliseconds.ONE_MINUTE));
    }

    public TimeStamp(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean ofsPlus, int ofsHours, int ofsMinutes) {
        this(year, month, day, hour, minute, second, millisecond, ofsPlus, ofsHours, ofsMinutes, false);
    }

    public TimeStamp(int year, int month, int day, int hour, int minute, int second, boolean ofsPlus, int ofsHours, int ofsMinutes) {
        this(year, month, day, hour, minute, second, 0, ofsPlus, ofsHours, ofsMinutes, false);
    }

    public TimeStamp(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this(year, month, day, hour, minute, second, millisecond, false);
    }

    public TimeStamp(int year, int month, int day, int hour, int minute, int second, boolean lenient) {
        this(year, month, day, hour, minute, second, 0, lenient);
    }

    public TimeStamp(int year, int month, int day, int hour, int minute, int second) {
        this(year, month, day, hour, minute, second, 0, false);
    }

    public TimeStamp(int year, int month, int day, boolean lenient) {
        this(year, month, day, 0, 0, 0, 0, lenient);
    }

    public TimeStamp(int year, int month, int day) {
        this(year, month, day, 0, 0, 0, 0, false);
    }

    public Date toDate() {
        return new Date(millis);
    }

    public int compareTo(Object o) {
        TimeStamp that = (TimeStamp) o;
        if (this.millis == that.millis) {
            return 0;
        }
        else if (this.millis < that.millis) {
            return -1;
        }
        else {
            return +1;
        }
    }

    public boolean equals(Object o) {
        if (o instanceof TimeStamp) {
            TimeStamp that = (TimeStamp) o;
            return this.millis == that.millis;
        }
        return false;
    }

    public int getYear() {
        return Milliseconds.year(millis);
    }

    public int getMonth() {
        return Milliseconds.month(millis);
    }

    public int getDayOfMonth() {
        return Milliseconds.dayOfMonth(millis);
    }

    public int getDayOfWeek() {
        return Milliseconds.dayOfWeek(millis);
    }

    public int getHour12() {
        return Milliseconds.hour12(millis);
    }

    public int getHour24() {
        return Milliseconds.hour24(millis);
    }

    public int getAmPm() {
        return Milliseconds.amPm(millis);
    }

    public boolean isAm() {
        return Milliseconds.isAm(millis);
    }

    public boolean isPm() {
        return Milliseconds.isPm(millis);
    }

    public int getMinute() {
        return Milliseconds.minute(millis);
    }

    public int getSecond() {
        return Milliseconds.second(millis);
    }

    public int getMillisecond() {
        return Milliseconds.millisecond(millis);
    }

    public int compareDay(TimeStamp that) {
        long thisdayno = Milliseconds.dayNumber(this.millis);
        long thatdayno = Milliseconds.dayNumber(that.millis);
        if (thisdayno == thatdayno) {
            return 0;
        }
        else if (thisdayno < thatdayno) {
            return -1;
        }
        else {
            return +1;
        }
    }

    public long getTimeInMillis() {
        return millis;
    }

    public long getDayNumber() {
        return Milliseconds.dayNumber(millis);
    }

    public long getHourNumber() {
        return Milliseconds.hourNumber(millis);
    }

    public boolean after(Object o) {
        TimeStamp that = (TimeStamp) o;
        return this.millis > that.millis;
    }

    public boolean before(Object o) {
        TimeStamp that = (TimeStamp) o;
        return this.millis < that.millis;
    }

    public String toString(String tz) {
        return toString(TimeZoneCache.get(tz));
    }

    public String toString(TimeZone tz) {
        return new SaneCalendar(tz, millis).toString();
    }

    public String toString() {
        return Milliseconds.millis2string(millis);
    }

    public String toStringWithoutSuffix() {
        return Milliseconds.millis2string(millis);
    }

    public String toStringWithSuffix() {
        return Milliseconds.millis2string(millis) + " UTC";
    }

    public String toStringWithoutMillis() {
        return Milliseconds.millis2stringWithoutMillis(millis);
    }

    public int hashCode() {
        // copied from java.util.Date
        return (int) millis ^ (int) (millis >> 32);
    }

    public TimeStamp plusMilliseconds(long milliseconds) {
        if (milliseconds == 0)
            return this;
        return new TimeStamp(this.millis + milliseconds);
    }

    public TimeStamp plusSeconds(long seconds) {
        if (seconds == 0)
            return this;
        return new TimeStamp(this.millis + Milliseconds.ONE_SECOND * seconds);
    }

    public TimeStamp plusMinutes(long minutes) {
        if (minutes == 0)
            return this;
        return new TimeStamp(this.millis + Milliseconds.ONE_MINUTE * minutes);
    }

    public TimeStamp plusHours(long hours) {
        if (hours == 0)
            return this;
        return new TimeStamp(this.millis + Milliseconds.ONE_HOUR * hours);
    }

    public TimeStamp plusDays(long days) {
        if (days == 0)
            return this;
        return new TimeStamp(this.millis + Milliseconds.ONE_DAY * days);
    }

    public TimeStamp plusWeeks(long weeks) {
        if (weeks == 0)
            return this;
        return new TimeStamp(this.millis + Milliseconds.ONE_WEEK * weeks);
    }

    public LocalTime getLocalTime(String timezone) {
        return getLocalTime(TimeZoneCache.get(timezone));
    }

    public LocalTime getLocalTime(TimeZone timezone) {
        SaneCalendar cal = new SaneCalendar(timezone);
        cal.setTimeInMillis(millis);

        int ye = cal.getYear();
        int mo = cal.getMonth();
        int da = cal.getDayOfMonth();
        int ho = cal.getHour24();
        int mi = cal.getMinute();
        int se = cal.getSecond();
        int ms = cal.getMillisecond();

        if (!timezone.useDaylightTime()) {
            return new LocalTime(ye, mo, da, ho, mi, se, ms);
        }

        cal = new SaneCalendar(timezone, ye, mo, da, ho, mi, se, ms);
        if (cal.getTimeInMillis() == millis) {
            return new LocalTime(ye, mo, da, ho, mi, se, ms);
        }

        // the only time millis doesn't round trip correctly via Calendar
        // is when we're in that hour that occurs twice because the clock is put
        // back

        // since a LocalTime can't represent this time we return the beginning
        // of the following
        // hour. since it is the first hour of the two that can't be represented
        // we'll end up
        // with the whole of the first hour scrunched up into the first second
        // on the
        // second hour of the two

        // this code works in half-hour multiples because some countries have
        // half an
        // hour of daylight saving time apparently

        final long THIRTYMINS = Milliseconds.ONE_MINUTE * 30;
        long m = (millis / THIRTYMINS) * THIRTYMINS;

        for (int i = 1; i <= 4; i++) {
            // go to the next half hour
            m += THIRTYMINS;

            // see if it round trips
            cal = new SaneCalendar(timezone);
            cal.setTimeInMillis(m);

            ye = cal.getYear();
            mo = cal.getMonth();
            da = cal.getDayOfMonth();
            ho = cal.getHour24();
            mi = cal.getMinute();
            se = cal.getSecond();
            ms = cal.getMillisecond();

            cal = new SaneCalendar(timezone, ye, mo, da, ho, mi, se, ms);
            if (cal.getTimeInMillis() == m) {
                return new LocalTime(ye, mo, da, ho, mi, se, ms);
            }
        }

        // uh oh
        // try doing it per minute
        // yes, this is a tad inefficient
        final long ONEMIN = Milliseconds.ONE_MINUTE;
        m = (millis / ONEMIN) * ONEMIN;

        for (int i = 1; i <= 24 * 60 * 60; i++) {
            // go to the next minute
            m += ONEMIN;

            // see if it round trips
            cal = new SaneCalendar(timezone);
            cal.setTimeInMillis(m);

            ye = cal.getYear();
            mo = cal.getMonth();
            da = cal.getDayOfMonth();
            ho = cal.getHour24();
            mi = cal.getMinute();
            se = cal.getSecond();
            ms = cal.getMillisecond();

            cal = new SaneCalendar(timezone, ye, mo, da, ho, mi, se, ms);
            if (cal.getTimeInMillis() == m) {
                return new LocalTime(ye, mo, da, ho, mi, se, ms);
            }
        }

        // give up
        throw new Error("Unsupported time zone: " + timezone.getID());
    }

    /**
     * Returns the current timestamp if at midnight UTC, otherwise returns the
     * previous midnight.
     */
    public TimeStamp roundToPrevMidnight() {
        if (Milliseconds.atMidnight(millis)) {
            return this;
        }
        return new TimeStamp(Milliseconds.roundToPrevMidnight(millis));
    }

    /**
     * Returns the current timestamp if at midnight UTC, otherwise returns the
     * next midnight.
     */
    public TimeStamp roundToNextMidnight() {
        if (Milliseconds.atMidnight(millis)) {
            return this;
        }
        return new TimeStamp(Milliseconds.nextMidnight(millis));
    }

    /*
     * static public void main(String[] args) { MCal mc = new MCal(2002, 1, 1,
     * 0, 0, 0, 0); System.out.println(mc);
     * System.out.println(mc.getTimeInMillis()); System.out.println(new
     * Cal(mc.getTimeInMillis()).getEDayNumber());
     * System.out.println(calcZDayNumber(2002, 1, 1, false)); MCal mc2 = new
     * MCal(2103, 2, 33, 0, 0, 0, 0); Cal c = new Cal(2103, 2, 29, 0, 0, 0, 0);
     * System.out.println(c.getYear()); System.out.println(c.getMonth());
     * System.out.println(c.getDayOfMonth());
     * System.out.println(c.getTimeInMillis());
     * System.out.println(mc2.getTimeInMillis());
     * System.out.println(c.getDayOfWeek()==Cal.SATURDAY);
     * System.out.println(mc2.getDayOfWeek()==Cal.SATURDAY);
     * 
     * for (int i=1; i <=7; i++) { mc2.setDayOfWeek(i); System.out.println(mc2);
     * System.out.println(""+i+" "+(mc2.getDayOfWeek()==i)); } }
     */
}
