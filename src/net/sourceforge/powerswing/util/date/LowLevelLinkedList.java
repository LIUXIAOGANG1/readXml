package net.sourceforge.powerswing.util.date;

import java.util.NoSuchElementException;

/**
 * A linked list which exposes the nodes which make it up, allowing linear time 
 * element deletion from the middle of the list. This does not implement every method
 * that java.util.LinkedList provides so if there's something missing that you need let
 * me know and I'll add it.
 * 
 * The node class is call Entry for consistancy with java.util.LinkedList
 * 
 * Entrys that are passed as method arguments are checked that they actually belong to
 * the linked list so this class should be fool-proof. If you try to do this an
 * IllegalArgumentException is thrown.
 *
 * @author Paul McClave
 */

public class LowLevelLinkedList {
    private Entry header = new Entry(this, null, null, null);
    private int size = 0;

    public LowLevelLinkedList() {
        header.next = header.previous = header;
    }

    public Entry addFirst(Object o) {
        return addBefore(o, header.next);
    }

    public Entry addLast(Object o) {
		return addBefore(o, header);
    }

	/**
	 * @throws NoSuchElementException
	 */
    public Object getFirst() {
        if (size==0) {
            throw new NoSuchElementException();
        }
        return header.next.element;
    }

	/**
	 * @throws NoSuchElementException
	 */
    public Entry getFirstEntry() {
        if (size==0) {
            throw new NoSuchElementException();
        }
        return header.next;
    }

	/**
	 * @throws NoSuchElementException
	 */
    public Object getLast() {
        if (size==0) {
            throw new NoSuchElementException();
        }
        return header.previous.element;
    }

	/**
	 * @throws NoSuchElementException
	 */
    public Entry getLastEntry() {
        if (size==0) {
            throw new NoSuchElementException();
        }
        return header.previous;
    }

    public void moveEntryToFront(Entry e) {
        if (e.list != this) {
            throw new IllegalArgumentException();
        }
        e.previous.next = e.next;
        e.next.previous = e.previous;

        e.previous = header.next.previous;
        e.next = header.next;
        e.previous.next = e;
        e.next.previous = e;
    }

    public void moveEntryToBack(Entry e) {
        if (e.list != this) {
            throw new IllegalArgumentException();
        }
        e.previous.next = e.next;
        e.next.previous = e.previous;
        
        e.previous = header.previous;
        e.next = header;
        e.previous.next = e;
        e.next.previous = e;
    }

	/**
	 * @throws NoSuchElementException
	 */
    public void removeEntry(Entry e) {
        if (e == header) {
            throw new NoSuchElementException();
        }
        if (e.list != this) {
            throw new IllegalArgumentException();
        }

        e.previous.next = e.next;
        e.next.previous = e.previous;
        e.list = null;
        size--;
    }

    public Object removeFirst() {
    	Object first = header.next.element;
    	removeEntry(header.next);
    	return first;
    }

    public Object removeLast() {
        Object last = header.previous.element;
        removeEntry(header.previous);
        return last;
    }

    public int size() {
        return size;
    }

	/**
	 * Clears the list. Because all entries in the list are invalidated
	 * this takes time proportional to the length of the list.
	 * 
	 * Use quickAndDirtyClear() if you don't like this.
	 */
	public void clear() {
	    // need to invalidate all entries!
        for (Entry e = header.next; e != header; e = e.next) {
            e.list = null;
        }
        quickAndDirtyClear();
	}
	
	/**
	 * Clears the list without invalidating all entries in the list,
	 * which can be done in constant time. You should only use this
	 * if you are wrapping the list in an object which can guarantee
	 * that invalidated entries won't get reused.
	 * 
	 * Otherwise play it safe and use clear().
	 */
	public void quickAndDirtyClear() {
        header.next = header.previous = header;
		size = 0;
	}

    private Entry addBefore(Object o, Entry e) {
        Entry newEntry = new Entry(this, o, e, e.previous);
        newEntry.previous.next = newEntry;
        newEntry.next.previous = newEntry;
        size++;
        return newEntry;
    }


    static public class Entry {
		private LowLevelLinkedList list;
        private Object element;
        private Entry next;
        private Entry previous;

        private Entry(LowLevelLinkedList list, Object element, Entry next, Entry previous) {
            this.list = list;
            this.element = element;
            this.next = next;
            this.previous = previous;
        }

        public Object get() {
            return element;
        }
        
        public Entry nextEntry() {
            if (list == null || next == list.header) {
	            return null;
            }
            return next;
        }
        
        public Entry prevEntry() {
            if (list == null || previous == list.header) {
	            return null;
            }
            return previous;
        }
    }
}
