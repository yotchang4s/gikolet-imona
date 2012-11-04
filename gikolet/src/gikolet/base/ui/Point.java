/*
 * Created on 2004/02/23
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gikolet.base.ui;

/**
 * @author tetsutaro
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Point {
	private int x;
	private int y;

	public Point() {
		this(0, 0);
	}

	public Point(int x, int y) {
		setLocation(x, y);
	}

	public Point getLocation() {
		return new Point(this.x, this.y);
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setLocation(int x, int y) {
		setX(x);
		setY(y);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}
}