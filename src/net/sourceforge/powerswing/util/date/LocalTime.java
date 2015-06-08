package net.sourceforge.powerswing.util.date;

import java.util.TimeZone;

public class LocalTime implements Comparable, TimeConstants {
    private final long millis;

    public LocalTime(long millisSinceLocalEpoch) {
        if (millisSinceLocalEpoch < 0) {
            // this saves me from checking if all the modulo arithmetic makes
            // sense for negative numbers
            millis = 0;
            throw new IllegalArgumentException("LocalTime cannot represent dates before " + this);
        }
        this.millis = millisSinceLocalEpoch;
    }

    public LocalTime() {
        this(new TimeStamp().getLocalTime(TimeZone.getDefault()).getMillisSinceLocalEpoch());
    }

    public LocalTime(String string) throws BadFormatException {
        this(Milliseconds.string2millis(string));
    }

    static public final char DAYNUMBER = 20202;

    public LocalTime(long daynum, char magic) {
        this(Milliseconds.dayNumber2millis(daynum));
        if (magic != DAYNUMBER) {
            throw new IllegalArgumentException();
        }
    }

    public LocalTime(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean lenient) {
        this(Milliseconds.ymdhmsm2millis(year, month, day, hour, minute, second, millisecond, lenient));
    }

    public LocalTime(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this(year, month, day, hour, minute, second, millisecond, false);
    }

    public LocalTime(int year, int month, int day, int hour, int minute, int second, boolean lenient) {
        this(year, month, day, hour, minute, second, 0, lenient);
    }

    public LocalTime(int year, int month, int day, int hour, int minute, int second) {
        this(year, month, day, hour, minute, second, 0, false);
    }

    public LocalTime(int year, int month, int day, boolean lenient) {
        this(year, month, day, 0, 0, 0, 0, lenient);
    }

    public LocalTime(int year, int month, int day) {
        this(year, month, day, 0, 0, 0, 0, false);
    }

    public int compareTo(Object o) {
        LocalTime that = (LocalTime) o;
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
        if (o instanceof LocalTime) {
            LocalTime that = (LocalTime) o;
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

    public int compareDay(LocalTime that) {
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

    public long getMillisSinceLocalEpoch() {
        return millis;
    }

    public long getDayNumber() {
        return Milliseconds.dayNumber(millis);
    }

    public long getHourNumber() {
        return Milliseconds.hourNumber(millis);
    }

    public boolean after(Object o) {
        LocalTime that = (LocalTime) o;
        return this.millis > that.millis;
    }

    public boolean before(Object o) {
        LocalTime that = (LocalTime) o;
        return this.millis < that.millis;
    }

    public String toString() {
        return Milliseconds.millis2string(millis);
    }

    public String toStringWithoutMillis() {
        return Milliseconds.millis2stringWithoutMillis(millis);
    }

    public int hashCode() {
        // copied from java.util.Date
        return (int) millis ^ (int) (millis >> 32);
    }

    public LocalTime plusMilliseconds(long milliseconds) {
        return new LocalTime(this.millis + milliseconds);
    }

    public LocalTime plusSeconds(long seconds) {
        return new LocalTime(this.millis + seconds * Milliseconds.ONE_SECOND);
    }

    public LocalTime plusMinutes(long minutes) {
        return new LocalTime(this.millis + minutes * Milliseconds.ONE_MINUTE);
    }

    public LocalTime plusHours(long hours) {
        return new LocalTime(this.millis + hours * Milliseconds.ONE_HOUR);
    }

    public LocalTime plusDays(long days) {
        return new LocalTime(this.millis + days * Milliseconds.ONE_DAY);
    }

    public LocalTime plusWeeks(long weeks) {
        return new LocalTime(this.millis + weeks * Milliseconds.ONE_WEEK);
    }

    public TimeStamp getTimeStamp(String timezone) {
        return getTimeStamp(TimeZoneCache.get(timezone));
    }

    public TimeStamp getTimeStamp(TimeZone timezone) {
        SaneCalendar cal = new SaneCalendar(timezone, getYear(), getMonth(), getDayOfMonth(), getHour24(), getMinute(), getSecond(), getMillisecond());
        if (!timezone.useDaylightTime() || (getHour24() == cal.getHour24() && getMinute() == cal.getMinute())) {
            return new TimeStamp(cal.getTimeInMillis());
        }

        // the only time hours and minutes don't round trip correctly via
        // Calendar
        // is when we're in that hour that doesn't exist because the clock is
        // put forward

        // since this entire hour takes places the zero seconds between the
        // previous and
        // following hour we want to return the beginning of the following hour

        // this code works in half-hour multiples because some countries have
        // half an
        // hour of daylight saving time apparently

        final long THIRTYMINS = Milliseconds.ONE_MINUTE * 30;
        long m = (millis / THIRTYMINS) * THIRTYMINS;

        for (int i = 1; i <= 4; i++) {
            // go to the next half hour
            m += THIRTYMINS;
            // see if it round trips
            cal = new SaneCalendar(timezone, Milliseconds.year(m), Milliseconds.month(m), Milliseconds.dayOfMonth(m), Milliseconds.hour24(m), Milliseconds.minute(m), Milliseconds.second(m), Milliseconds.millisecond(m));
            if (Milliseconds.hour24(m) == cal.getHour24() && Milliseconds.minute(m) == cal.getMinute()) {
                return new TimeStamp(cal.getTimeInMillis());
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
            cal = new SaneCalendar(timezone, Milliseconds.year(m), Milliseconds.month(m), Milliseconds.dayOfMonth(m), Milliseconds.hour24(m), Milliseconds.minute(m), Milliseconds.second(m), Milliseconds.millisecond(m));
            if (Milliseconds.hour24(m) == cal.getHour24() && Milliseconds.minute(m) == cal.getMinute()) {
                return new TimeStamp(cal.getTimeInMillis());
            }
        }

        // give up
        throw new Error("Unsupported time zone: " + timezone.getID());
    }

    /**
     * Returns the current timestamp if at midnight, otherwise returns the
     * previous midnight.
     */
    public LocalTime roundToPrevMidnight() {
        if (Milliseconds.atMidnight(millis)) {
            return this;
        }
        return new LocalTime(Milliseconds.roundToPrevMidnight(millis));
    }

    /**
     * Returns the current timestamp if at midnight, otherwise returns the next
     * midnight.
     */
    public LocalTime roundToNextMidnight() {
        if (Milliseconds.atMidnight(millis)) {
            return this;
        }
        return new LocalTime(Milliseconds.nextMidnight(millis));
    }
}
