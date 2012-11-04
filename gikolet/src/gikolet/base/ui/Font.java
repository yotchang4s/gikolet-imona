/*
 * Created on 2005/02/19 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.base.ui;

import gikolet.base.Toolkit;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Font {
	public final static int	FACE_MONOSPACE		= 0x001;
	public final static int	FACE_PROPORTIONAL	= 0x002;
	public final static int	FACE_SYSTEM			= 0x004;
	public final static int	SIZE_LARGE			= 0x008;
	public final static int	SIZE_MEDIUM			= 0x010;
	public final static int	SIZE_SMALL			= 0x020;
	public final static int	SIZE_TINY			= 0x040;
	public final static int	STYLE_BOLD			= 0x080;
	public final static int	STYLE_ITALIC		= 0x100;
	public final static int	STYLE_PLAIN			= 0x200;
	public final static int	STYLE_UNDERLINED	= 0x400;

	private static Font		_defaultFont;

	private int				_face;
	private int				_style;
	private int				_size;

	protected Font(int face, int style, int size) {
		switch (face) {
			case FACE_MONOSPACE:
			case FACE_PROPORTIONAL:
			case FACE_SYSTEM:
				break;
			default:
				throw new IllegalArgumentException("Invalid face is " + face);
		}
		_face = face;

		int ss = style;
		if ((style & Font.STYLE_PLAIN) != 0) {
			if ((style & Font.STYLE_BOLD) != 0
					|| (style & Font.STYLE_ITALIC) != 0) {
				throw new IllegalArgumentException("Invalid style is " + style);
			}
			ss -= Font.STYLE_PLAIN;
		} else {
			if ((style & Font.STYLE_BOLD) != 0) {
				ss -= Font.STYLE_BOLD;
			}
			if ((style & Font.STYLE_ITALIC) != 0) {
				ss -= Font.STYLE_ITALIC;
			}
		}
		if ((style & Font.STYLE_UNDERLINED) != 0) {
			ss -= Font.STYLE_UNDERLINED;
		}
		if (ss != 0) {
			throw new IllegalArgumentException("Invalid style is " + style);
		}
		_style = style;

		switch (size) {
			case SIZE_LARGE:
			case SIZE_MEDIUM:
			case SIZE_SMALL:
			case SIZE_TINY:
				break;
			default:
				throw new IllegalArgumentException("Invalid size is " + size);
		}
		_size = size;
	}

	public static Font createFont(int face, int style, int size) {
		return Toolkit.getDisplay().createFont(face, style, size);
	}

	public static void setDefaultFont(Font font) {
		if (font == null) {
			throw new NullPointerException("font");
		}
		_defaultFont = font;
	}

	public static Font getDefaultFont() {
		return _defaultFont;
	}

	public int getFace() {
		return _face;
	}

	public int getStyle() {
		return _style;
	}

	public boolean isPlain() {
		return (_style & STYLE_PLAIN) != 0;
	}

	public boolean isBold() {
		return (_style & STYLE_BOLD) != 0;
	}

	public boolean isItalic() {
		return (_style & STYLE_ITALIC) != 0;
	}

	public boolean isUnderlined() {
		return (_style & STYLE_UNDERLINED) != 0;
	}

	public int getSize() {
		return _size;
	}

	public abstract int getBaselinePosition();

	public abstract int getHeight();

	public abstract int charWidth(char c);

	public abstract int stringWidth(String str);

	public abstract int substringWidth(String str, int offset, int lenght);
}