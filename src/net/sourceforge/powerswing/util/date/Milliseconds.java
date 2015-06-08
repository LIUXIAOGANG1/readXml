package net.sourceforge.powerswing.util.date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Milliseconds implements TimeConstants {
    static public final long ONE_MILLI  = 1;
    static public final long ONE_SECOND = 1000 * ONE_MILLI;
    static public final long ONE_MINUTE  =  60 * ONE_SECOND;
    static public final long ONE_HOUR    =  60 * ONE_MINUTE;
    static public final long ONE_HALFDAY =  12 * ONE_HOUR;
    static public final long ONE_DAY     =  24 * ONE_HOUR;
    static public final long ONE_WEEK    =   7 * ONE_DAY;
  
	static public long ymdhmsm2millis(int year, int month, int day, int hour, int minute, int second, int millisecond, boolean lenient) {
		return ymd2dayNumber(year, month, day, lenient)*ONE_DAY + hour*ONE_HOUR + minute*ONE_MINUTE + second*ONE_SECOND + millisecond;
	}

	static private long ymd2dayNumber(int y, int m, int d, boolean lenient) {
	    return ymd2weirdDayNumber(y, m, d, lenient) - 719468;
	}

    /* 
     * Big complicated date calculation algorithm taken from
     * http://alcor.concordia.ca/~gpkatch/gdate-algorithm.html
     */
	static private long ymd2weirdDayNumber(int year, int month, int day, boolean lenient) {
		long daynum;
		{
            long m = (month + 9) % 12;
            long y = year - m/10;
	        daynum = 365*y + y/4 - y/100 + y/400 + (m*306 + 5)/10 + (day - 1);
		}
        if (!lenient) {
            long y = (10000*daynum + 14780)/3652425;
            long ddd = daynum - (y*365 + y/4 - y/100 + y/400);
            if (ddd < 0) {
            	y--;
                ddd = daynum - (y*365 + y/4 - y/100 + y/400);
            }
            long mi = (52 + 100*ddd)/3060;
            if (year != (y + (mi + 2)/12) || month != ((mi + 2)%12 + 1) || day != (ddd - (mi*306 + 5)/10 + 1)) {
                throw new IllegalArgumentException();
            }
        }
        return daynum;
	}

	static public long dayNumber2millis(long daynum) {
	    return daynum*ONE_DAY;
	}

  	static public long dayNumber(long millis) {
	    return millis/ONE_DAY;
	}
	
  	static public long hourNumber(long millis) {
	    return millis/ONE_HOUR;
	}
	
	static private long weirdDayNumber(long millis) {
	    return dayNumber(millis) + 719468;
	}
	
	static public int year(long millis) {
	    long d = weirdDayNumber(millis);
        long y = (10000*d + 14780)/3652425;
        long ddd = d - (y*365 + y/4 - y/100 + y/400);
        if (ddd < 0) {
        	y--;
            ddd = d - (y*365 + y/4 - y/100 + y/400);
        }
        long mi = (52 + 100*ddd)/3060;
        return (int) (y + (mi + 2)/12);
	}

	static public int dayOfMonth(long millis) {
	    long d = weirdDayNumber(millis);
        long y = (10000*d + 14780)/3652425;
        long ddd = d - (y*365 + y/4 - y/100 + y/400);
        if (ddd < 0) {
        	y--;
            ddd = d - (y*365 + y/4 - y/100 + y/400);
        }
        long mi = (52 + 100*ddd)/3060;
        return (int) (ddd - (mi*306 + 5)/10 + 1);
	}

	static public int month(long millis) {
	    long d = weirdDayNumber(millis);
        long y = (10000*d + 14780)/3652425;
        long ddd = d - (y*365 + y/4 - y/100 + y/400);
        if (ddd < 0) {
        	y--;
            ddd = d - (y*365 + y/4 - y/100 + y/400);
        }
        long mi = (52 + 100*ddd)/3060;
        return (int) ((mi + 2)%12 + 1);
	}	    
	
	static public int dayOfWeek(long millis) {
	    return (int) (((millis/ONE_DAY)+3)%7)+1;
	}
	
	static public int hour12(long millis) {
	    return (int) ((millis/ONE_HOUR)%12);
	}

	static public int hour24(long millis) {
	    return (int) ((millis/ONE_HOUR)%24);
	}

	static public int amPm(long millis) {
	    return (int) ((millis/ONE_HALFDAY)%2);
	}

	static public boolean isAm(long millis) {
	    return ((millis/ONE_HALFDAY)%2) == 0;
	}

	static public boolean isPm(long millis) {
	    return ((millis/ONE_HALFDAY)%2) == 1;
	}

	static public int minute(long millis) {
	    return (int) ((millis/ONE_MINUTE)%60);
	}

	static public int second(long millis) {
	    return (int) ((millis/ONE_SECOND)%60);
	}

	static public int millisecond(long millis) {
	    return (int) (millis % 1000);
	}

	static public boolean atMidnight(long millis) {
	    return (millis%ONE_DAY) == 0;
	}
	
	static public long roundToPrevMidnight(long millis) {
	    return (millis/ONE_DAY)*ONE_DAY;
	}

	static public long nextMidnight(long millis) {
	    return ((millis/ONE_DAY)*ONE_DAY)+ONE_DAY;
	}

//    /*
//     * This version is a lot more efficient and should be used once we switch to Java 1.4
//     */
//
//    static private Pattern re = Pattern.compile("^(\\d\\d\\d\\d)-(\\d\\d?)-(\\d\\d?) (\\d\\d?):(\\d\\d):(\\d\\d)(\\.(\\d\\d\\d))$");
//
//    static public long string2millis(String string, boolean lenient) throws BadFormatException {
//        Matcher m = re.matcher(string);
//        if (!m.matches()) {
//            throw new BadFormatException();
//        }
//
//        int year, month, day, hour, minute, second, millisecond;
//        try {
//            year = Integer.parseInt(m.group(1));
//            month = Integer.parseInt(m.group(2));
//            day = Integer.parseInt(m.group(3));
//            hour = Integer.parseInt(m.group(4));
//            minute = Integer.parseInt(m.group(5));
//            second = Integer.parseInt(m.group(6));
//            if (m.group(7) != null) {
//                millisecond = Integer.parseInt(m.group(8));
//            }
//            else {
//                millisecond = 0;
//            }
//        }
//        catch (NumberFormatException e) {
//            throw new BadFormatException();
//        }
//
//        return Milliseconds.ymdhmsm2millis(year, month, day, hour, minute, second, millisecond, lenient);
//    }

	private static Pattern re = Pattern.compile("^(\\d\\d\\d\\d)-(\\d\\d?)-(\\d\\d?) (\\d\\d?):(\\d\\d):(\\d\\d)(\\.(\\d\\d\\d))?$");
	
    static public long string2millis(String string) throws BadFormatException {
        Matcher m = re.matcher(string);
        if (!m.matches()){
            throw new BadFormatException();
        }

        int year, month, day, hour, minute, second, millisecond;
        try {
            year = Integer.parseInt(m.group(1));
            month = Integer.parseInt(m.group(2));
            day = Integer.parseInt(m.group(3));
            hour = Integer.parseInt(m.group(4));
            minute = Integer.parseInt(m.group(5));
            second = Integer.parseInt(m.group(6));
            if (m.group(7) != null) {
                millisecond = Integer.parseInt(m.group(8));
            }
            else {
                millisecond = 0;
            }
        }
        catch (NumberFormatException e) {
            throw new BadFormatException();
        }

        try {
            return Milliseconds.ymdhmsm2millis(year, month, day, hour, minute, second, millisecond, false);
        }
        catch (IllegalArgumentException e) {
            throw new BadFormatException();
        }
    }

    static public String millis2string(long millis) {
        return year(millis) + "-" + pad2(month(millis)) + "-" + pad2(dayOfMonth(millis)) + " " + pad2(hour24(millis)) + ":" + pad2(minute(millis)) + ":" + pad2(second(millis)) + "." + pad3(millisecond(millis));
    }

    static public String millis2stringWithoutMillis(long millis) {
        int m = millisecond(millis);
        if (m == 0) {
            return year(millis) + "-" + pad2(month(millis)) + "-" + pad2(dayOfMonth(millis)) + " " + pad2(hour24(millis)) + ":" + pad2(minute(millis)) + ":" + pad2(second(millis));
        }
        return year(millis) + "-" + pad2(month(millis)) + "-" + pad2(dayOfMonth(millis)) + " " + pad2(hour24(millis)) + ":" + pad2(minute(millis)) + ":" + pad2(second(millis)) + "." + pad3(millisecond(millis));
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
