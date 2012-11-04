/*
 * Created on 2005/02/03
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
public class Range {
	public int	start;
	public int	to;

	public Range() {
		this(0, 0);
	}

	public Range(int start, int to) {
		this.start = start;
		this.to = to;
	}
}