package gikolet.base.ui;

/**
 * @author 鉄太郎
 */
public class Label extends Control {
	public static final int	LEFT		= 0;
	public static final int	CENTER		= 1;
	public static final int	RIGHT		= 2;

	private String			_oldText	= "";

	private int				align;

	public Label() {
		this("", LEFT);
	}

	public Label(String text) {
		this(text, LEFT);
	}

	public Label(int horizontalAlignment) {
		this(null, horizontalAlignment);
	}

	public Label(String text, int horizontalAlignment) {
		super(false);

		setText(text);
		this.align = horizontalAlignment;
	}

	public final int getAlignment() {
		return this.align;
	}

	public final void setAlignment(int a) {
		this.align = a;
		alignmentChanged();
		repaint();
	}

	public Dimension getPreferredSize(int hintWidth, int hintHeight) {
		Font font = getFont();
		if (font != null) {
			return new Dimension(font.stringWidth(getText()), font.getHeight());
		}
		return super.getPreferredSize(hintWidth, hintHeight);
	}

	protected void paintControl(Graphics g) {		
		String text = getText();
		Font font = g.getFont();
		int width = getWidth();
		int height = getHeight();

		int x = 0;
		int minW = font.stringWidth(text);
		if (this.align == CENTER) {
			x = (width - minW) / 2;
		} else if (this.align == RIGHT) {
			x = width - minW;
		}
		g.drawString(text, x, (height - font.getHeight()) / 2);
	}

	protected void textChanged() {
		super.textChanged();

		String text = getText();
		Font font = getFont();
		if (font != null
				&& font.stringWidth(text) != font.stringWidth(_oldText)) {
			revalidate();
		}
		_oldText = text;

		repaint();
	}

	protected void alignmentChanged() {
	}

	protected void iconChanged() {
	}
}