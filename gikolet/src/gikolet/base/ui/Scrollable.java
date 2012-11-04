/*
 * 作成日： 2004/12/07 TODO この生成されたファイルのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 * コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

import gikolet.base.Toolkit;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 *         コード・スタイル ＞ コード・テンプレート
 */
public abstract class Scrollable extends Control {
	public final static int		SCROLLBAR_AS_NEEDED	= 0;
	public final static int		SCROLLBAR_NEVER		= 1;
	public final static int		SCROLLBAR_ALWAYS	= 2;

	private final static int	scrollBarAreaWidth	= 6;

	private int					vsbPolicy;
	private int					hsbPolicy;

	private boolean				showVerticalScrollBar;
	private boolean				showHorizontalScrollBar;

	private int					_viewContentX, _viewContentY,
			_viewContentWidth, _viewContentHeight;
	private int					_viewAreaTop, _viewAreaBottom, _viewAreaLeft,
			_viewAreaRight;

	private int					_scrollWeight;

	protected Scrollable(boolean focusable) {
		this(focusable, SCROLLBAR_AS_NEEDED, SCROLLBAR_AS_NEEDED);
	}

	protected Scrollable(boolean focusable, int vsbPolicy, int hsbPolicy) {
		super(focusable);

		this.showVerticalScrollBar = false;
		this.showHorizontalScrollBar = false;

		setScrollWeight(-1);

		setVerticalScrollBarPolicy(vsbPolicy);
		setHorizontalScrollBarPolicy(hsbPolicy);

		setBackColor(Color.WHITE);
		setForeColor(Color.BLACK);
	}

	public void setScrollWeight(int scrollWeight) {
		if (scrollWeight <= 0) {
			scrollWeight = -1;
		}
		_scrollWeight = scrollWeight;
	}

	public int getScrollWeight() {
		return _scrollWeight;
	}

	public boolean isShowVerticalScrollBar() {
		return this.showVerticalScrollBar;
	}

	public boolean isShowHorizontalScrollBar() {
		return this.showHorizontalScrollBar;
	}

	private final static Color	scrollBar		= new Color(0xFF8040);
	private final static Color	scrollBarShadow	= new Color(0x804000);
	private final static Color	scrollBarBG		= new Color(0x969696);

	protected void paintControl(Graphics g) {
		int width = getWidth()
				- ((this.showVerticalScrollBar) ? scrollBarAreaWidth : 0);
		int height = getHeight()
				- ((this.showHorizontalScrollBar) ? scrollBarAreaWidth : 0);

		int cw = width - 1;
		int ch = height - 1;

		if (!this.showVerticalScrollBar) {
			cw--;
		}
		if (!this.showHorizontalScrollBar) {
			ch--;
		}

		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();

		g.clipRect(1, 1, cw, ch);
		Graphics viewContentGraphics = g.createGraphics(-getViewContentX() + 1,
				-getViewContentY() + 1, getViewContentWidth(),
				getViewContentHeight());
		paintViewContent(viewContentGraphics);
		g.setClip(clipX, clipY, clipWidth, clipHeight);

		if (this.showVerticalScrollBar) {
			g.setColor(Color.BLACK);
			g.drawRect(width, 0, scrollBarAreaWidth - 1, height - 1);
			g.setColor(scrollBarBG);
			g.drawRect(width + 1, 1, scrollBarAreaWidth - 3, height - 3);

			int contentHeight = getViewContentHeight();
			if (contentHeight > 0) {
				int bh = (height * ch) / contentHeight;
				bh = (bh < 8) ? 8 : bh;

				int x = width + 1;
				int y = ((height - bh) * getViewContentY())
						/ (contentHeight - ch);

				g.setColor(scrollBar);
				g.fillRect(x, y + 1, scrollBarAreaWidth - 2, bh - 2);

				g.setColor(Color.BLACK);
				g.drawLine(x, y, x + scrollBarAreaWidth - 3, y);
				g.drawLine(x, y + bh - 1, x + scrollBarAreaWidth - 3, y + bh
						- 1);

				g.setColor(scrollBarShadow);
				g.drawRect(x, y + 1, scrollBarAreaWidth - 3, bh - 3);
			}
		}
		if (this.showHorizontalScrollBar) {
			g.setColor(Color.BLACK);
			g.drawRect(0, height, width - 1, scrollBarAreaWidth - 1);
			g.setColor(scrollBarBG);
			g.drawRect(1, height + 1, width - 3, scrollBarAreaWidth - 3);

			int contentWidth = getViewContentWidth();
			if (contentWidth > 0) {
				int bw = (width * cw) / contentWidth;
				bw = (bw < 10) ? 10 : bw;

				int x = ((width - bw) * getViewContentX())
						/ (contentWidth - cw);
				int y = height + 1;

				g.setColor(scrollBar);
				g.fillRect(x + 1, y, bw - 2, scrollBarAreaWidth - 2);

				g.setColor(Color.BLACK);
				g.drawLine(x, y, x, y + scrollBarAreaWidth - 3);
				g.drawLine(x + bw - 1, y, x + bw - 1, y + scrollBarAreaWidth
						- 3);

				g.setColor(scrollBarShadow);
				g.drawRect(x + 1, y, bw - 3, scrollBarAreaWidth - 3);
			}
		}
	}

