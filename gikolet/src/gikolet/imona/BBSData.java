/*
 * Created on 2005/03/31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.imona;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BBSData {
	public final static int	BOARD			= 0;
	public final static int	THREAD_HEADER	= 1;
	public final static int	RES				= 2;

	private int				_type;

	protected BBSData(int type) {
		_type = type;
	}

	public int getType() {
		return _type;
	}
}