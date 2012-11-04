/*
 * Created on 2004/04/20
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gikolet.base.ui.events;

import gikolet.base.util.EventListener;

/**
 * @author tetsutaro
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface ApplicationEventListener extends EventListener {
	public void startApplication();
	public void exitApplication();
	public void pauseApplication();
}