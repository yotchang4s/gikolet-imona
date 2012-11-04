/*
 * Created on 2004/02/03
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gikolet.base.ui;

/**
 * @author tetsutaro To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Rectangle {
	public int x;

	public int y;

	public int width;

	public int height;

	public Rectangle() {
		this(0, 0, 0, 0);
	}

	public Rectangle(int x, int y, int width, int height) {
		setBounds(x, y, width, height);
	}

	public void setIntersection(int x, int y, int width, int height) {
		int newX = this.x;
		int newY = this.y;

		long newWidth = newX + this.width;
		long newHeight = newY + this.height;

		if (newX < x) {
			newX = x;
		}
		if (newY < y) {
			newY = y;
		}
		
		long rightBound = x + width;
		if (newWidth > rightBound) {
			newWidth = rightBound;
		}
		long bottomBound = y + height;
		if (newHeight > bottomBound) {
			newHeight = bottomBound;
		}

		newWidth -= newX;
		newHeight -= newY;

		if (newWidth < Integer.MIN_VALUE) {
			newWidth = Integer.MIN_VALUE;
		}
		if (newHeight < Integer.MIN_VALUE) {
			newHeight = Integer.MIN_VALUE;
		}

		setBounds(newX, newY, (int) newWidth, (int) newHeight);
	}

	public void setLocation(Point location) {
		this.x = location.getX();
		this.y = location.getY();
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(Dimension size) {
		this.width = size.getWidth();
		this.height = size.getHeight();
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setBounds(Rectangle bounds) {
		this.x = bounds.x;
		this.y = bounds.y;
		this.width = bounds.width;
		this.height = bounds.height;
	}

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Point getLocation() {
		return new Point(this.x, this.y);
	}

	public Dimension getSize() {
		return new Dimension(this.width, this.height);
	}

	public Rectangle getBounds() {
		return new Rectangle(this.x, this.y, this.width, this.height);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void add(Rectangle r) {
		int x1 = Math.min(this.x, r.x);
		int x2 = Math.max(this.x + this.width, r.x + r.width);
		int y1 = Math.min(this.y, r.y);
		int y2 = Math.max(this.y + this.height, r.y + r.height);
		this.x = x1;
		this.y = y1;
		this.width = x2 - x1;
		this.height = y2 - y1;
	}

	public String toString() {
		return "x=" + this.x + "; y=" + this.y + "; width=" + this.width + "; height="
				+ this.height;
	}
}