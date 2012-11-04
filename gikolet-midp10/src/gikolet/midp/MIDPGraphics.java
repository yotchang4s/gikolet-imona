/*
 * Created on 2005/02/20 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.midp;

import gikolet.base.ui.Color;
import gikolet.base.ui.Font;
import gikolet.base.ui.Graphics;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
class MIDPGraphics extends Graphics {
	private javax.microedition.lcdui.Graphics	graphics;
	static int									screenWidth;
	static int									screenHeight;

	private static Graphics						current;

	// 絶対座標
	// トランスレートの影響を受けない。
	private int									px;
	private int									py;
	private int									pWidth;
	private int									pHeight;

	// 絶対座標
	// トランスレートの影響を受ける。
	private int									x;
	private int									y;
	private int									width;
	private int									height;

	// 絶対座標
	// トランスレートの影響を受ける。
	private int									cx;
	private int									cy;
	private int									cWidth;
	private int									cHeight;

	private int									translateX;
	private int									translateY;

	private Color								color;
	private Color								backColor;
	private int									strokeStyle;
	private MIDPFont							font;

	// 有効な範囲だけを変える
	MIDPGraphics(javax.microedition.lcdui.Graphics g) {
		this.graphics = g;
		this.px = this.x = 0;
		this.py = this.y = 0;
		this.pWidth = this.width = screenWidth;
		this.pHeight = this.height = screenHeight;

		// this.graphics.clipRect(this.x, this.y, this.width, this.height);
		clipRectImpl(this.x, this.y, this.width, this.height);
		this.cx = this.graphics.getClipX();
		this.cy = this.graphics.getClipY();
		this.cWidth = this.graphics.getClipWidth();
		this.cHeight = this.graphics.getClipHeight();

		this.font = (MIDPFont) Font.getDefaultFont();
		this.color = Color.BLACK;
		this.backColor = Color.WHITE;
		this.strokeStyle = Graphics.SOLID;

		MIDPGraphics.current = null;
	}

	public Graphics createGraphics() {
		return createGraphics(this.translateX, this.translateY, this.width,
				this.height);
	}

	public Graphics createGraphics(int x, int y, int width, int height) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		graphics.setClip(this.cx + this.translateX, this.cy + this.translateY,
				this.cWidth, this.cHeight);

		MIDPGraphics g = new MIDPGraphics(this.graphics);
		g.px = graphics.getClipX();
		g.py = graphics.getClipY();
		g.pWidth = graphics.getClipWidth();
		g.pHeight = graphics.getClipHeight();

		g.x = x;
		g.y = y;
		g.width = width;
		g.height = height;

		g.cx = graphics.getClipX();
		g.cy = graphics.getClipY();
		g.cWidth = graphics.getClipWidth();
		g.cHeight = graphics.getClipHeight();

		g.color = this.color;
		g.backColor = this.backColor;
		g.font = this.font;
		g.strokeStyle = this.strokeStyle;

		MIDPGraphics.current = null;

		return g;
	} // このGrpahicsがカレントかをチェックする。

	// 違うならばgraphicsの値をこのGraphicsに適したものにしてこのGraphicsをカレントにする。
	// カレントのときはtrue、そうでないときはfalseを返す。
	private boolean currentCheack() {
		if (current != this) {
			// クリップの設定
			graphics.setClip(this.cx + this.translateX, this.cy
					+ this.translateY, this.cWidth, this.cHeight);

			// 色を設定する。
			graphics.setColor(this.color.getRGB());

			// フォントを設定する。
			graphics.setFont(this.font.getMIDPFont());

			// ストロークスタイルを設定する。
			if (this.strokeStyle == SOLID) {
				graphics
						.setStrokeStyle(javax.microedition.lcdui.Graphics.SOLID);
			} else if (this.strokeStyle == DOTTED) {
				graphics
						.setStrokeStyle(javax.microedition.lcdui.Graphics.DOTTED);
			}

			// カレントのGraphicsをこのGraphicsに設定する。
			current = this;

			return false;
		}
		return true;
	}

	public void setClip(int x, int y, int width, int height) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		graphics.setClip(this.px, this.py, this.pWidth, this.pHeight);
		clipRectImpl(this.x + this.translateX, this.y + this.translateY,
				this.width, this.height);
		clipRectImpl(x + this.translateX, y + this.translateY, width, height);

		this.cx = graphics.getClipX() - this.translateX;
		this.cy = graphics.getClipY() - this.translateY;
		this.cWidth = graphics.getClipWidth();
		this.cHeight = graphics.getClipHeight();

		currentCheack();
	}

	// translateの影響を受けない
	private void clipRectImpl(int x, int y, int w, int h) {
		if (graphics.getClipWidth() == 0 || graphics.getClipHeight() == 0) {
			return;
		}
		graphics.clipRect(x, y, w, h);
	}

	public void clipRect(int x, int y, int width, int height) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		if (current != this) {
			graphics.setClip(this.cx + this.translateX, this.cy
					+ this.translateY, this.cWidth, this.cHeight);
		}

		clipRectImpl(x + this.translateX, y + this.translateX, width, height);

		this.cx = graphics.getClipX() - this.translateX;
		this.cy = graphics.getClipY() - this.translateY;
		this.cWidth = graphics.getClipWidth();
		this.cHeight = graphics.getClipHeight();

		currentCheack();
	}

	public void translate(int x, int y) {
		this.translateX += x;
		this.translateY += y;

		if (currentCheack()) {
			graphics.setClip(this.px, this.py, this.pWidth, this.pHeight);
			clipRectImpl(this.cx + this.translateX, this.cy + this.translateY,
					this.cWidth, this.cHeight);
		}
	}

	public void setFont(Font font) {
		this.font = (MIDPFont) font;
		graphics.setFont(this.font.getMIDPFont());

		currentCheack();
	}

	public void setBackColor(Color backColor) {
		if (backColor == null) {
			throw new NullPointerException("backColor is null.");
		}
		this.backColor = backColor;

		currentCheack();
	}

	public void setColor(Color color) {
		if (color == null) {
			throw new NullPointerException("color is null.");
		}
		graphics.setColor(color.getRGB());
		this.color = color;

		currentCheack();
	}

	public void setStrokeStyle(int strokeStyle) {
		if (strokeStyle == DOTTED) {
			graphics.setStrokeStyle(javax.microedition.lcdui.Graphics.DOTTED);
		} else if (strokeStyle == SOLID) {
			graphics.setStrokeStyle(javax.microedition.lcdui.Graphics.SOLID);
		} else {
			throw new IllegalArgumentException("strokeStyle is illegal.");
		}
		this.strokeStyle = strokeStyle;

		currentCheack();
	}

	public int getStrokeStyle() {
		return this.strokeStyle;
	}

	public Color getColor() {
		return this.color;
	}

	public Color getBackColor() {
		return this.backColor;
	}

	public Font getFont() {
		return this.font;
	}

	public int getTranslateX() {
		return this.translateX;
	}

	public int getTranslateY() {
		return this.translateY;
	}

	public int getClipX() {
		return this.cx - this.x + this.translateX;
	}

	public int getClipY() {
		return this.cy - this.y + this.translateY;
	}

	public int getClipWidth() {
		return this.cWidth;
	}

	public int getClipHeight() {
		return this.cHeight;
	}

	public void clearRect(int x, int y, int width, int height) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		int color = graphics.getColor();

		graphics.setColor(this.backColor.getRGB());
		graphics.fillRect(x, y, width, height);

		graphics.setColor(color);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {

		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	/*
	 * public void drawImage(Image image, int x, int y) { x += this.x +
	 * translateX; y += this.y + translateY; currentCheack();
	 * graphics.drawImage( image, x, y, javax.microedition.lcdui.Graphics.TOP |
	 * javax.microedition.lcdui.Graphics.LEFT); }
	 */

	public void drawLine(int x1, int y1, int x2, int y2) {
		x1 += this.x + this.translateX;
		y1 += this.y + this.translateY;
		x2 += this.x + this.translateX;
		y2 += this.y + this.translateY;

		currentCheack();

		graphics.drawLine(x1, y1, x2, y2);
	}

	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		currentCheack();

		for (int i = 0; i < nPoints - 1; i++) {
			graphics
					.drawLine(this.x + this.translateX + xPoints[i], this.y
							+ this.translateY + yPoints[i], this.x
							+ this.translateX + xPoints[i + 1], this.y
							+ this.translateY + yPoints[i + 1]);
		}
	}

	public void drawRect(int x, int y, int width, int height) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.drawRect(x, y, width, height);
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {

		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void drawChar(char c, int x, int y) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.drawChar(c, x, y, javax.microedition.lcdui.Graphics.TOP
				| javax.microedition.lcdui.Graphics.LEFT);
	}

	public void drawString(String str, int x, int y) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.drawString(str, x, y, javax.microedition.lcdui.Graphics.TOP
				| javax.microedition.lcdui.Graphics.LEFT);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {

		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public void fillRect(int x, int y, int width, int height) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();
		graphics.fillRect(x, y, width, height);
	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {

		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public String toString() {
		String str = "px=" + this.px + ";py=" + this.py + ";pw=" + this.pWidth
				+ ";ph=" + this.pHeight;
		str += "\nx=" + this.x + ";y=" + this.y + ";w=" + this.width + ";h="
				+ this.height;
		str += "\ncx=" + this.cx + ";cy=" + this.cy + ";cw=" + this.cWidth
				+ ";ch=" + this.cHeight;
		str += "\nrcx=" + graphics.getClipX() + ";rcy=" + graphics.getClipY()
				+ ";rcw=" + graphics.getClipWidth() + ";rch="
				+ graphics.getClipHeight();
		return str;
	}
}