/*
 * Created on 2005/05/25 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public interface Config {
	final int	RES_SHOW_METHOD_2CH					= 0;
	final int	RES_SHOW_METHOD_BODY_PRIORITY		= 1;

	final int	RES_MOVE_METHOD_LEFT_RIGHT			= 0;
	final int	RES_MOVE_METHOD_UP_DONW_LEFT_RIGHT	= 1;

	String getServer();

	int getThreadHeaderReadSize();

	int getThreadTabTextLength();

	boolean isTopTabBarShow();

	int getResShowMethod();

	int getResReadSize();
	
	int getResMoveMethod();

	int getNewResReadSize();

	int getResScrollWeight();
}
