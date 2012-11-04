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
public class Dimension {
	private int width;
	private int height;

	public Dimension() {
		this(0, 0);
	}

	public Dimension(Dimension d) {
		this(d.getWidth(), d.getHeight());
	}

	public Dimension(int width, int height) {
		setSize(width, height);
	}

	public void setSize(Dimension d) {
		setSize(d.getWidth(), d.getHeight());
	}

	public void setWidth(int width) {
		setSize(width, getHeight());
	}

	public void setHeight(int height) {
		setSize(getWidth(), height);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Dimension getSize() {
		return new Dimension(this);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public String toString() {
		return "width=" + this.width + ": height=" + this.height;
	}
}