	protected void paintViewContent(Graphics g) {
	}

	protected Dimension getContentPreferredSize(int hintWidth, int hintHeight) {
		int viewContentWidth = getViewContentWidth();
		int viewContentHeight = getViewContentHeight();
		if (hintWidth != DEFAULT) {
			viewContentWidth = hintWidth;
		}
		if (hintHeight != DEFAULT) {
			viewContentHeight = hintHeight;
		}
		return new Dimension(viewContentWidth, viewContentHeight);
	}

	protected void revalidateViewContent() {
		final int thisCount = ++revalidateCount;

		Toolkit.getDisplay().invoke(new Runnable() {
			public void run() {
				if (thisCount == revalidateCount) {
					validateViewContent();
				}
			}
		});
	}

	public void repaintViewContent() {
		repaintViewContent(0, 0, getViewContentWidth(), getViewAreaHeight());
	}

	public void repaintViewContent(int x, int y, int width, int height) {
		int vAX = getViewAreaY();
		int vAY = getViewAreaY();

		int vCX = getViewContentX();
		int vCY = getViewContentY();
		int vAW = getViewAreaWidth();
		int vAH = getViewAreaHeight();

		if (width <= 0 || height <= 0 || x + width <= 0 || vCX + vAW <= x
				|| y + height <= 0 || vCY + vAH <= y) {
			return;
		}
		int pLeft = Math.max(x, vCX);
		int pTop = Math.max(y, vCY);
		int pRight = Math.min(x + width, vCX + vAW);
		int pBottom = Math.min(y + height, vCY + vAH);

		if (pRight - pLeft > 0 || pBottom - pTop > 0) {
			repaint(pLeft + vAX - vCX, pTop + vAY - vCY, pRight - pLeft,
					pBottom - pTop);
		}

	}

	private int	revalidateCount	= 0;

	public void validateViewContent() {
		revalidateCount = 0;

		boolean hspNever = getHorizontalScrollBarPolicy() == SCROLLBAR_NEVER;
		boolean vspNever = getVerticalScrollBarPolicy() == SCROLLBAR_NEVER;

		int width = getWidth();
		int height = getHeight();
		int w = width;
		int h = height;
		width -= 2;
		height -= 2;

		Dimension pd = getContentPreferredSize((hspNever) ? width : DEFAULT,
				(vspNever) ? height : DEFAULT);

		this.showVerticalScrollBar = false;
		this.showHorizontalScrollBar = false;

		if (this.vsbPolicy == SCROLLBAR_ALWAYS) {
			this.showVerticalScrollBar = true;
			width = w - 1 - scrollBarAreaWidth;
		} else if (this.vsbPolicy == SCROLLBAR_AS_NEEDED) {
			if (pd.getHeight() > height) {
				this.showVerticalScrollBar = true;
				width = w - 1 - scrollBarAreaWidth;
			}
		}
		if (this.hsbPolicy == SCROLLBAR_ALWAYS) {
			this.showHorizontalScrollBar = true;
			height = h - 1 - scrollBarAreaWidth;
		} else if (this.hsbPolicy == SCROLLBAR_AS_NEEDED) {
			if (pd.getWidth() > width) {
				this.showHorizontalScrollBar = true;
				height = h - 1 - scrollBarAreaWidth;

				if (this.vsbPolicy == SCROLLBAR_AS_NEEDED) {
					if (pd.getHeight() > height) {
						this.showVerticalScrollBar = true;
						width = w - 1 - scrollBarAreaWidth;
					}
				}
			}
		}

		if (this.vsbPolicy == SCROLLBAR_AS_NEEDED && this.showVerticalScrollBar
				&& hspNever) {
			pd = getContentPreferredSize(width, DEFAULT);
			this.showVerticalScrollBar = pd.getHeight() > h - 2;
		} else if (this.hsbPolicy == SCROLLBAR_AS_NEEDED
				&& this.showHorizontalScrollBar && vspNever) {
			pd = getContentPreferredSize(DEFAULT, height);
			this.showHorizontalScrollBar = pd.getWidth() > w - 2;
		}
		int rightInset = 1;
		int bottomInset = 1;
		if (this.showVerticalScrollBar) {
			rightInset = scrollBarAreaWidth;
		}
		if (this.showHorizontalScrollBar) {
			bottomInset = scrollBarAreaWidth;
		}

		_viewAreaTop = 1;
		_viewAreaBottom = bottomInset;
		_viewAreaLeft = 1;
		_viewAreaRight = rightInset;

		int cw = getViewAreaWidth();
		if (cw > pd.getWidth()) {
			pd.setWidth(cw);
		}

		int ch = getViewAreaHeight();
		if (ch > pd.getHeight()) {
			pd.setHeight(ch);
		}

		setViewContentSize(pd);
		// scrollCheck();
		setViewContentPosition(getViewContentX(), getViewContentY());
	}

