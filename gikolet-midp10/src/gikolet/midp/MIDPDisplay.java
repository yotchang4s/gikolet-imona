/*
 * Created on 2005/02/28 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.midp;

import gikolet.base.ui.CancelException;
import gikolet.base.ui.Display;
import gikolet.base.ui.Font;
import gikolet.base.ui.MenuItem;
import gikolet.base.ui.events.KeyEvent;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class MIDPDisplay extends Display {
	private Displayable	_current;
	private MWTCanvas	_canvas;
	private TextBox		_textBox;

	private Command		_menuCmd;
	private Command		_popMenuCmd;

	private Command		_inputOkCmd;
	private Command		_inputCancelCmd;

	public MIDPDisplay() {
		super();

		_canvas = new MWTCanvas();
		_textBox = new TextBox("���", "", Short.MAX_VALUE, TextField.ANY);

		_menuCmd = new Command("MENU", Command.SCREEN, 0);
		_popMenuCmd = new Command("MENU", Command.SCREEN, 1);

		_inputOkCmd = new Command("OK", Command.SCREEN, 0);
		_inputCancelCmd = new Command("CANCEL", Command.SCREEN, 1);

		CommandListener listener = new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				MIDPDisplay.this.commandAction(c, d);
			}
		};

		_canvas.setCommandListener(listener);
		_textBox.setCommandListener(listener);
		_textBox.addCommand(_inputOkCmd);
		_textBox.addCommand(_inputCancelCmd);

		javax.microedition.lcdui.Font midpFont = javax.microedition.lcdui.Font.getDefaultFont();

		Font.setDefaultFont(MIDPFont.getFont(midpFont));

		setNativeCurrent(null);
	}

	private String				_inputText;

	private final static Object	IME_LOCK	= new Object();

	public String imeOn(String title, String text, int mode) throws CancelException {
		if (text == null) {
			throw new NullPointerException("text is null.");
		}
		switch (mode) {
			case ANY:
				mode = TextField.ANY;
				break;
			case PASSWORD:
				mode = TextField.PASSWORD;
				break;
			case NUMBER:
				mode = TextField.NUMERIC;
				break;
			default:
				throw new IllegalArgumentException("mode is illegal.");
		}
		_textBox.setTitle(title);
		_textBox.setString(text);
		_textBox.setConstraints(mode);

		_inputText = text;

		// _current = _textBox;
		setNativeCurrent(_textBox);

		synchronized (IME_LOCK) {
			while (_current == _textBox) {
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
	}

	protected void setNativeCurrent(Displayable current) {
		if (current == null) {
			current = _canvas;
		}
		if (current instanceof Alert) {
			javax.microedition.lcdui.Display.getDisplay(GikoletMIDlet.getMIDlet()).setCurrent(
					(Alert) current, _current);
		} else {
			_current = current;
			javax.microedition.lcdui.Display.getDisplay(GikoletMIDlet.getMIDlet()).setCurrent(
					current);
		}
	}

	protected void fireApplicationStart() {
		setNativeCurrent(_current);
		super.fireApplicationStart();
	}

	protected void fireApplicationPause() {
		super.fireApplicationPause();
	}

	protected void fireApplicationDestroy() {
		super.fireApplicationDestroy();
	}

	public Font createFont(int face, int style, int size) {
		return new MIDPFont(face, style, size);
	}

	public void destroy() {
		fireApplicationDestroyCore();
		GikoletMIDlet.getMIDlet().notifyDestroyed();
	}

	public int getScreenWidth() {
		return _canvas.getWidth();
	}

	public int getScreenHeight() {
		return _canvas.getHeight();
	}

	protected void setMenuItemNavi(MenuItem menuItem) {
		_canvas.removeCommand(_menuCmd);
		// _canvas.removeCommand(_spaceMenuCmd);

		if (menuItem != null /* && !isPopupMenuDialogShowing() */) {
			_menuCmd = new Command(menuItem.getLabel(), Command.SCREEN, 0);
			_canvas.addCommand(_menuCmd);
		} /*
			 * else { _canvas.addCommand(_spaceMenuCmd); }
			 */
	}

	protected void setPopupMenuItemNavi(MenuItem menuItem) {
		_canvas.removeCommand(_popMenuCmd);

		if (menuItem != null /* && !isMenuDialogShowing() */) {
			_popMenuCmd = new Command(menuItem.getLabel(), Command.SCREEN, 1);
			_canvas.addCommand(_popMenuCmd);
		}
	}

	protected void paint(int x, int y, int width, int height, boolean direct) {
		_canvas._g.setClip(0, 0, getScreenWidth(), getScreenHeight());
		_canvas._g.clipRect(x, y, width, height);
		MIDPDisplay.this.paint(new MIDPGraphics(_canvas._g));
		_canvas.repaint(x, y, width, height);
		if (direct) {
			_canvas.serviceRepaints();
		}
	}

	private void commandAction(final Command command, Displayable displayable) {
		if (displayable == _canvas) {
			invoke(new Runnable() {
				public void run() {
					if (command == _menuCmd) {
						showMenuDialog();
					} else if (command == _popMenuCmd) {
						showPopupMenuDialog();
					}
				}
			});
		} else if (displayable == _textBox) {
			if (command == _inputOkCmd) {
				_inputText = _textBox.getString();
			} else if (command == _inputCancelCmd) {
				_inputText = null;
			}
			setNativeCurrent(null);
			synchronized (IME_LOCK) {
				IME_LOCK.notifyAll();
			}
		}
	}

	protected int changeMIDPCanvasCodeToKeyEventCode(int keyCode) {
		return KeyEvent.KEY_NON;
	}

	class MWTCanvas extends Canvas {
		private Graphics	_g;
		private Image		_image;

		MWTCanvas() {
			_image = Image.createImage(getWidth(), getHeight());
			_g = _image.getGraphics();

			MIDPGraphics.screenWidth = getWidth();
			MIDPGraphics.screenHeight = getHeight();
		}

		protected void keyPressed(int keyCode) {
			int code = changeCanvasCodeToKeyEventCode(keyCode);
			if (code != KeyEvent.KEY_NON) {
				getEventQueue().keyAction(KeyEvent.PRESSED, code);
			}
		}

		protected void keyRepeated(int keyCode) {
			int code = changeCanvasCodeToKeyEventCode(keyCode);
			if (code != KeyEvent.KEY_NON) {
				getEventQueue().keyAction(KeyEvent.REPEATED, code);
			}
		}

		protected void keyReleased(int keyCode) {
			int code = changeCanvasCodeToKeyEventCode(keyCode);
			if (code != KeyEvent.KEY_NON) {
				getEventQueue().keyAction(KeyEvent.RELEASED, code);
			}
		}

		private int changeCanvasCodeToKeyEventCode(int keyCode) {
			int c = changeMIDPCanvasCodeToKeyEventCode(keyCode);
			if (c != KeyEvent.KEY_NON) {
				return c;
			}
			switch (keyCode) {
				case KEY_NUM0:
					return KeyEvent.KEY_NUM0;
				case KEY_NUM1:
					return KeyEvent.KEY_NUM1;
				case KEY_NUM2:
					return KeyEvent.KEY_NUM2;
				case KEY_NUM3:
					return KeyEvent.KEY_NUM3;
				case KEY_NUM4:
					return KeyEvent.KEY_NUM4;
				case KEY_NUM5:
					return KeyEvent.KEY_NUM5;
				case KEY_NUM6:
					return KeyEvent.KEY_NUM6;
				case KEY_NUM7:
					return KeyEvent.KEY_NUM7;
				case KEY_NUM8:
					return KeyEvent.KEY_NUM8;
				case KEY_NUM9:
					return KeyEvent.KEY_NUM9;
				case KEY_POUND:
					return KeyEvent.KEY_POUND;
				case KEY_STAR:
					return KeyEvent.KEY_STAR;
			}
			int action;
			try {
				action = getGameAction(keyCode);
			} catch (Exception e) {
				action = 0;
			}
			if (action != 0) {
				switch (action) {
					case UP:
						return KeyEvent.UP;
					case DOWN:
						return KeyEvent.DOWN;
					case LEFT:
						return KeyEvent.LEFT;
					case RIGHT:
						return KeyEvent.RIGHT;
					case FIRE:
						return KeyEvent.ENTER;
				}
			}
			return KeyEvent.KEY_NON;
		}

		protected void paint(Graphics g) {
			g.drawImage(_image, 0, 0, Graphics.TOP | Graphics.LEFT);
		}

		/*
		 * private javax.microedition.lcdui.Graphics g; protected void
		 * paint(final javax.microedition.lcdui.Graphics g) { this.g = g;
		 * invokeAndWait(this.run); } private Runnable run = new Paint(); class
		 * Paint implements Runnable { public void run() { Graphics gg = new
		 * MIDPGraphics(g); MIDPDisplay.this.paint(gg); } };
		 */
	}
}