/*
 * Created on 2005/02/20 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.doja;

import gikolet.base.ui.Color;
import gikolet.base.ui.Font;
import gikolet.base.ui.Graphics;
import gikolet.base.ui.Rectangle;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DoJaGraphics extends Graphics {
	private com.nttdocomo.ui.Graphics	graphics;
	private Rectangle					_tmpRect;

	static int							screenWidth;
	static int							screenHeight;

	private static Graphics				current;

	// 絶対座標
	// トランスレートの影響を受けない。
	private int							px;
	private int							py;
	private int							pWidth;
	private int							pHeight;

	// 絶対座標
	// トランスレートの影響を受ける。
	private int							x;
	private int							y;
	private int							width;
	private int							height;

	// 絶対座標
	// トランスレートの影響を受ける。
	private int							cx;
	private int							cy;
	private int							cWidth;
	private int							cHeight;

	private int							translateX;
	private int							translateY;

	private Color						color;
	private Color						backColor;
	private int							strokeStyle;
	private DoJaFont					font;

	// 有効な範囲だけを変える
	DoJaGraphics(com.nttdocomo.ui.Graphics g, Rectangle rect) {
		this.graphics = g;
		_tmpRect = rect;
		this.px = this.x = 0;
		this.py = this.y = 0;
		this.pWidth = this.width = screenWidth;
		this.pHeight = this.height = screenHeight;

		rect.setIntersection(this.x, this.y, this.width, this.height);
		clipRectImpl(this.x, this.y, this.width, this.height);

		this.cx = _tmpRect.getX();
		this.cy = _tmpRect.getY();
		this.cWidth = _tmpRect.getWidth();
		this.cHeight = _tmpRect.getHeight();

		this.font = (DoJaFont) Font.getDefaultFont();
		this.color = Color.BLACK;
		this.backColor = Color.WHITE;
		this.strokeStyle = Graphics.SOLID;

		DoJaGraphics.current = null;
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
		_tmpRect.setBounds(this.cx + this.translateX,
				this.cy + this.translateY, this.cWidth, this.cHeight);

		DoJaGraphics g = new DoJaGraphics(this.graphics, _tmpRect);
		g.px = _tmpRect.getX();
		g.py = _tmpRect.getY();
		g.pWidth = _tmpRect.getWidth();
		g.pHeight = _tmpRect.getHeight();

		g.x = x;
		g.y = y;
		g.width = width;
		g.height = height;

		g.cx = _tmpRect.getX();
		g.cy = _tmpRect.getY();
		g.cWidth = _tmpRect.getWidth();
		g.cHeight = _tmpRect.getHeight();

		g.color = this.color;
		g.backColor = this.backColor;
		g.font = this.font;
		g.strokeStyle = this.strokeStyle;

		DoJaGraphics.current = null;

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
			graphics.setColor(com.nttdocomo.ui.Graphics.getColorOfRGB(color
					.getRed(), color.getGreen(), color.getBlue()));

			// フォントを設定する。
			graphics.setFont(this.font.getDoJaFont());

			// カレントのGraphicsをこのGraphicsに設定する。
			current = this;

			return false;
		}
		return true;
	}

	public void setClip(int x, int y, int width, int height) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		_tmpRect.setBounds(this.px, this.py, this.pWidth, this.pHeight);
		graphics.setClip(this.px, this.py, this.pWidth, this.pHeight);

		clipRectImpl(this.x + this.translateX, this.y + this.translateY,
				this.width, this.height);
		clipRectImpl(x + this.translateX, y + this.translateY, width, height);

		this.cx = _tmpRect.getX() - this.translateX;
		this.cy = _tmpRect.getY() - this.translateY;
		this.cWidth = _tmpRect.getWidth();
		this.cHeight = _tmpRect.getHeight();

		currentCheack();
	}

	// translateの影響を受けない
	private void clipRectImpl(int x, int y, int w, int h) {
		if (_tmpRect.getWidth() == 0 || _tmpRect.getHeight() == 0) {
			return;
		}
		_tmpRect.setIntersection(x, y, w, h);
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

		this.cx = _tmpRect.getX() - this.translateX;
		this.cy = _tmpRect.getY() - this.translateY;
		this.cWidth = _tmpRect.getWidth();
		this.cHeight = _tmpRect.getHeight();

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
		this.font = (DoJaFont) font;
		graphics.setFont(this.font.getDoJaFont());

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
		graphics.setColor(com.nttdocomo.ui.Graphics.getColorOfRGB(color
				.getRed(), color.getGreen(), color.getBlue()));
		this.color = color;

		currentCheack();
	}

	public void setStrokeStyle(int strokeStyle) {
		if (strokeStyle != DOTTED && strokeStyle != SOLID) {
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

		graphics.setColor(com.nttdocomo.ui.Graphics.getColorOfRGB(backColor
				.getRed(), backColor.getGreen(), backColor.getBlue()));
		graphics.fillRect(x, y, width, height);

		graphics.setColor(com.nttdocomo.ui.Graphics.getColorOfRGB(color
				.getRed(), color.getGreen(), color.getBlue()));
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {

		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	private int sgn(int a) {
		if (a > 0) {
			return 1;
		} else if (a < 0) {
			return -1;
		} else {
			return 0;
		}
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		x1 += this.x + this.translateX;
		y1 += this.y + this.translateY;
		x2 += this.x + this.translateX;
		y2 += this.y + this.translateY;

		currentCheack();

		if (strokeStyle == DOTTED) {
			// ネット上からパクってきたBresenhamアルゴリズム

			int i, x, y, sx, sy, width, height; // sx,sy は、x,y の増分。 +1 または -1
			int err; // 直線からのずれ
			int width2, height2;

			width = Math.abs(x2 - x1);
			height = Math.abs(y2 - y1);

			width2 = 2 * width;
			height2 = 2 * height;

			sx = sgn(x2 - x1);
			sy = sgn(y2 - y1);

			err = 0;
			x = x1;
			y = y1;

			int dotCount = 0;
			// 実際にラインを描画
			if (width >= height) {
				for (i = 0; i <= width; i++) {
					if (dotCount >= 0) {
						graphics.fillRect(x, y, 1, 1);
						if (dotCount > 1) {
							dotCount = -2;
						}
					}
					dotCount++;
					x += sx;
					err = err + height2;
					if (err >= 0) { // もし、err が 0 以上なら、y 座標をひとつ進める
						err -= width2;
						y += sy;
					}
				}
			} else {
				for (i = 0; i <= height; i++) {
					if (dotCount >= 0) {
						graphics.fillRect(x, y, 1, 1);
						if (dotCount > 1) {
							dotCount = -2;
						}
					}
					dotCount++;
					y += sy;
					err = err + width2;
					if (err >= 0) {
						err -= height2;
						x += sx;
					}
				}
			}
		} else {
			graphics.drawLine(x1, y1, x2, y2);
		}
	}

	public void drawPixel(int x, int y) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.fillRect(x, y, 1, 1);
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
		drawLine(x, y, x + width - 1, y);
		drawLine(x + width, y, x + width, y + height - 1);
		drawLine(x + width, y + height, x + 1, y + height);
		drawLine(x, y + height, x, y + 1);
	}

	/*
	 * public void drawRoundRect(int x, int y, int width, int height, int
	 * arcWidth, int arcHeight) { x += this.x + this.translateX; y += this.y +
	 * this.translateY; currentCheack(); graphics.drawRoundRect(x, y, width,
	 * height, arcWidth, arcHeight); }
	 */

	public void drawChar(char c, int x, int y) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		graphics.drawString(String.valueOf(c), x, y
				+ font.getBaselinePosition());
	}

	public void drawString(String str, int x, int y) {
		x += this.x + this.translateX;
		y += this.y + this.translateY;

		currentCheack();

		if (font.isUnderlined()) {
			int width = font.stringWidth(str);
			int height = font.getHeight();
			graphics.drawLine(x, y + height - 1, x + width, y + height - 1);
		}
		graphics.drawString(str, x, y + font.getBaselinePosition());
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

	/*
	 * public void fillRoundRect(int x, int y, int width, int height, int
	 * arcWidth, int arcHeight) { x += this.x + this.translateX; y += this.y +
	 * this.translateY; currentCheack(); graphics.fillRoundRect(x, y, width,
	 * height, arcWidth, arcHeight); }
	 */
}