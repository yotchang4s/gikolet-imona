/*
 * Created on 2005/01/30 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet;

import gikolet.base.ui.Color;
import gikolet.base.ui.Dimension;
import gikolet.base.ui.Font;
import gikolet.base.ui.Graphics;
import gikolet.base.ui.Scrollable;
import gikolet.base.ui.events.KeyEvent;
import gikolet.imona.Res;
import gikolet.imona.ThreadHeader;

import java.util.Vector;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ResView extends Scrollable {
	public final static int	RES_SHOW_METHOD_2CH				= 0;
	public final static int	RES_SHOW_METHOD_BODY_PRIORITY	= 1;

	private Res				_res;
	private Color			nameColor						= new Color(0x008000);
	private int				_selectedIndex					= -1;
	private int				_showMethod;

	private Font			_underLineFont;
	private Font			_font;

	private boolean			aaMode							= false;

	private Vector			_paintData						= new Vector();
	private Vector			_linkPaintData					= new Vector();
	// private boolean _valid = false;
	private int				_startPaintIndex;

	private ThreadHeader	_threadHeader;

	private class PaintData {
		Res.Link	link;
		String[]	lines;
		Font		font;
		Color		color;
		int			startX;
		int			x;
		int			y;
		int			width;
		int			height;
		int			linkIndex	= -1;
	}

	public ResView() {
		super(true, Scrollable.SCROLLBAR_AS_NEEDED, Scrollable.SCROLLBAR_NEVER);

		_showMethod = RES_SHOW_METHOD_2CH;

		setBackColor(new Color(0xEFEFEF));
		setForeColor(Color.BLACK);

		setRes(null, null);
	}

	public void setResShowMethod(int showMethod) {
		if (showMethod != RES_SHOW_METHOD_2CH && showMethod != RES_SHOW_METHOD_BODY_PRIORITY) {
			throw new IllegalArgumentException("showMethod is illegal.");
		}
		_showMethod = showMethod;

		cachePrefSize = null;

		revalidateViewContent();
		repaintViewContent();
	}

	public int getResShowMethod() {
		return _showMethod;
	}

	public boolean isAAMode() {
		return aaMode;
	}

	public void setAAMode(boolean _aaMode) {
		if (_aaMode != aaMode) {
			aaMode = _aaMode;
			if (aaMode) {
				setHorizontalScrollBarPolicy(Scrollable.SCROLLBAR_AS_NEEDED);
			} else {
				setHorizontalScrollBarPolicy(Scrollable.SCROLLBAR_NEVER);
			}
			cachePrefSize = null;
			_selectedIndex = -1;

			if (_font != null) {
				if (_aaMode) {
					if (_font.getFace() != Font.FACE_MONOSPACE) {
						_font = Font.createFont(Font.FACE_MONOSPACE, _font.getStyle(), _font
								.getSize());

						revalidateViewContent();
					}
				} else {
					Font f = getFont();
					if (_font != f) {
						_font = f;
						revalidateViewContent();
					}
				}
			}
		}
	}

	public void setSelectedIndex(int index) {
		if (-1 <= index && index <= _linkPaintData.size()) {
			int oldIndex = _selectedIndex;
			_selectedIndex = index;
			repaintPaintData(oldIndex);
			repaintPaintData(index);
		}
	}

	public int getSelectedIndex() {
		return _selectedIndex;
	}

	public int getLinkSize() {
		return _linkPaintData.size();
	}

	public boolean isNext(boolean forward) {
		if (_linkPaintData.isEmpty()) {
			return false;
		} else {
			if (forward) {
				return _linkPaintData.size() != _selectedIndex;
			} else {
				return -1 != _selectedIndex;
			}
		}
	}

	private void repaintPaintData(int index) {
		if (0 <= index && index < _linkPaintData.size()) {
			PaintData pd = (PaintData) _linkPaintData.elementAt(index);
			repaintViewContent(pd.x, pd.y, pd.width, pd.height);
		}
	}

	protected void keyAction(KeyEvent event) {
		int type = event.getKeyActionType();
		int code = event.getKeyCode();
		if (type != KeyEvent.RELEASED) {
			int viewCX = getViewContentX();
			int viewCY = getViewContentY();
			int viewAW = getViewAreaWidth();
			int viewAH = getViewAreaHeight();

			if (code == KeyEvent.DOWN || code == KeyEvent.KEY_NUM8) {
				boolean first = true;
				do {
					if (_selectedIndex + 1 < _linkPaintData.size()) {
						PaintData pd = (PaintData) _linkPaintData.elementAt(_selectedIndex + 1);
						if (viewCY <= pd.y && pd.y + pd.font.getHeight() <= viewCY + viewAH
								&& (viewCX < pd.x + pd.width && pd.x < viewCX + viewAW)) {
							_selectedIndex++;
							repaintPaintData(_selectedIndex - 1);
							repaintPaintData(_selectedIndex);
							event.consume();
							return;
						}
					}

					if (first) {
						if (!_linkPaintData.isEmpty()) {
							int i = 0;
							if (0 <= _selectedIndex && _selectedIndex < _linkPaintData.size()) {
								PaintData pd = (PaintData) _linkPaintData.elementAt(_selectedIndex);
								if (pd.y + pd.font.getHeight() <= viewCY + viewAH) {
									i = _selectedIndex;
								}
							}
							// ループを防ぐ
							if (_selectedIndex == _linkPaintData.size()
									&& getViewContentHeight() == viewCY + viewAH) {
								scrollDown();
								return;
							}
							for (; i < _linkPaintData.size(); i++) {
								PaintData pd = (PaintData) _linkPaintData.elementAt(i);
								if (viewCY + viewAH < pd.y + pd.font.getHeight()) {
									break;
								}
								if (i != _selectedIndex) {
									if (viewCY <= pd.y
											&& pd.y + pd.font.getHeight() <= viewCY + viewAH
											&& (viewCX < pd.x + pd.width && pd.x < viewCX + viewAW)) {
										int oldSelectedIndex = _selectedIndex;
										_selectedIndex = i;
										repaintPaintData(_selectedIndex);
										repaintPaintData(oldSelectedIndex);
										event.consume();
										return;
									}
								}
							}
							if (_selectedIndex == _linkPaintData.size() - 1
									&& getViewContentHeight() == viewCY + viewAH) {
								_selectedIndex = _linkPaintData.size();
								repaintPaintData(_selectedIndex - 1);
							}
						}
						if (scrollDown()) {
							viewCX = getViewContentX();
							viewCY = getViewContentY();
							event.consume();
						}
						if (_selectedIndex == _linkPaintData.size() - 1 && _selectedIndex != -1) {
							PaintData pd = (PaintData) _linkPaintData.elementAt(_selectedIndex);
							if (viewCY > pd.y) {
								_selectedIndex++;
								repaintPaintData(_selectedIndex - 1);
							}
						}
						first = false;
					} else {
						if (getViewContentHeight() == viewCY + viewAH
								&& _selectedIndex < _linkPaintData.size() - 1) {
							_selectedIndex = _linkPaintData.size();
							repaintPaintData(_selectedIndex - 1);
						}
						first = true;
					}
				} while (!first);

			} else if (code == KeyEvent.UP || code == KeyEvent.KEY_NUM2) {
				boolean first = true;
				do {
					if (_selectedIndex - 1 >= 0) {
						PaintData pd = (PaintData) _linkPaintData.elementAt(_selectedIndex - 1);
						if (viewCY <= pd.y && pd.y + pd.font.getHeight() <= viewCY + viewAH
								&& (viewCX < pd.x + pd.width && pd.x < viewCX + viewAW)) {
							_selectedIndex--;
							repaintPaintData(_selectedIndex + 1);
							repaintPaintData(_selectedIndex);
							event.consume();
							return;
						}
					}

					if (first) {
						if (!_linkPaintData.isEmpty()) {
							int i = _linkPaintData.size() - 1;
							if (0 <= _selectedIndex && _selectedIndex < _linkPaintData.size()) {
								PaintData pd = (PaintData) _linkPaintData.elementAt(_selectedIndex);
								if (viewCY <= pd.y) {
									i = _selectedIndex;
								}
							}
							// ループを防ぐ
							if (_selectedIndex == -1 && 0 == viewCY) {
								scrollUP();
								return;
							}
							for (; i >= 0; i--) {
								PaintData pd = (PaintData) _linkPaintData.elementAt(i);
								if (viewCY > pd.y) {
									break;
								}
								if (i != _selectedIndex) {
									if (viewCY <= pd.y
											&& pd.y + pd.font.getHeight() <= viewCY + viewAH
											&& (viewCX < pd.x + pd.width && pd.x < viewCX + viewAW)) {
										int oldSelectedIndex = _selectedIndex;
										_selectedIndex = i;
										repaintPaintData(oldSelectedIndex);
										repaintPaintData(_selectedIndex);
										event.consume();
										return;
									}
								}
							}
							if (_selectedIndex == 0 && 0 == viewCY) {
								_selectedIndex = -1;
								repaintPaintData(0);
							}
						}
						if (scrollUP()) {
							viewCX = getViewContentX();
							viewCY = getViewContentY();
							event.consume();
						}
						if (_selectedIndex == 0) {
							PaintData pd = (PaintData) _linkPaintData.elementAt(_selectedIndex);
							if (viewCY + viewAH < pd.y + pd.font.getHeight()) {
								_selectedIndex = -1;
								repaintPaintData(0);
							}
						}
						first = false;
					} else {
						if (0 == viewCY && _selectedIndex > 0) {
							_selectedIndex = _linkPaintData.size();
							repaintPaintData(_selectedIndex - 1);
						}
						first = true;
					}
				} while (!first);
			} else if (code == KeyEvent.LEFT || code == KeyEvent.KEY_NUM4) {
				if (scrollLeft()) {
					event.consume();
				}
			} else if (code == KeyEvent.RIGHT || code == KeyEvent.KEY_NUM6) {
				if (scrollRight()) {
					event.consume();
				}
			}
		}
		super.keyAction(event);
	}

	public Res.Link getViewSelected() {
		int viewCX = getViewContentX();
		int viewCY = getViewContentY();
		int viewCW = getViewAreaWidth();
		int viewCH = getViewAreaHeight();

		if (0 <= _selectedIndex && _selectedIndex < _linkPaintData.size()) {
			PaintData pd = (PaintData) _linkPaintData.elementAt(_selectedIndex);
			if (viewCY <= pd.y && pd.y + pd.font.getHeight() <= viewCY + viewCH
					&& (viewCX < pd.x + pd.width && pd.x < viewCX + viewCW)) {
				return pd.link;
			}
		}
		return null;
	}

	private PaintData getPaintData(Font font, String str, Location p, int width) {
		PaintData pd = new PaintData();
		pd.font = font;
		pd.color = Color.BLACK;
		pd.startX = p.x;
		pd.x = p.x;
		pd.y = p.y;

		StringBuffer sb = new StringBuffer();
		Vector vw = new Vector();
		p.x *= 1000;
		pd.width = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if (c == '\n') {
				vw.addElement(sb.toString());
				sb.delete(0, sb.length());
				p.x = 0;
				p.y += font.getHeight();
				pd.x = 0;
			} else if (c == '\r') {
				if (i + 1 < str.length()) {
					if (str.charAt(i + 1) == '\n') {
						i++;
					}
				}
				vw.addElement(sb.toString());
				sb.delete(0, sb.length());
				p.x = 0;
				p.y += font.getHeight();
				pd.x = 0;
			} else {
				int cwx1000;
				if (aaMode) {
					cwx1000 = AAModeTable.charWidthx1000(font, c);
				} else {
					cwx1000 = font.charWidth(c) * 1000;
				}

				if (p.x == 0 && cwx1000 / 1000 > width) {
					vw.addElement(String.valueOf(c));
					sb.delete(0, sb.length());
					p.y += font.getHeight();
					pd.width = Math.max(cwx1000 / 1000, pd.width);
					continue;
				}
				if ((p.x + cwx1000) / 1000 > width) {
					vw.addElement(sb.toString());
					sb.delete(0, sb.length());
					p.x = 0;
					p.y += font.getHeight();
					pd.x = 0;
				}
				sb.append(c);

				p.x += cwx1000;
				pd.width = Math.max(p.x / 1000, pd.width);
			}
		}
		if (sb.length() > 0) {
			vw.addElement(sb.toString());
		}
		pd.lines = new String[vw.size()];
		vw.copyInto(pd.lines);

		if (pd.lines.length == 1) {
			pd.x = pd.startX;
			pd.width -= pd.startX;
		}
		pd.height = pd.lines.length * font.getHeight();

		p.x /= 1000;

		return pd;
	}

	public void setRes(ThreadHeader threadHeader, Res res) {
		_threadHeader = threadHeader;
		_res = res;

		_paintData.removeAllElements();
		_linkPaintData.removeAllElements();
		_startPaintIndex = 0;
		_selectedIndex = -1;
		cachePrefSize = null;

		validateViewContent();

		setViewContentPosition(0, 0);
		repaintViewContent();
	}

	protected void viewContentSizeChanged() {
		// cachePrefSize = null;
		// revalidateViewContent();
		// repaintViewContent();

		super.viewContentSizeChanged();
	}

	protected void fontChanged() {
		cachePrefSize = null;
		_font = getFont();
		if (_font != null) {
			if (aaMode && _font.getFace() != Font.FACE_MONOSPACE) {
				_font = Font.createFont(Font.FACE_MONOSPACE, _font.getStyle(), _font.getSize());
			}
			_underLineFont = Font.createFont(_font.getFace(), _font.getStyle()
					| Font.STYLE_UNDERLINED, _font.getSize());
		}
		super.fontChanged();
	}

	protected void paintViewContent(Graphics g) {
		Res res = _res;
		if (res == null || _font == null || _paintData.isEmpty()) {
			return;
		}
		Font font = g.getFont();

		g.setFont(_font);

		_startPaintIndex = Math.min(_startPaintIndex, _paintData.size() - 1);
		PaintData se = (PaintData) _paintData.elementAt(_startPaintIndex);
		if (se.y + se.height <= g.getClipY()) {
			for (_startPaintIndex += 1; _startPaintIndex < _paintData.size(); _startPaintIndex++) {
				se = (PaintData) _paintData.elementAt(_startPaintIndex);
				if (se.y + se.height > g.getClipY()) {
					break;
				}
			}
		} else if (g.getClipY() < se.y) {
			for (_startPaintIndex -= 1; _startPaintIndex >= 0; _startPaintIndex--) {
				se = (PaintData) _paintData.elementAt(_startPaintIndex);
				if (g.getClipY() >= se.y) {
					break;
				}
			}
		}
		if (g.getClipY() >= se.y) {
			for (int i = _startPaintIndex - 1; i >= 0; i--) {
				se = (PaintData) _paintData.elementAt(i);
				if (g.getClipY() >= se.y + se.height) {
					break;
				}
				_startPaintIndex--;
			}
		}

		int x = 0;
		for (int i = _startPaintIndex; i < _paintData.size(); i++) {
			PaintData pd = (PaintData) _paintData.elementAt(i);
			if (pd.y >= g.getClipY() + g.getClipHeight()) {
				break;
			}

			x = pd.startX;
			boolean lineDraw = false;
			if (pd.link != null) {
				lineDraw = true;
			}
			g.setColor(pd.color);
			for (int j = 0; j < pd.lines.length; j++) {
				if (pd.y + j * pd.font.getHeight() >= g.getClipY() + g.getClipHeight()) {
					break;
				}
				if (pd.y + (j + 1) * pd.font.getHeight() > g.getClipY()) {
					String str = pd.lines[j];
					if (!str.equals("")) {
						if (lineDraw) {
							int sw;
							if (aaMode) {
								sw = AAModeTable.stringWidth(pd.font, str);
							} else {
								sw = pd.font.stringWidth(str);
							}
							if (pd.linkIndex == _selectedIndex
									&& (getViewContentY() <= pd.y
											&& pd.y + pd.font.getHeight() <= getViewContentY()
													+ getViewAreaHeight() && (getViewContentX() < pd.x
											+ pd.width && pd.x < getViewContentX()
											+ getViewAreaWidth()))) {
								g.setColor(pd.color);
								g.fillRect(x, pd.y + j * pd.font.getHeight(), sw, pd.font
										.getHeight());
								g.setColor(pd.color.getReversalColor());
							} else {
								g.setColor(pd.color);
							}
							// g.drawLine(x, pd.y +
							// pd.font.getBaselinePosition()
							// + j * pd.font.getHeight(), x + sw, pd.y
							// + pd.font.getBaselinePosition() + j
							// * pd.font.getHeight());
						}
						if (pd.link != null) {
							g.setFont(_underLineFont);
						}
						if (aaMode) {
							AAModeTable.drawString(g, str, x, pd.y + j * pd.font.getHeight());
						} else {
							g.drawString(str, x, pd.y + j * pd.font.getHeight());
						}
						if (pd.link != null) {
							g.setFont(_font);
						}
					}
				}
				x = 0;
			}
		}
		g.setFont(font);
	}

	class Location {
		int	x;
		int	y;
	}

	private Dimension	cachePrefSize;
	private int			cacheHintWidth	= DEFAULT - 1;

	public Dimension getContentPreferredSize(int hintWidth, int hintHeight) {
		Font font = _font;
		if (font == null || _threadHeader == null || _res == null) {
			return super.getContentPreferredSize(hintWidth, hintHeight);
		}
		if (cacheHintWidth != hintWidth || cachePrefSize == null) {
			cacheHintWidth = hintWidth;
			if (hintWidth == DEFAULT) {
				hintWidth = Integer.MAX_VALUE;
			}

			_paintData.removeAllElements();
			_linkPaintData.removeAllElements();

			cachePrefSize = new Dimension();

			PaintData pd;

			Location p = new Location();

			pd = getPaintData(font, _res.getNo() + "/" + _threadHeader.getResCount() + ":", p,
					hintWidth);
			addPaintData(pd);

			if (_res.getMailAddress() != null && !_res.getMailAddress().equals("")) {
				pd = getPaintData(font, _res.getName(), p, hintWidth);
				pd.color = nameColor;
				addPaintData(pd);

				pd = getPaintData(font, "[" + _res.getMailAddress() + "]" + '\r', p, hintWidth);
				addPaintData(pd);
			} else {
				pd = getPaintData(font, _res.getName() + '\r', p, hintWidth);
				pd.color = nameColor;
				addPaintData(pd);
			}

			if (_showMethod == RES_SHOW_METHOD_2CH) {
				pd = getPaintData(font, _res.getDate() + " ID:" + _res.getID() + '\r', p, hintWidth);
				addPaintData(pd);
			}
			p.y += (_font.getHeight() / 3);
			cachePrefSize.setHeight(cachePrefSize.getHeight() + (_font.getHeight() / 3));

			int linkIndex = 0;
			for (int i = 0; i < _res.getBlockSize(); i++) {
				Res.TextBlock block = _res.getBlock(i);
				String str = block.getText();

				pd = getPaintData(font, str, p, hintWidth);
				if (block.getType() == Res.URILink.URI_LINK
						|| block.getType() == Res.InnerLink.INNER_LINK) {
					pd.font = _underLineFont;
					pd.link = (Res.Link) block;
					pd.color = Color.BLUE;
					pd.linkIndex = linkIndex++;

					_linkPaintData.addElement(pd);
				}
				addPaintData(pd);
			}

			if (_showMethod == RES_SHOW_METHOD_BODY_PRIORITY) {
				// dはﾀﾞﾐｰ
				PaintData linePd = getPaintData(font, "\rd\r", p, hintWidth);
				addPaintData(linePd);

				p.y -= (_font.getHeight() / 3);
				cachePrefSize.setHeight(cachePrefSize.getHeight() - (_font.getHeight() / 3));

				String header = "ID:" + _res.getID() + "\r投稿日:" + _res.getDate();
				pd = getPaintData(font, header, p, hintWidth);
				addPaintData(pd);

				int lineCount = 1;
				while (cachePrefSize.getWidth() > lineCount++ * font.charWidth('━'))
					;

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < lineCount; i++) {
					sb.append('━');
				}
				linePd.lines[1] = sb.toString();
			}

			/*
			 * for (int i = 0; i < _paintData.size(); i++) { pd = (PaintData)
			 * _paintData.elementAt(i); if (pd.width > cachePrefSize.getWidth()) {
			 * cachePrefSize.setWidth(pd.width); } int lineCount =
			 * pd.lines.length; if (pd.startX > 0) { lineCount--; }
			 * cachePrefSize .setHeight(cachePrefSize.getHeight() + lineCount *
			 * pd.font.getHeight()); }
			 */
		}
		return cachePrefSize.getSize();
	}

	private void addPaintData(PaintData pd) {
		if (cachePrefSize != null) {
			if (pd.width > cachePrefSize.getWidth()) {
				cachePrefSize.setWidth(pd.width);
			}
			int lineCount = pd.lines.length;
			if (pd.startX > 0) {
				lineCount--;
			}
			cachePrefSize.setHeight(cachePrefSize.getHeight() + lineCount * pd.font.getHeight());
		}
		_paintData.addElement(pd);
	}

	public Res getRes() {
		return _res;
	}
}