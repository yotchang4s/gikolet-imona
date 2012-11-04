/*
 * Created on 2004/01/05
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
public class Color {
	public final static Color	WHITE					= new Color(0x0ffffff);
	public final static Color	LIGHT_GRAY				= new Color(0x0c0c0c0);
	public final static Color	GRAY					= new Color(0x0808080);
	public final static Color	DARKGRAY				= new Color(0x0404040);
	public final static Color	BLACK					= new Color(0x0000000);
	public final static Color	RED						= new Color(0x0ff0000);
	public final static Color	PINK					= new Color(0x0ffafaf);
	public final static Color	ORANGE					= new Color(0x0ffc800);
	public final static Color	YELLOW					= new Color(0x0ffff00);
	public final static Color	GREEN					= new Color(0x000ff00);
	public final static Color	MAGENTA					= new Color(0x0ff00ff);
	public final static Color	CYAN					= new Color(0x000ffff);
	public final static Color	BLUE					= new Color(0x00000ff);

	public final static Color	BACK_COLOR				= new Color(0x0ECE4D8);
	public final static Color	FORE_COLOR				= new Color(0x0000000);
	public final static Color	SELECTION_BACK_COLOR	= new Color(0x0316ac5);
	public final static Color	SELECTION_FORE_COLOR	= new Color(0x0ffffff);

	private int					rgb;

	public Color(int rgb) {
		this.rgb = rgb;
	}

	public Color(int r, int g, int b) {
		this.rgb = (r << 16) + (g << 8) + (b);
	}

	public int getBlue() {
		return this.rgb & 255;
	}

	public int getGreen() {
		return (this.rgb >> 8) & 255;
	}

	public int getRed() {
		return (this.rgb >> 16) & 255;
	}

	public int getRGB() {
		return this.rgb;
	}

	public Color getReversalColor() {
		return new Color(255 - getRed(), 255 - getGreen(), 255 - getBlue());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Color) {
			if (this.rgb == ((Color) obj).rgb) {
				return true;
			}
		}
		return false;
	}
}
