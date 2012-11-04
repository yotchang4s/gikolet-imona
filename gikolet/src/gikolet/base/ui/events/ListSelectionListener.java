/*
 * Created on 2005/01/31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.base.ui.events;

import gikolet.base.util.EventListener;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ListSelectionListener extends EventListener {
	void selectedChange(int index);
}
