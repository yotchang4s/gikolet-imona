/*
 * Created on 2005/02/20 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.doja;

import gikolet.base.ui.Font;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DoJaFont extends Font {
	private com.nttdocomo.ui.Font	_font;

	protected DoJaFont(int face, int style, int size) {
		super(face, style, size);

		int dojaType = 0;
		switch (face) {
			case Font.FACE_MONOSPACE:
				dojaType |= com.nttdocomo.ui.Font.FACE_MONOSPACE;
				break;
			case Font.FACE_PROPORTIONAL:
				dojaType |= com.nttdocomo.ui.Font.FACE_PROPORTIONAL;
				break;
			case Font.FACE_SYSTEM:
				dojaType |= com.nttdocomo.ui.Font.FACE_SYSTEM;
				break;
			default:
				throw new IllegalArgumentException("Invalid face(DoJa) is "
						+ face);
		}

		if ((style & Font.STYLE_PLAIN) != 0) {
			if ((style & Font.STYLE_BOLD) != 0
					|| (style & Font.STYLE_ITALIC) != 0) {
				throw new IllegalArgumentException("Invalid style(DoJa) is "
						+ style);
			}
			style -= Font.STYLE_PLAIN;
			dojaType |= com.nttdocomo.ui.Font.STYLE_PLAIN;
		} else {
			if ((style & Font.STYLE_BOLD) != 0) {
				style -= Font.STYLE_BOLD;
				dojaType |= com.nttdocomo.ui.Font.STYLE_BOLD;
			}
			if ((style & Font.STYLE_ITALIC) != 0) {
				style -= Font.STYLE_ITALIC;

				if ((dojaType & com.nttdocomo.ui.Font.STYLE_BOLD) != 0) {
					dojaType |= com.nttdocomo.ui.Font.STYLE_BOLDITALIC;
				} else {
					dojaType |= com.nttdocomo.ui.Font.STYLE_ITALIC;
				}
			}
		}
		if ((style & Font.STYLE_UNDERLINED) != 0) {
			style -= Font.STYLE_UNDERLINED;
		}
		if (style != 0) {
			throw new IllegalArgumentException("Invalid style(DoJa) is "
					+ style);
		}

		switch (size) {
			case Font.SIZE_LARGE:
				dojaType = com.nttdocomo.ui.Font.SIZE_LARGE;
				break;
			case Font.SIZE_MEDIUM:
				dojaType = com.nttdocomo.ui.Font.SIZE_MEDIUM;
				break;
			case Font.SIZE_SMALL:
				dojaType = com.nttdocomo.ui.Font.SIZE_SMALL;
				break;
			case Font.SIZE_TINY:
				dojaType = com.nttdocomo.ui.Font.SIZE_TINY;
				break;
			default:
				throw new IllegalArgumentException("Invalid size(DoJa) is "
						+ size);
		}

		_font = com.nttdocomo.ui.Font.getFont(dojaType);
	}

	public com.nttdocomo.ui.Font getDoJaFont() {
		return _font;
	}

	public int getBaselinePosition() {
		return _font.getAscent();
	}

	public int getHeight() {
		return _font.getHeight();
	}

	public int charWidth(char c) {
		return _font.stringWidth(String.valueOf(c));
	}

	public int stringWidth(String str) {
		return _font.stringWidth(str);
	}

	public int substringWidth(String str, int offset, int lenght) {
		return _font.stringWidth(str.substring(offset, offset + lenght));
	}
}