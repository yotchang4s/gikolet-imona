/*
 * Created on 2004/04/24
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gikolet.base.util;

/**
 * @author tetsutaro
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Event {
	private Object source;

	public Event(Object source) {
		this.source = source;
	}

	public Object getSource() {
		return this.source;
	}
}