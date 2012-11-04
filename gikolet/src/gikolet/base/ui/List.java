/*
 * Created on 2005/02/02 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.base.ui;

import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.ListSelectionListener;

import java.util.Vector;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class List extends Scrollable {
	private Vector	_strs;
	private Vector	_paintDatas;
	private int		_selectedIndex	= -1;
	private boolean	valid			= false;
	private boolean	circulate		= true;
	private Vector	_listeners;
	private int		startPaintIndex;
	private Color	_selectColor;

	public List() {
		this(Scrollable.SCROLLBAR_AS_NEEDED, Scrollable.SCROLLBAR_AS_NEEDED);
	}

	public List(int vsbPolicy, int hsbPolicy) {
		super(true, vsbPolicy, hsbPolicy);

		setSelectColor(null);

		_paintDatas = new Vector();
		_strs = new Vector();

		_listeners = new Vector();
	}

	class Element {
		String[]	lines;
		Font		font;
		int			y;
		int			height;
	}

	private void fireListSelected(int index) {
		for (int i = 0; i < _listeners.size(); i++) {
			Object listener = _listeners.elementAt(i);
			if (listener instanceof ListSelectionListener) {
				((ListSelectionListener) listener).selectedChange(index);
			}
		}
	}

	/*
	 * private void fireListAdded(String str, int index) { for (int i = 0; i <
	 * _listeners.getCount(); i++) { EventListener listener =
	 * _listeners.getEventListener(i); if (listener instanceof
	 * ListChangeListener) { ((ListChangeListener) listener).listAdded(str,
	 * index); } } } private void fireListremoved(String str, int index) { for
	 * (int i = 0; i < _listeners.getCount(); i++) { EventListener listener =
	 * _listeners.getEventListener(i); if (listener instanceof
	 * ListChangeListener) { ((ListChangeListener) listener).listRemoved(str,
	 * index); } } } private void fireListContentChanged(String str, int index) {
	 * for (int i = 0; i < _listeners.getCount(); i++) { EventListener listener =
	 * _listeners.getEventListener(i); if (listener instanceof
	 * ListChangeListener) { ((ListChangeListener)
	 * listener).listContentChanged(str, index); } } } public void
	 * addListChangeListener(ListChangeListener listener) {
	 * _listeners.add(listener); } public void
	 * removeListChangeListener(ListChangeListener listener) {
	 * _listeners.remove(listener); }
	 */

	public void addListSelectionListener(ListSelectionListener listener) {
		_listeners.addElement(listener);
	}

	public void removeListSelectionListener(ListSelectionListener listener) {
		_listeners.removeElement(listener);
	}

	public void add(String str) {
		_strs.addElement(str);
		if (valid) {
			Element e = new Element();
			e.font = getFont();
			e.lines = Utilities.split(e.font, str, getViewContentWidth());
			if (!_paintDatas.isEmpty()) {
				Element be = (Element) _paintDatas.lastElement();
				e.y = be.y + be.height;
			}
			e.height = e.lines.length * e.font.getHeight();
			_paintDatas.addElement(e);
		} else {
			validateElement();
		}

		cachePrefSize = null;
		if (getSelectedIndex() == -1) {
			setSelectedIndex(0);
		}
		// fireListAdded(str, _strs.size() - 1);
		revalidateViewContent();
		repaintViewContent();
	}

	public void set(String str, int index) {
		/*
		 * if (index < _strs.size()) { removeAt(index); } insert(str, index);
		 * setSelectedIndex(index);
		 */
		_strs.setElementAt(str, index);
		if (valid) {
			Element adde = new Element();

			adde.font = getFont();
			adde.lines = Utilities.split(adde.font, str, getViewContentWidth());
			adde.height = adde.lines.length * adde.font.getHeight();

			Element olde = (Element) _paintDatas.elementAt(index);
			adde.y = olde.y;

			_paintDatas.setElementAt(adde, index);

			for (int i = index + 1; i < _paintDatas.size(); i++) {
				Element e = (Element) _paintDatas.elementAt(i);
				e.y -= olde.height;
				e.y += adde.height;
			}
		} else {
			validateElement();
		}

		cachePrefSize = null;
		revalidateViewContent();

		// fireListContentChanged(str, _strs.size() - 1);
		repaintViewContent();
	}

	public void addRange(String[] strs) {
		if (valid) {
			for (int i = 0; i < strs.length; i++) {
				_strs.addElement(strs[i]);

				Element e = new Element();
				e.font = getFont();
				e.lines = Utilities.split(e.font, strs[i],
						getViewContentWidth());
				if (i > 0) {
					Element be = (Element) _paintDatas.elementAt(i - 1);
					e.y = be.y + be.height;
				}
				e.height = e.lines.length * e.font.getHeight();
				_paintDatas.addElement(e);
			}
		} else {
			for (int i = 0; i < strs.length; i++) {
				_strs.addElement(strs[i]);
			}
			validateElement();
		}
		cachePrefSize = null;
		if (getSelectedIndex() == -1) {
			setSelectedIndex(0);
		}
		/*
		 * for (int i = 0; i < strs.length; i++) { fireListAdded(strs[i],
		 * _strs.size() - 1); }
		 */
		revalidateViewContent();
		repaintViewContent();
	}

	public void insert(String str, int index) {
		_strs.insertElementAt(str, index);
		if (valid) {
			Element adde = new Element();

			adde.font = getFont();
			adde.lines = Utilities.split(adde.font, str, getViewContentWidth());
			adde.height = adde.lines.length * adde.font.getHeight();

			if (index != 0) {
				Element ew = (Element) _paintDatas.elementAt(index - 1);
				adde.y = ew.y + ew.height;
			}
			_paintDatas.insertElementAt(adde, index);
			for (int i = index + 1; i < _paintDatas.size(); i++) {
				Element e = (Element) _paintDatas.elementAt(i);
				e.y += adde.height;
			}
		} else {
			validateElement();
		}

		cachePrefSize = null;
		if (getSelectedIndex() == -1) {
			setSelectedIndex(0);
		}
		// fireListAdded(str, index);
		revalidateViewContent();
		repaintViewContent();
	}

	public void removeAt(int index) {
		// String reStr = (String) _strs.elementAt(index);
		_strs.removeElementAt(index);
		if (valid) {
			Element re = (Element) _paintDatas.elementAt(index);
			int reh = re.height;

			_paintDatas.removeElementAt(index);
			for (int i = index; i < _paintDatas.size(); i++) {
				Element e = (Element) _paintDatas.elementAt(i);
				e.y -= reh;
			}
		} else {
			validateElement();
		}

		cachePrefSize = null;
		if (getSelectedIndex() == getListSize()) {
			setSelectedIndex(getListSize() - 1);
		}
		// fireListremoved(reStr, index);
		revalidateViewContent();
		repaintViewContent();
	}

	public void clear() {
		String[] strs = new String[_strs.size()];
		_strs.copyInto(strs);

		_strs.removeAllElements();
		valid = false;
		validateElement();

		cachePrefSize = null;
		setSelectedIndex(-1);

		/*
		 * for (int i = 0; i < strs.length; i++) { fireListremoved(strs[i],
		 * _strs.size() - 1); }
		 */

		revalidateViewContent();
		repaintViewContent();
	}

	public void setSelectColor(Color color) {
		if (color == null) {
			color = new Color(0x0316AC5);
		}
		_selectColor = color;

		repaintViewContent();
	}

	public Color getSelectColor() {
		return _selectColor;
	}

	protected void viewContentSizeChanged() {
		valid = false;
		validateElement();
		super.viewContentSizeChanged();
	}

	private void validateElement() {
		Font font = getFont();
		int width = getViewContentWidth();
		if (font != null && width > 0 && getViewContentHeight() > 0) {
			if (!valid) {
				create(font, width);
				valid = true;
			}
		} else {
			valid = false;
		}
	}

	protected void fontChanged() {
		valid = false;
		validateElement();

		cachePrefSize = null;

		revalidateViewContent();
		repaintViewContent();

		super.fontChanged();
	}

	protected void keyAction(KeyEvent event) {
		int keyActionType = event.getKeyActionType();
		int keyCode = event.getKeyCode();

		if (keyActionType != KeyEvent.RELEASED) {
			if (keyCode == KeyEvent.UP || keyCode == KeyEvent.KEY_NUM2) {
				selectPreviousIndex();
				event.consume();
			} else if (keyCode == KeyEvent.DOWN || keyCode == KeyEvent.KEY_NUM8) {
				selectNextIndex();
				event.consume();
			} else if (keyCode == KeyEvent.LEFT || keyCode == KeyEvent.KEY_NUM4) {
				// selectPrevPageIndex();
				if (getHorizontalScrollBarPolicy() != SCROLLBAR_NEVER
						&& scrollLeft()) {
					event.consume();
				}
			} else if (keyCode == KeyEvent.RIGHT
					|| keyCode == KeyEvent.KEY_NUM6) {
				// selectNextPageIndex();
				if (getHorizontalScrollBarPolicy() != SCROLLBAR_NEVER
						&& scrollRight()) {
					event.consume();
				}
			}
		}
		super.keyAction(event);
	}

	/*
	 * protected void selectPrevPageIndex() { if (_strs.isEmpty()) { return; }
	 * validateElement(); Rectangle viewRect = getViewRect();
	 * scrollRectToVisible(0, viewRect.y - viewRect.height, viewRect.width,
	 * viewRect.height); int startIndex = _selectedIndex; Element se = null; for
	 * (startIndex -= 1; startIndex >= 0; startIndex--) { se = (Element)
	 * _paintDatas.elementAt(startIndex); if (se.y >= viewRect.y) { break; } }
	 * if (se != null) { setSelectedIndex(startIndex); } } protected void
	 * selectNextPageIndex() { if (_strs.isEmpty()) { return; }
	 * validateElement(); Rectangle viewRect = getViewRect(); // int height =
	 * getHeight(); int startIndex = _selectedIndex; if (startIndex < 0) {
	 * startIndex = 0; } else if (_strs.size() - 1 < startIndex) { return; }
	 * Element se = null; for (startIndex += 1; startIndex < _paintDatas.size();
	 * startIndex++) { se = (Element) _paintDatas.elementAt(startIndex); if
	 * (se.y + se.height >= viewRect.y + viewRect.height) { break; } } if (se !=
	 * null) { setSelectedIndex(startIndex); ensureIndexIsVisible(startIndex); } }
	 */

	protected void selectPreviousIndex() {
		int s = getSelectedIndex();
		if (s != -1) {
			if (s - 1 < 0) {
				if (isCirculate()) {
					s = getListSize() - 1;
				} else {
					return;
				}
			} else {
				s--;
			}
			setSelectedIndex(s);
			ensureIndexIsVisible(s);
		}
	}

	protected void selectNextIndex() {
		int s = getSelectedIndex();
		if (s != -1) {
			if (s + 1 >= getListSize()) {
				if (isCirculate()) {
					s = 0;
				} else {
					return;
				}
			} else {
				s++;
			}
			setSelectedIndex(s);
			ensureIndexIsVisible(s);
		}
	}

	public boolean isCirculate() {
		return this.circulate;
	}

	public void setCirculate(boolean circulate) {
		this.circulate = circulate;
	}

	public void ensureIndexIsVisible(int index) {
		if (0 > index || index >= getListSize()) {
			throw new IndexOutOfBoundsException("index is illegal.");
		}
		validateElement();
		if (valid) {
			Element e = (Element) _paintDatas.elementAt(index);
			int x = 0;
			int y = e.y;
			// int width = font.stringWidth(get(index));
			int width = getViewContentWidth();
			int height = e.height;

			scrollRectToVisible(x, y, width, height);
		}
	}

	public void setSelectedIndex(int index) {
		if (-1 > index || index >= getListSize()) {
			throw new IndexOutOfBoundsException("index is illegal.");
		}
		if (_selectedIndex == index) {
			return;
		}
		int oldIndex = _selectedIndex;
		_selectedIndex = index;

		Font font = getFont();
		if (font == null) {
			return;
		}
		validateElement();

		if (valid) {
			if (index != -1 && oldIndex != -1 && oldIndex < _paintDatas.size()) {
				Element e = (Element) _paintDatas.elementAt(index);

				int x = 0;
				int minY = e.y;
				int width = getViewContentWidth();
				int maxY = minY + e.height;

				Element oe = (Element) _paintDatas.elementAt(oldIndex);
				minY = Math.min(minY, oe.y);
				maxY = Math.max(maxY, oe.y + oe.height);

				repaintViewContent(x, minY, width, maxY - minY);
			}
		} else {
			repaintViewContent();
		}
		fireListSelected(_selectedIndex);
	}

	public int getListSize() {
		return _strs.size();
	}

	private void create(Font font, int width) {
		_paintDatas.removeAllElements();

		int y = 0;
		for (int i = 0; i < _strs.size(); i++) {
			String str = (String) _strs.elementAt(i);

			Element e = new Element();

			e.font = font;
			e.lines = Utilities.split(e.font, str, width);
			e.y = y;
			e.height = e.lines.length * e.font.getHeight();

			y = e.y + e.height;

			_paintDatas.addElement(e);
		}
	}

	private Dimension getSize(Font font, int width) {
		int w = 0;
		int h = 0;

		// System.out.println(width);
		if (width == DEFAULT) {
			width = Integer.MAX_VALUE;
		} else {
			w = width;
		}

		Dimension d = new Dimension();
		for (int i = 0; i < _strs.size(); i++) {
			String str = (String) _strs.elementAt(i);

			setHeight(d, font, str, width);
			w = Math.max(w, d.getWidth());
			h += d.getHeight();
			// h += trimStr(font, str, width).length * font.getHeight();
		}
		d.setSize(w, h);

		return d;
	}

	private void setHeight(Dimension d, Font font, String str, int width) {
		int x = 0;
		int w = 0;
		int h = font.getHeight();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			int cw = font.charWidth(c);

			if (x == 0 && cw > width) {
				continue;
			}
			if (x + cw > width) {
				w = Math.max(w, x);
				h += font.getHeight();
				x = 0;
			}
			x += cw;
		}
		d.setSize(Math.max(w, x), h);
	}

	/*
	 * private String[] trimStr(Font font, String str, int width) { if
	 * (str.length() == 0) { return new String[] { "" }; } Vector vw = new
	 * Vector(); StringBuffer sb = new StringBuffer(); int x = 0; for (int i =
	 * 0; i < str.length(); i++) { char c = str.charAt(i); int cw =
	 * font.charWidth(c); //if (x + cw >= width) { // if (sb.length() == 0) { //
	 * vw.addElement(String.valueOf(c)); // continue; // } //
	 * vw.addElement(sb.toString()); // sb.delete(0, sb.length()); // x = 0; // }
	 * if (x == 0 && cw > width) { vw.addElement(String.valueOf(c)); continue; }
	 * if (x + cw > width) { vw.addElement(sb.toString()); sb.delete(0,
	 * sb.length()); x = 0; } sb.append(c); x += cw; } if (sb.length() > 0) {
	 * vw.addElement(sb.toString()); } String[] lines = new String[vw.size()];
	 * vw.copyInto(lines); return lines; }
	 */

	public int getSelectedIndex() {
		return _selectedIndex;
	}

	protected void paintViewContent(Graphics g) {
		if (_strs.size() == 0) {
			return;
		}
		validateElement();
		if (!valid) {
			return;
		}

		int width = getViewContentWidth();

		startPaintIndex = Math.min(startPaintIndex, _paintDatas.size() - 1);
		Element se = (Element) _paintDatas.elementAt(startPaintIndex);
		if (se.y + se.height <= g.getClipY()) {
			for (startPaintIndex += 1; startPaintIndex < _paintDatas.size(); startPaintIndex++) {
				se = (Element) _paintDatas.elementAt(startPaintIndex);
				if (se.y + se.height > g.getClipY()) {
					break;
				}
			}
		} else if (g.getClipY() < se.y) {
			for (startPaintIndex -= 1; startPaintIndex >= 0; startPaintIndex--) {
				se = (Element) _paintDatas.elementAt(startPaintIndex);
				if (g.getClipY() >= se.y) {
					break;
				}
			}
		}
		for (int i = startPaintIndex; i < _paintDatas.size(); i++) {
			Element e = (Element) _paintDatas.elementAt(i);
			if (e.y >= g.getClipY() + g.getClipHeight()) {
				break;
			}
			if (i == _selectedIndex) {
				g.setColor(_selectColor);
				g.fillRect(0, e.y, width, e.height);
				g.setColor(Color.WHITE);
			} else if (i % 2 == 0) {
				g.setColor(new Color(0xDCEAF5));
				g.fillRect(0, e.y, width, e.height);
				g.setColor(Color.BLACK);
			} else {
				g.setColor(Color.BLACK);
			}

			for (int j = 0; j < e.lines.length; j++) {
				if (e.y + j * e.font.getHeight() >= g.getClipY()
						+ g.getClipHeight()) {
					break;
				}
				if (e.y + (j + 1) * e.font.getHeight() > g.getClipY()) {
					String str = e.lines[j];
					g.drawString(str, 0, e.y + j * e.font.getHeight());
				}
			}
		}
	}

	private Dimension	cachePrefSize;
	private int			cacheHintWidth	= DEFAULT - 1;

	public Dimension getContentPreferredSize(int hintWidth, int hintHeight) {
		Font font = getFont();
		if (font == null) {
			return super.getContentPreferredSize(hintWidth, hintHeight);
		}
		if (cacheHintWidth != hintWidth || cachePrefSize == null) {
			cacheHintWidth = hintWidth;
			cachePrefSize = getSize(font, hintWidth);
		}
		return cachePrefSize.getSize();
	}
}