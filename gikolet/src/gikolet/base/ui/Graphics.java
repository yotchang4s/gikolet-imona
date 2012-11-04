package gikolet.base.ui;

/*
 * Created on 2003/12/30
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author tetsutaro To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Graphics {
	public final static int	SOLID	= 0;
	public final static int	DOTTED	= 1;

	static int				screenWidth;
	static int				screenHeight;

	//有効な範囲だけを変える
	protected Graphics() {}

	public abstract Graphics createGraphics();

	public abstract Graphics createGraphics(int x, int y, int width, int height);

	public abstract void setClip(int x, int y, int width, int height);

	public abstract void clipRect(int x, int y, int width, int height);

	public abstract void translate(int x, int y);

	public abstract void setFont(Font font);

	public abstract void setBackColor(Color color);

	public abstract void setColor(Color color);

	public abstract void setStrokeStyle(int strokeStyle);

	public abstract int getStrokeStyle();

	public abstract Color getColor();

	public abstract Color getBackColor();

	public abstract Font getFont();

	public abstract int getTranslateX();

	public abstract int getTranslateY();

	public abstract int getClipX();

	public abstract int getClipY();

	public abstract int getClipWidth();

	public abstract int getClipHeight();

	public abstract void clearRect(int x, int y, int width, int height);

	public abstract void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	public abstract void drawLine(int x1, int y1, int x2, int y2);

	public abstract void drawPolyline(int[] xPoints, int[] yPoints, int nPoints);

	public abstract void drawRect(int x, int y, int width, int height);

	/*public abstract void drawRoundRect(int x, int y, int width, int height, int arcWidth,
	 int arcHeight);*/

	public abstract void drawChar(char c, int x, int y);

	public abstract void drawString(String str, int x, int y);

	public abstract void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	public abstract void fillRect(int x, int y, int width, int height);

	/*public abstract void fillRoundRect(int x, int y, int width, int height, int arcWidth,
	 int arcHeight);*/
}