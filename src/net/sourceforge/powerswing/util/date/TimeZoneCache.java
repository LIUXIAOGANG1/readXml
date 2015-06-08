package net.sourceforge.powerswing.util.date;

import java.util.TimeZone;

/** 
 * This class is for use by TimeStamp and LocalTime.
 * It is not public because then it would be possible for random classes to
 * modify the TimeZone objects stored in the cache causing all sorts of mayhem.
 */
class TimeZoneCache {
	static private final CacheMap cache = new CacheMap(10, new CacheMissListener() {
        public Object cacheMiss(Object id) {
            return TimeZone.getTimeZone((String) id);
        }
    });
    
    static public TimeZone get(String id) {
        return (TimeZone) cache.get(id);
    }
}
