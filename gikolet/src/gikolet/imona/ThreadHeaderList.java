/*
 * Created on 2005/01/21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.imona;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ThreadHeaderList {
	private Hashtable	_threadHeaders;

	public ThreadHeaderList() {
		_threadHeaders = new Hashtable();
	}

	public synchronized int indexOf(int threadNo) {
		return indexOf(threadNo, 0);
	}

	public synchronized int indexOf(int threadNo, int index) {
		Enumeration e = _threadHeaders.elements();
		int i = 0;
		while (e.hasMoreElements()) {
			ThreadHeader th = (ThreadHeader) e.nextElement();
			if (index <= i) {
				if (th.getThreadNo() == threadNo) {
					return i;
				}
			}
			i++;
		}
		return -1;
	}

	public synchronized ThreadHeader getThreadHeader(int index) {
		return (ThreadHeader) _threadHeaders.get(new Integer(index));
	}

	public synchronized void set(ThreadHeader threadHeader, int index) {
		if (index < 1) {
			throw new IllegalArgumentException("index");
		}
		_threadHeaders.put(new Integer(index), threadHeader);
	}

	public synchronized void setRange(ThreadHeaderList threadHeaders) {
		int[] keys = threadHeaders.getKeys();
		for (int i = 0; i < keys.length; i++) {
			set(threadHeaders.getThreadHeader(keys[i]), keys[i]);
		}
	}

	public synchronized int getStartIndex(int index) {
		if (_threadHeaders.containsKey(new Integer(index))) {
			int i = index - 1;
			while (true) {
				if (!_threadHeaders.containsKey(new Integer(i))) {
					return i + 1;
				}
				i--;
			}
		}
		return -1;
	}

	public synchronized int getEndIndex(int index) {
		if (_threadHeaders.containsKey(new Integer(index))) {
			int i = index + 1;
			while (true) {
				if (!_threadHeaders.containsKey(new Integer(i))) {
					return i - 1;
				}
				i++;
			}
		}
		return -1;
	}

	public synchronized boolean isEmpty() {
		return _threadHeaders.isEmpty();
	}

	public synchronized int[] getKeys() {
		int[] wi = new int[_threadHeaders.size()];

		int i = 0;
		Enumeration e = _threadHeaders.keys();
		while (e.hasMoreElements()) {
			Integer oi = (Integer) e.nextElement();
			wi[i] = oi.intValue();
			i++;
		}
		return wi;
	}

	public synchronized void removeAt(int index) {
		_threadHeaders.remove(new Integer(index));
	}

	public synchronized boolean containsIndex(int index) {
		return _threadHeaders.containsKey(new Integer(index));
	}

	public synchronized boolean contains(ThreadHeader header) {
		return _threadHeaders.contains(header);
	}

	public synchronized void clear() {
		_threadHeaders.clear();
	}
}