	protected void scrollRectToVisible(int x, int y, int width, int height) {
		int viewAreaWidth = getViewAreaWidth();
		int viewAreaHeight = getViewAreaHeight();

		int viewContentX = getViewContentX();
		int viewContentY = getViewContentY();

		int dx = positionAdjustment(viewAreaWidth, x - viewContentX, width);
		int dy = positionAdjustment(viewAreaHeight, y - viewContentY, height);

		if (dx != 0 || dy != 0) {
			setViewContentPosition(viewContentX - dx, viewContentY - dy);
		}
	}

	private int positionAdjustment(int parentWidth, int childAt, int childWidth) {
	    	// 変更無し
		if (childAt >= 0 && childWidth + childAt <= parentWidth) {
			return 0;
		}
	    	// 変更無し
		if (childAt <= 0 && childWidth + childAt >= parentWidth) {
			return 0;
		}
		if (childAt > 0 && childWidth <= parentWidth) {
			return -childAt + parentWidth - childWidth;
		}
		if (childAt >= 0 && childWidth >= parentWidth) {
			return -childAt;
		}
		if (childAt <= 0 && childWidth <= parentWidth) {
			return -childAt;
		}
		if (childAt < 0 && childWidth >= parentWidth) {
			return -childAt + parentWidth - childWidth;
		}
		return 0;
	}

	private void setViewContentSize(Dimension d) {
		_viewContentWidth = d.getWidth();
		_viewContentHeight = d.getHeight();

		viewContentSizeChanged();
	}

	protected void sizeChanged() {
		revalidateViewContent();
		repaintViewContent();

		super.sizeChanged();
	}

	protected void viewContentSizeChanged() {
	}

	public int getViewAreaX() {
		return _viewAreaLeft;
	}

	public int getViewAreaY() {
		return _viewAreaTop;
	}

	public int getViewAreaWidth() {
		return getWidth() - _viewAreaLeft - _viewAreaRight;
	}

	public int getViewAreaHeight() {
		return getHeight() - _viewAreaTop - _viewAreaBottom;
	}

	// forwardがtrueで順方向, falseで逆方向
	public boolean isVScrollEnd(boolean forward) {
		if (forward) {
			return getViewContentY() + getViewAreaHeight() == getViewContentHeight();
		} else {
			return getViewContentY() == 0;
		}
	}
	
//	 forwardがtrueで順方向, falseで逆方向
	public boolean isHScrollEnd(boolean forward) {
		if (forward) {
			return getViewContentX() + getViewAreaWidth() == getViewContentWidth();
		} else {
			return getViewContentX() == 0;
		}
	}

	public boolean scrollUP() {
		int sw = _scrollWeight;
		if (sw == -1) {
			sw = getViewAreaHeight();
		}
		return scrollUP(sw);
	}

	public boolean scrollDown() {
		int sw = _scrollWeight;
		if (sw == -1) {
			sw = getViewAreaHeight();
		}
		return scrollDown(sw);
	}

	public boolean scrollLeft() {
		int sw = _scrollWeight;
		if (sw == -1) {
			sw = getViewAreaWidth();
		}
		return scrollLeft(sw);
	}

	public boolean scrollRight() {
		int sw = _scrollWeight;
		if (sw == -1) {
			sw = getViewAreaWidth();
		}
		return scrollRight(sw);
	}

