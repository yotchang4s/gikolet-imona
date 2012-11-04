/*
 * Created on 2005/02/20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.midp;

import gikolet.base.ui.Font;

/**
 * @author tetsutaro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MIDPFont extends Font {
	private javax.microedition.lcdui.Font	_font;

	static Font getFont(javax.microedition.lcdui.Font midpFont) {
		int face = 0;
		int style = 0;
		int size = 0;

		if (midpFont.getFace() == javax.microedition.lcdui.Font.FACE_MONOSPACE) {
			face = Font.FACE_MONOSPACE;
		} else if (midpFont.getFace() == javax.microedition.lcdui.Font.FACE_PROPORTIONAL) {
			face = Font.FACE_PROPORTIONAL;
		} else if (midpFont.getFace() == javax.microedition.lcdui.Font.FACE_SYSTEM) {
			face = Font.FACE_SYSTEM;
		}

		if (midpFont.isPlain()) {
			style |= Font.STYLE_PLAIN;
		} else {
			if (midpFont.isBold()) {
				style |= Font.STYLE_BOLD;
			}
			if (midpFont.isItalic()) {
				style |= Font.STYLE_ITALIC;
			}
		}
		if (midpFont.isUnderlined()) {
			style |= Font.STYLE_UNDERLINED;
		}

		if (midpFont.getSize() == javax.microedition.lcdui.Font.SIZE_LARGE) {
			size = Font.SIZE_LARGE;
		} else if (midpFont.getSize() == javax.microedition.lcdui.Font.SIZE_MEDIUM) {
			size = Font.SIZE_MEDIUM;
		} else if (midpFont.getSize() == javax.microedition.lcdui.Font.SIZE_SMALL) {
			size = Font.SIZE_SMALL;
		}
		return new MIDPFont(face, style, size);
	}

	MIDPFont(int face, int style, int size) {
		super(face, style, size);

		int midpFace;
		int midpStyle;
		int midpSize;

		switch (face) {
			case Font.FACE_MONOSPACE:
				midpFace = javax.microedition.lcdui.Font.FACE_MONOSPACE;
				break;
			case Font.FACE_PROPORTIONAL:
				midpFace = javax.microedition.lcdui.Font.FACE_PROPORTIONAL;
				break;
			case Font.FACE_SYSTEM:
				midpFace = javax.microedition.lcdui.Font.FACE_SYSTEM;
				break;
			default:
				throw new IllegalArgumentException("Invalid face(MIDP) is "
						+ face);
		}

		midpStyle = 0;
		if ((style & Font.STYLE_PLAIN) != 0) {
			if ((style & Font.STYLE_BOLD) != 0
					|| (style & Font.STYLE_ITALIC) != 0) {
				throw new IllegalArgumentException("Invalid style(MIDP) is "
						+ style);
			}
			style -= Font.STYLE_PLAIN;
			midpStyle |= javax.microedition.lcdui.Font.STYLE_PLAIN;
		} else {
			if ((style & Font.STYLE_BOLD) != 0) {
				style -= Font.STYLE_BOLD;
				midpStyle |= javax.microedition.lcdui.Font.STYLE_BOLD;
			}
			if ((style & Font.STYLE_ITALIC) != 0) {
				style -= Font.STYLE_ITALIC;
				midpStyle |= javax.microedition.lcdui.Font.STYLE_ITALIC;
			}
		}
		if ((style & Font.STYLE_UNDERLINED) != 0) {
			style -= Font.STYLE_UNDERLINED;
			midpStyle |= javax.microedition.lcdui.Font.STYLE_UNDERLINED;
		}
		if (style != 0) {
			throw new IllegalArgumentException("Invalid style(MIDP) is "
					+ style);
		}

		switch (size) {
			case Font.SIZE_LARGE:
				midpSize = javax.microedition.lcdui.Font.SIZE_LARGE;
				break;
			case Font.SIZE_MEDIUM:
				midpSize = javax.microedition.lcdui.Font.SIZE_MEDIUM;
				break;
			case Font.SIZE_SMALL:
			case Font.SIZE_TINY:
				midpSize = javax.microedition.lcdui.Font.SIZE_SMALL;
				break;
			default:
				throw new IllegalArgumentException("Invalid size(MIDP) is "
						+ size);
		}

		_font = javax.microedition.lcdui.Font.getFont(midpFace, midpStyle,
				midpSize);
	}

	public javax.microedition.lcdui.Font getMIDPFont() {
		return _font;
	}

	public int getBaselinePosition() {
		return _font.getBaselinePosition();
	}

	public int getHeight() {
		return _font.getHeight();
	}

	public int stringWidth(String str) {
		return _font.stringWidth(str);
	}

	public int charWidth(char c) {
		return _font.charWidth(c);
	}

	public int substringWidth(String str, int offset, int lenght) {
		return _font.substringWidth(str, offset, lenght);
	}
}