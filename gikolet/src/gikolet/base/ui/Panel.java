/*
 * 作成日： 2004/12/06 TODO この生成されたファイルのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 * コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 *         コード・スタイル ＞ コード・テンプレート
 */
public class Panel extends ContainerControl {
	public final static int	HORIZONTAL		= 0;
	public final static int	VERTICAL		= 1;

	private int				type;
	private int				marginWidth		= 0;
	private int				marginHeight	= 0;
	private int				spacing			= 0;

	protected Panel(boolean focus, boolean validateRoot) {
		this(focus, validateRoot, HORIZONTAL, 0, 0, 0);
	}

	protected Panel(boolean focus, boolean validateRoot, int direction,
			int marginWidth, int marginHeight, int spacting) {
		super(focus, validateRoot);

		setDirection(direction);
		setMargin(marginWidth, marginHeight);
	}
	
	public Panel() {
		this(HORIZONTAL, 0, 0, 0);
	}

	public Panel(int direction, int marginWidth, int marginHeight, int spacting) {
		this(false, false);
	}

	public void setDirection(int direction) {
		if (direction != HORIZONTAL && direction != VERTICAL) {
			throw new IllegalArgumentException(
					"direction is not HORIZONTAL and VERTICAL.");
		}
		this.type = direction;
	}

	public void setMargin(int marginWidth, int marginHeight) {
		if (marginWidth < 0) {
			throw new IllegalArgumentException(
					"marginWidth is a negative value.");
		}
		if (marginHeight < 0) {
			throw new IllegalArgumentException(
					"marginHeight is a negative value.");
		}
		this.marginWidth = marginWidth;
		this.marginHeight = marginHeight;
		revalidate();
		repaint();
	}

	protected void layout() {
		Rectangle rect = getClientArea();
		if (getControlCount() == 0) {
			return;
		}
		int width = rect.getWidth() - this.marginWidth * 2;
		int height = rect.getHeight() - this.marginHeight * 2;
		if (this.type == HORIZONTAL) {
			width -= (getControlCount() - 1) * this.spacing;
			int x = rect.getX() + this.marginWidth, extra = width
					% getControlCount();
			int y = rect.getY() + this.marginHeight, cellWidth = width
					/ getControlCount();
			for (int i = 0; i < getControlCount(); i++) {
				Control child = getControlAt(i);
				int childWidth = cellWidth;
				if (i == 0) {
					childWidth += extra / 2;
				} else {
					if (i == getControlCount() - 1) {
						childWidth += (extra + 1) / 2;
					}
				}
				child.setBounds(x, y, childWidth, height);
				x += childWidth + this.spacing;
			}
		} else {
			height -= (getControlCount() - 1) * this.spacing;

			int x = rect.getX() + this.marginWidth;
			int cellHeight = height / getControlCount();
			int y = rect.getY() + this.marginHeight;
			int extra = height % getControlCount();

			for (int i = 0; i < getControlCount(); i++) {
				Control child = getControlAt(i);
				int childHeight = cellHeight;
				if (i == 0) {
					childHeight += extra / 2;
				} else {
					if (i == getControlCount() - 1) {
						childHeight += (extra + 1) / 2;
					}
				}
				child.setBounds(x, y, width, childHeight);
				y += childHeight + this.spacing;
			}
		}
	}

	public Dimension getPreferredSize(int hintWidth, int hintHeight) {
		int maxWidth = Control.DEFAULT, maxHeight = Control.DEFAULT;

		if (hintWidth != Control.DEFAULT) {
			int w = hintWidth - this.marginWidth * 2 - getLeftInset()
					- getRightInset();

			if (type == HORIZONTAL) {
				w -= (getControlCount() - 1) * this.spacing;
				maxWidth = w / getControlCount();
			} else {
				maxWidth = w;
			}
		}
		if (hintHeight != Control.DEFAULT) {
			int h = hintHeight - this.marginHeight * 2 - getTopInset()
					- getBottomInset();

			if (type != HORIZONTAL) {
				h -= (getControlCount() - 1) * this.spacing;
				maxHeight = h / getControlCount();
			} else {
				maxHeight = h;
			}
		}
		for (int i = 0; i < getControlCount(); i++) {
			Control child = getControlAt(i);
			Dimension size = child.getPreferredSize(maxWidth, maxHeight);
			if (hintWidth == Control.DEFAULT) {
				maxWidth = Math.max(maxWidth, size.getWidth());
			}
			if (hintHeight == Control.DEFAULT) {
				maxHeight = Math.max(maxHeight, size.getHeight());
			}
		}
		int width = 0, height = 0;
		if (this.type == HORIZONTAL) {
			width = getControlCount() * maxWidth;
			if (getControlCount() != 0) {
				width += (getControlCount() - 1) * this.spacing;
			}
			height = maxHeight;
		} else {
			width = maxWidth;
			height = getControlCount() * maxHeight;
			if (getControlCount() != 0) {
				height += (getControlCount() - 1) * this.spacing;
			}
		}
		width += this.marginWidth * 2 + getLeftInset() + getRightInset();
		height += this.marginHeight * 2 + getTopInset() + getBottomInset();

		return new Dimension(width, height);
	}
}