	public boolean scrollUP(int height) {
		int vy = getViewContentY();
		setViewContentPosition(getViewContentX(), vy - height);
		return vy != getViewContentY();
	}

	public boolean scrollDown(int height) {
		int vy = getViewContentY();
		setViewContentPosition(getViewContentX(), vy + height);
		return vy != getViewContentY();
	}

	public boolean scrollLeft(int width) {
		int vx = getViewContentX();
		setViewContentPosition(vx - width, getViewContentY());
		return vx != getViewContentX();
	}

	public boolean scrollRight(int width) {
		int vx = getViewContentX();
		setViewContentPosition(vx + width, getViewContentY());
		return vx != getViewContentX();
	}

	public int getViewContentX() {
		return _viewContentX;
	}

	public int getViewContentY() {
		return _viewContentY;
	}

	public int getViewContentWidth() {
		return _viewContentWidth;
	}

	public int getViewContentHeight() {
		return _viewContentHeight;
	}

	public void setViewContentPosition(int x, int y) {
		int viewAreaWidth = getViewAreaWidth();
		int viewAreaHeight = getViewAreaHeight();

		int viewContentWidth = getViewContentWidth();
		int viewContentHeigth = getViewContentHeight();

		if (viewContentWidth < x + viewAreaWidth) {
			x = Math.max(0, viewContentWidth - viewAreaWidth);
		} else if (x < 0) {
			x = 0;
		}
		if (viewContentHeigth < y + viewAreaHeight) {
			y = Math.max(0, viewContentHeigth - viewAreaHeight);
		} else if (y < 0) {
			y = 0;
		}
		_viewContentX = x;
		_viewContentY = y;

		repaint();
	}

	public Dimension getPreferredSize(int hintWidth, int hintHeight) {
		Dimension ps = getContentPreferredSize(DEFAULT, DEFAULT);

		int pW = ps.getWidth();
		int pH = ps.getHeight();
		ps.setWidth(ps.getWidth() + 2);
		ps.setHeight(ps.getHeight() + 2);

		// int hsp = getHorizontalScrollBarPolicy();
		// int vsp = getVerticalScrollBarPolicy();

		if (this.vsbPolicy == SCROLLBAR_ALWAYS) {
			ps.setWidth(1 + pW + scrollBarAreaWidth);
		} else if (this.vsbPolicy == SCROLLBAR_AS_NEEDED) {
			if (hintHeight != DEFAULT) {
				if (ps.getHeight() > hintHeight) {
					ps.setWidth(1 + pW + scrollBarAreaWidth);
				}
			}
		}
		if (this.hsbPolicy == SCROLLBAR_ALWAYS) {
			ps.setHeight(1 + pH + scrollBarAreaWidth);
		} else if (this.hsbPolicy == SCROLLBAR_AS_NEEDED) {
			if (hintWidth != DEFAULT) {
				if (ps.getWidth() > hintWidth) {
					ps.setHeight(1 + pH + scrollBarAreaWidth);

					if (this.vsbPolicy == SCROLLBAR_AS_NEEDED) {
						if (hintHeight != DEFAULT) {
							if (ps.getHeight() > hintHeight) {
								ps.setWidth(1 + pW + scrollBarAreaWidth);
							}
						}
					}
				}
			}
		}
		if (hintWidth != DEFAULT) {
			ps.setWidth(hintWidth - 2);
		}
		if (hintHeight != DEFAULT) {
			ps.setHeight(hintHeight - 2);
		}
		return ps;
	}

	public void setVerticalScrollBarPolicy(int policy) {
		switch (policy) {
			case SCROLLBAR_ALWAYS:
			case SCROLLBAR_AS_NEEDED:
			case SCROLLBAR_NEVER:
				break;
			default:
				throw new IllegalArgumentException("policy is illegal.");
		}
		this.vsbPolicy = policy;
		revalidate();
		repaint();
	}

	public void setHorizontalScrollBarPolicy(int policy) {
		switch (policy) {
			case SCROLLBAR_ALWAYS:
			case SCROLLBAR_AS_NEEDED:
			case SCROLLBAR_NEVER:
				break;
			default:
				throw new IllegalArgumentException("policy is illegal.");
		}
		this.hsbPolicy = policy;
		revalidate();
		repaint();
	}

	public int getVerticalScrollBarPolicy() {
		return this.vsbPolicy;
	}

	public int getHorizontalScrollBarPolicy() {
		return this.hsbPolicy;
	}
}