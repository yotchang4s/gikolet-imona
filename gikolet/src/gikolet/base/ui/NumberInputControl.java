/*
 * Created on 2005/02/04 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.base.ui;

import gikolet.base.ui.events.KeyEvent;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class NumberInputControl extends Control {
	private int	_maxRange;
	private int	_minRange;
	private int	_figures;
	private int	_inputFigure	= 1;
	private int	_number;

	public NumberInputControl(int minRange, int maxRange, int number) {
		super(true);

		setBackColor(Color.WHITE);

		setRange(minRange, maxRange);
		setNumber(number);
	}

	protected void keyAction(KeyEvent event) {
		int type = event.getKeyActionType();
		int code = event.getKeyCode();
		if (type != KeyEvent.RELEASED) {
			if (code == KeyEvent.DOWN) {
				int num = _number - pow(10, _inputFigure - 1);
				if (num >= _minRange) {
					_number = num;
				} else {
					_number = _minRange;
				}
			} else if (code == KeyEvent.UP) {
				int num = _number + pow(10, _inputFigure - 1);
				if (num <= _maxRange) {
					_number = num;
				} else {
					_number = _maxRange;
				}
			} else if (code == KeyEvent.LEFT) {
				int n = _inputFigure + 1;
				if (n <= _figures) {
					_inputFigure = n;
				}
			} else if (code == KeyEvent.RIGHT) {
				int n = _inputFigure - 1;
				if (n >= 1) {
					_inputFigure = n;
				}
			}
		}
		repaint();
		super.keyAction(event);
	}

	private int pow(int a, int b) {
		int n = 1;
		for (int i = 0; i < b; i++) {
			n *= a;
		}
		return n;
	}

	public void setRange(int minRange, int maxRange) {
		if (minRange > maxRange) {
			throw new IndexOutOfBoundsException("minRange > maxRange");
		}
		_maxRange = maxRange;
		_minRange = minRange;

		String min = String.valueOf(Math.abs(minRange));
		String max = String.valueOf(Math.abs(maxRange));
		_figures = Math.max(min.length(), max.length());

		String numStr = String.valueOf(Math.abs(_number));
		if (numStr.length() > _figures) {
			numStr.substring(numStr.length() - _figures, numStr.length());
			_number = Integer.parseInt(numStr) * ((_number < 0) ? -1 : 1);
		}
		if (_figures < _inputFigure) {
			_inputFigure = _figures;
		}
		revalidate();
		repaint();
	}

	public int getMaxRange() {
		return _maxRange;
	}

	public int getMinRange() {
		return _minRange;
	}

	public int getNumber() {
		return _number;
	}

	public void setNumber(int number) {
		if (number < _minRange || _maxRange < number) {
			throw new IndexOutOfBoundsException("number is illegal bounds.");
		}
		int oldNum = _number;
		_number = number;
		if ((oldNum < 0 && 0 <= _number) || (_number < 0 && 0 <= oldNum)) {
			revalidate();
		}
		repaint();
	}

	public int getFigures() {
		return _figures;
	}

	protected void paintControl(Graphics g) {
		Dimension d = getPreferredSize(DEFAULT, DEFAULT);
		Font font = g.getFont();

		boolean focus = getFocused();

		int baseX = (getWidth() - d.getWidth()) / 2;
		int baseY = (getHeight() - d.getHeight()) / 2;

		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		int x = baseX + 2;
		int y = baseY + 1;

		int number = getNumber();

		if (number < 0) {
			g.drawChar('-', x, y + 4);
			x += font.charWidth('-');
		}
		String numStr = String.valueOf(Math.abs(number));
		for (int i = 0; i < _figures; i++) {
			char c;
			int si = i - (_figures - numStr.length());
			if (si >= 0) {
				c = numStr.charAt(si);
			} else {
				c = '0';
			}
			int cw = font.charWidth(c);
			if (i == _figures - _inputFigure && focus) {
				if (_number != _maxRange) {
					paintArrow(g, true, x + cw / 2, y);
				}
				if (_number != _minRange) {
					paintArrow(g, false, x + cw / 2, y + 4 + 1
							+ font.getHeight());
				}
			}
			g.drawChar(c, x, y + 4);
			x += cw;
		}
	}

	private void paintArrow(Graphics g, boolean up, int x, int y) {
		if (up) {
			g.drawLine(x, y, x, y);
			g.drawLine(x - 1, y + 1, x + 1, y + 1);
			g.drawLine(x - 2, y + 2, x + 2, y + 2);
		} else {
			g.drawLine(x - 2, y, x + 2, y);
			g.drawLine(x - 1, y + 1, x + 1, y + 1);
			g.drawLine(x, y + 2, x, y + 2);
		}
	}

	public Dimension getPreferredSize(int hintWidth, int hintHeight) {
		Font font = getFont();
		if (font != null) {
			int width = 4;
			int height = font.getHeight() + 10;
			String numStr = String.valueOf(Math.abs(_number));
			for (int i = 0; i < _figures; i++) {
				char c;
				int si = i - (_figures - numStr.length());
				if (si >= 0) {
					c = numStr.charAt(si);
				} else {
					c = '0';
				}
				int cw = font.charWidth(c);
				width += cw;
			}
			if (_number < 0) {
				width += font.charWidth('-');
			}
			return new Dimension(width, height);
		}
		return super.getPreferredSize(hintWidth, hintHeight);
	}
}