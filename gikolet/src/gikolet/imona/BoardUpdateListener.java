/*
 * Created on 2005/02/03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.imona;

import gikolet.base.util.EventListener;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface BoardUpdateListener extends EventListener {
	void boardAdded(String category, Board newBoard);
	
	void boardChanged(String category,Board oldBoard, Board newBoard);
}