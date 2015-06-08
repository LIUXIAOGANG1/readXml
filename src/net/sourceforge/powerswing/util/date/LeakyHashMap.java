package net.sourceforge.powerswing.util.date;

import java.util.HashMap;

/**
 * Hash map with a set maximum number of elements. Once it becomes full it
 * discards the least recently used element each time an element is added. You
 * can pass in an LHMDiscardListener to catch objects as they are discarded and
 * do something with them. Note that the listener will not be called when
 * objects are removed with remove()
 * 
 * Only calls to get() and put() update the recency status of an element.
 * 
 * This class isn't synchronized; use SynchronizedLeakyHashMap instead if you
 * need that.
 * 
 * @author Paul McClave
 */

public class LeakyHashMap {
    private int maxsize;

    private HashMap <Object, LowLevelLinkedList.Entry> hash = new HashMap <Object, LowLevelLinkedList.Entry> ();

    private LowLevelLinkedList recency = new LowLevelLinkedList();

    private DiscardListener discardListener;

    public LeakyHashMap(int maxsize) {
        this.maxsize = maxsize;
        this.discardListener = null;
    }

    public LeakyHashMap(int maxsize, DiscardListener discardListener) {
        this.maxsize = maxsize;
        this.discardListener = discardListener;
    }

    public Object put(Object key, Object value) {
        LowLevelLinkedList.Entry oldentry = hash.get(key);
        if (oldentry != null) {
            recency.removeEntry(oldentry);
        }
        KeyValue kv = new KeyValue(key, value);
        LowLevelLinkedList.Entry newentry = recency.addFirst(kv);
        hash.put(key, newentry);
        if (recency.size() > maxsize) {
            KeyValue last = (KeyValue) recency.removeLast();
            hash.remove(last.key);
            if (discardListener != null) {
                discardListener.entryDiscarded(last.key, last.value);
            }
        }

        if (oldentry == null) {
            return null;
        }
        return ((KeyValue) oldentry.get()).value;
    }

    public Object get(Object key) {
        LowLevelLinkedList.Entry entry = hash.get(key);
        if (entry == null)
            return null;

        recency.moveEntryToFront(entry);
        return ((KeyValue) entry.get()).value;
    }

    public Object remove(Object key) {
        LowLevelLinkedList.Entry entry = hash.remove(key);
        if (entry != null) {
            recency.removeEntry(entry);
            return ((KeyValue) entry.get()).value;
        }
        return null;
    }

    public boolean isEmpty() {
        return hash.isEmpty();
    }

    public void clear() {
        hash.clear();
        recency.quickAndDirtyClear();
    }

    public int size() {
        return hash.size();
    }

    public int maxSize() {
        return maxsize;
    }

    static private class KeyValue {
        public final Object key;

        public final Object value;

        public KeyValue(Object k, Object v) {
            key = k;
            value = v;
        }
    }

    public interface DiscardListener {
        public void entryDiscarded(Object key, Object value);
    }
}
