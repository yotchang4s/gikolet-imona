/*
 * Created on 2005/02/20 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.doja;

import gikolet.base.ui.CancelException;
import gikolet.base.ui.Font;
import gikolet.base.ui.MenuItem;
import gikolet.base.ui.Rectangle;
import gikolet.base.ui.events.KeyEvent;

import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.Display;
import com.nttdocomo.ui.Frame;
import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.Panel;
import com.nttdocomo.ui.PhoneSystem;
import com.nttdocomo.ui.SoftKeyListener;
import com.nttdocomo.ui.TextBox;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
class DoJaDisplay extends gikolet.base.ui.Display {
	private Frame	_current;
	DoJaMWTCanvas	_canvas;
	private Command	_softKey1Label;
	private Command	_softKey2Label;

	private Command	_menuCmd;
	private Command	_popMenuCmd;
	private Command	_spaceMenuCmd;

	class Command {
		String	label;

		Command(String label) {
			this.label = label;
		}
	}

	public DoJaDisplay() {
		super();

		PhoneSystem.setAttribute(PhoneSystem.DEV_KEYPAD, 1);
		
		_canvas = new DoJaMWTCanvas();

		_menuCmd = new Command("");
		_popMenuCmd = new Command("");
		_spaceMenuCmd = new Command("");

		_canvas.setSoftLabel(Frame.SOFT_KEY_1, _menuCmd.label);
		_canvas.setSoftLabel(Frame.SOFT_KEY_2, _popMenuCmd.label);

		Font.setDefaultFont(createFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
				Font.SIZE_MEDIUM));

		_current = _canvas;
	}

	protected Font createFont(int face, int style, int size) {
		return new DoJaFont(face, style, size);
	}

	public int getScreenWidth() {
		return _canvas.getWidth();
	}

	public int getScreenHeight() {
		return _canvas.getHeight();
	}

	private final Object	IME_LOCK	= new Object();
	private String			_inputText;

	public String imeOn(String title, final String text, int mode)
			throws CancelException {
		if (text == null) {
			throw new NullPointerException();
		}
		final int displayMode;
		final int inputMode;
		switch (mode) {
			case ANY:
				displayMode = TextBox.DISPLAY_ANY;
				inputMode = TextBox.KANA;
				break;
			case PASSWORD:
				displayMode = TextBox.DISPLAY_ANY;
				inputMode = TextBox.ALPHA;
				break;
			case NUMBER:
				displayMode = TextBox.DISPLAY_ANY;
				inputMode = TextBox.NUMBER;
				break;
			default:
				throw new IllegalArgumentException("mode is illegal.");
		}

		Panel panel = new Panel();
		final TextBox textBox = new TextBox(text, Integer.MAX_VALUE,
				Integer.MAX_VALUE, displayMode);

		textBox.setInputMode(inputMode);

		panel.setSoftLabel(Frame.SOFT_KEY_1, "OK");
		panel.setSoftLabel(Frame.SOFT_KEY_2, "CANCEL");

		panel.setSoftKeyListener(new SoftKeyListener() {
			public void softKeyPressed(int softKey) {
				if (softKey == Frame.SOFT_KEY_1) {
					_inputText = textBox.getText();
				} else if (softKey == Frame.SOFT_KEY_2) {
					_inputText = null;
				}
				_current = _canvas;
				setNativeCurrent();
				synchronized (IME_LOCK) {
					IME_LOCK.notifyAll();
				}
			}

			public void softKeyReleased(int softKey) {
			}
		});
		panel.setTitle(title);
		panel.add(textBox);

		textBox.requestFocus();

		_inputText = text;

		_current = panel;
		setNativeCurrent();

		synchronized (IME_LOCK) {
			while (_current == panel) {
				try {
					IME_LOCK.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		if (_inputText == null) {
			throw new CancelException();
		}

		return _inputText;

		/*
		 * new Thread() { public void run() { try { _canvas.imeOn(text,
		 * displayMode, inputMode); } catch (Exception e) { return text; } }
		 * }.start(); synchronized (IME_LOCK) { _inputText = null; while
		 * (_inputText == null) { try { IME_LOCK.wait(); } catch
		 * (InterruptedException e) { } } } return _inputText;
		 */
	}

	private void setNativeCurrent() {
		Display.setCurrent(_current);
	}

	private void processIMEEvent(String text) {
		synchronized (IME_LOCK) {
			_inputText = text;
			IME_LOCK.notifyAll();
		}
	}

	protected synchronized void setMenuItemNavi(MenuItem menuItem) {
		if (menuItem != null) {
			_menuCmd = new Command(menuItem.getLabel());
			_softKey1Label = _menuCmd;
			_canvas.setSoftLabel(Frame.SOFT_KEY_1, _softKey1Label.label);
		} else {
			_softKey1Label = _spaceMenuCmd;
			_canvas.setSoftLabel(Frame.SOFT_KEY_1, _softKey1Label.label);
		}

	}

	protected synchronized void setPopupMenuItemNavi(MenuItem menuItem) {
		if (menuItem != null) {
			_popMenuCmd = new Command(menuItem.getLabel());
			_softKey2Label = _popMenuCmd;
			_canvas.setSoftLabel(Frame.SOFT_KEY_2, _softKey2Label.label);
		} else {
			_softKey2Label = _spaceMenuCmd;
			_canvas.setSoftLabel(Frame.SOFT_KEY_2, _softKey2Label.label);
		}
	}

	protected void fireApplicationDestroy() {
		super.fireApplicationDestroy();
	}

	protected void fireApplicationPause() {
		super.fireApplicationPause();
	}

	protected void fireApplicationStart() {
		super.fireApplicationStart();
		Display.setCurrent(_current);
	}

	public void destroy() {
		fireApplicationDestroyCore();
		GikoletIApplication.getIApplication().terminate();
	}

	protected void paint(int x, int y, int width, int height, boolean direct) {
		// if (direct) {
		Graphics g = _canvas.getGraphics();
		g.lock();
		g.setClip(0, 0, getScreenWidth(), getScreenHeight());
		paint(new DoJaGraphics(g, new Rectangle(x, y, width, height)));
		g.unlock(true);
		// } else {
		// _canvas.repaint(x, y, width, height);
		// }
	}

	class DoJaMWTCanvas extends Canvas implements Runnable {
		private long	_pressedKeyTime;
		private boolean	_repeat;
		private int		_repeatKeyCode;
		private Object	_repeatKeyLock	= new Object();

		public DoJaMWTCanvas() {
			DoJaGraphics.screenWidth = getWidth();
			DoJaGraphics.screenHeight = getHeight();

			new Thread(this).start();
		}

		public void paint(Graphics g) {
			DoJaDisplay.this.paint(0, 0, getWidth(), getHeight(), true);
		}

		public void processIMEEvent(int type, String text) {
			DoJaDisplay.this.processIMEEvent(text);
		}

		public void processEvent(int type, final int param) {
			if (param == Display.KEY_SOFT1 || param == Display.KEY_SOFT2) {
				if (type == Display.KEY_PRESSED_EVENT) {
					invoke(new Runnable() {
						public void run() {
							if (param == Display.KEY_SOFT1) {
								if (_softKey1Label == _menuCmd) {
									showMenuDialog();
								}
							} else if (param == Display.KEY_SOFT2) {
								if (_softKey2Label == _popMenuCmd) {
									showPopupMenuDialog();
								}
							}
						}
					});
				}
			} else {
				int mwtType;
				int mwtCode;
				if (type == Display.KEY_PRESSED_EVENT) {
					mwtType = KeyEvent.PRESSED;
					mwtCode = changeEventParamToKeyEventCode(param);
					getEventQueue().keyAction(mwtType, mwtCode);

					synchronized (_repeatKeyLock) {
						_repeatKeyCode = mwtCode;
						_repeat = true;
						_pressedKeyTime = System.currentTimeMillis();
						_repeatKeyLock.notifyAll();
					}
				} else if (type == Display.KEY_RELEASED_EVENT) {
					mwtType = KeyEvent.RELEASED;
					mwtCode = changeEventParamToKeyEventCode(param);
					getEventQueue().keyAction(mwtType, mwtCode);

					synchronized (_repeatKeyLock) {
						if (mwtCode == _repeatKeyCode) {
							_repeat = false;
						}
					}
				}
			}
		}

		public void run() {
			long pressedKeyTime = 0;
			while (true) {
				int sleepTime = 130;
				synchronized (_repeatKeyLock) {
					while (!_repeat) {
						try {
							_repeatKeyLock.wait();
							sleepTime = 450;
						} catch (InterruptedException e) {
						}
					}
					pressedKeyTime = _pressedKeyTime;
				}
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
				}

				synchronized (_repeatKeyLock) {
					if (_repeat && pressedKeyTime == _pressedKeyTime) {
						getEventQueue().keyAction(KeyEvent.REPEATED,
								_repeatKeyCode);
					}
				}
			}
		}

		private int changeEventParamToKeyEventCode(int param) {
			switch (param) {
				case Display.KEY_UP:
					return KeyEvent.UP;
				case Display.KEY_DOWN:
					return KeyEvent.DOWN;
				case Display.KEY_LEFT:
					return KeyEvent.LEFT;
				case Display.KEY_RIGHT:
					return KeyEvent.RIGHT;
				case Display.KEY_SELECT:
					return KeyEvent.ENTER;
				case Display.KEY_0:
					return KeyEvent.KEY_NUM0;
				case Display.KEY_1:
					return KeyEvent.KEY_NUM1;
				case Display.KEY_2:
					return KeyEvent.KEY_NUM2;
				case Display.KEY_3:
					return KeyEvent.KEY_NUM3;
				case Display.KEY_4:
					return KeyEvent.KEY_NUM4;
				case Display.KEY_5:
					return KeyEvent.KEY_NUM5;
				case Display.KEY_6:
					return KeyEvent.KEY_NUM6;
				case Display.KEY_7:
					return KeyEvent.KEY_NUM7;
				case Display.KEY_8:
					return KeyEvent.KEY_NUM8;
				case Display.KEY_9:
					return KeyEvent.KEY_NUM9;
				case Display.KEY_POUND:
					return KeyEvent.KEY_POUND;
				case Display.KEY_ASTERISK:
					return KeyEvent.KEY_STAR;
				case Display.KEY_CLEAR:
					return KeyEvent.CLEAR;
				default:
					return KeyEvent.KEY_NON;
			}
		}
	}
}