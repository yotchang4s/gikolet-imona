/*
 * Created on 2005/01/23
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
public class ResList {
	private Hashtable	_reses;

	public ResList() {
		_reses = new Hashtable();
	}

	public synchronized void set(Res res) {
		_reses.put(new Integer(res.getNo()), res);
	}

	public synchronized void setRange(ResList reses) {
		int[] keys = reses.getKeys();
		for (int i = 0; i < keys.length; i++) {
			set(reses.get(keys[i]));
		}
	}

	public synchronized Res get(int no) {
		return (Res) _reses.get(new Integer(no));
	}

	public synchronized int[] getKeys() {
		int[] wi = new int[_reses.size()];

		int i = 0;
		Enumeration e = _reses.keys();
		while (e.hasMoreElements()) {
			Integer oi = (Integer) e.nextElement();
			wi[i] = oi.intValue();
			i++;
		}
		return wi;
	}

	public synchronized void clear() {
		_reses.clear();
	}

	public synchronized int getStartIndex(int index) {
		if (this._reses.containsKey(new Integer(index))) {
			int i = index - 1;
			while (true) {
				if (!this._reses.containsKey(new Integer(i))) {
					return i + 1;
				}
				i--;
			}
		}
		return -1;
	}

	public synchronized int getEndIndex(int index) {
		if (this._reses.containsKey(new Integer(index))) {
			int i = index + 1;
			while (true) {
				if (!this._reses.containsKey(new Integer(i))) {
					return i - 1;
				}
				i++;
			}
		}
		return -1;
	}

	public synchronized boolean containsIndex(int index) {
		return _reses.containsKey(new Integer(index));
	}
}