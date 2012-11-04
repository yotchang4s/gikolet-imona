/*
 * Created on 2004/04/19 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gikolet.base.ui;

import gikolet.base.ui.events.ApplicationEventListener;
import gikolet.base.ui.events.KeyEvent;

import java.util.Vector;

/**
 * @author tetsutaro To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public abstract class Display {
	public final static int	NUMBER		= 0;
	public final static int	PASSWORD	= 1;
	public final static int	ANY			= 2;

	private Shell			shell;
	private EventQueue		queue;
	private RootControl		currentWindow;
	private Vector			windows		= new Vector();

	private Vector			_listeners	= new Vector();

	private PopupMenuDialog	menuDialog;
	private PopupMenuDialog	popupMenuDialog;

	protected Display() {
		this.queue = new EventQueue(this);
		new EventThread(this.queue).start();
	}

	protected EventQueue getEventQueue() {
		return this.queue;
	}

	protected abstract Font createFont(int face, int style, int size);

	public abstract void destroy();

	public String imeOn(String text, int mode) throws CancelException {
		return imeOn(null, text, mode);
	}

	// ブロックするように実装すること
	public abstract String imeOn(String title, String text, int mode) throws CancelException;

	protected void fireApplicationStart() {
		queue.appStart();
	}

	protected void fireApplicationPause() {
		queue.appPause();
	}

	protected void fireApplicationDestroy() {
		queue.appDestroy();
	}

	protected void fireApplicationStartCore() {
		if (_listeners != null) {
			for (int i = 0; i < _listeners.size(); i++) {
				((ApplicationEventListener) _listeners.elementAt(i)).startApplication();
			}
		}
	}

	protected void fireApplicationPauseCore() {
		if (_listeners != null) {
			for (int i = 0; i < _listeners.size(); i++) {
				((ApplicationEventListener) _listeners.elementAt(i)).pauseApplication();
			}
		}
	}

	protected void fireApplicationDestroyCore() {
		if (_listeners != null) {
			for (int i = 0; i < _listeners.size(); i++) {
				((ApplicationEventListener) _listeners.elementAt(i)).exitApplication();
			}
		}
	}

	protected void showMenuDialog() {
		/*
		 * if (isMenuDialogShowing()) { return; }
		 */
		RootControl root = getCurrentWindow();
		if (root != null/* && !isPopupMenuDialogShowing() */) {
			root.getMenuItem().fireAction();
			if (root.getMenuItem() instanceof Menu) {
				Menu menu = (Menu) root.getMenuItem();
				if (menu.getMenuItemSize() > 0) {
					this.menuDialog = new PopupMenuDialog(root, menu);
					this.menuDialog.setVisible(true);
					this.menuDialog = null;

					return;
				}
			}
		}
	}

	protected void showPopupMenuDialog() {
		/*
		 * if (isPopupMenuDialogShowing()) { return; }
		 */
		RootControl root = getCurrentWindow();
		if (root != null /* && !isMenuDialogShowing() */) {
			Control focus = root.getFocus();
			MenuItem mi = focus.getPopupMenuItem();
			if (mi != null) {
				mi.fireAction();
				if (mi instanceof Menu) {
					Menu m = (Menu) mi;
					if (m.getMenuItemSize() > 0) {
						this.popupMenuDialog = new PopupMenuDialog(root, m);
						// setMenuItemCommand();
						this.popupMenuDialog.setVisible(true);
						this.popupMenuDialog = null;

						return;
					}
				}
			}
		}
	}

	public abstract int getScreenWidth();

	public abstract int getScreenHeight();

	public Dimension getScreenSize() {
		return new Dimension(getScreenWidth(), getScreenHeight());
	}

	public boolean isEventThread() {
		return EventThread.isEventThrad();
	}

	public void setShell(Shell shell) {
		Shell oldShell = this.shell;
		this.shell = shell;

		if (oldShell != null) {
			oldShell.setDisplay(null);
		}
		if (shell != null) {
			shell.setDisplay(this);
			if (shell.getFocus() == null) {
				shell.transferFocusForward();
			}
			shell.revalidate();
		}

		if (currentWindow == null || currentWindow == oldShell) {
			setCurrentRoot(shell);
		}
		repaint();
	}

	protected abstract void setMenuItemNavi(MenuItem menuItem);

	protected abstract void setPopupMenuItemNavi(MenuItem menuItem);

	/*
	 * public boolean isMenuDialogShowing() { //return menuDialog != null &&
	 * menuDialog.getVisible(); return false; } public boolean
	 * isPopupMenuDialogShowing() { //return popupMenuDialog != null &&
	 * popupMenuDialog.getVisible(); return false; }
	 */

	public Shell getShell() {
		return this.shell;
	}

	public void addApplicationEventListener(ApplicationEventListener listener) {
		_listeners.addElement(listener);
	}

	public void removeApplicationEventListener(ApplicationEventListener listener) {
		_listeners.removeElement(listener);
	}

	protected abstract void paint(int x, int y, int width, int height, boolean direct);

	void repaint() {
		repaint(0, 0, getScreenWidth(), getScreenHeight());
	}

	void repaint(int x, int y, int width, int height) {
		this.queue.repaint(x, y, width, height);
	}

	void validate(ContainerControl source, boolean direct) {
		this.queue.validate(source, direct);
	}

	void layout(ContainerControl source) {
		// System.out.println(source);
		if (source != null) {
			source.layoutCore();
		}
		return;
	}

	private Control	lastFocus;

	void keyAction(int keyActionType, int keyCode) {
		if (this.shell == null) {
			return;
		}
		RootControl current = getCurrentWindow();
		Control focus = current.getFocus();
		if (focus == null) {
			if (!current.transferFocusForward()) {
				return;
			}
			focus = current.getFocus();
		}
		if (keyActionType == KeyEvent.PRESSED) {
			focus.processKeyMessage(keyActionType, keyCode);
			this.lastFocus = focus;
		} else if (keyActionType == KeyEvent.REPEATED) {
			focus.processKeyMessage(keyActionType, keyCode);
		} else if (keyActionType == KeyEvent.RELEASED) {
			if (focus == this.lastFocus) {
				focus.processKeyMessage(keyActionType, keyCode);
			}
		}
	}

	void mouseMove(int x, int y) {
	}

	void mouseDown() {
	}

	void mouseUP() {
	}

	public void invoke(Runnable run) {
		this.queue.invoke(run);
	}

	public Exception invokeAndWait(Runnable run) {
		return this.queue.invokeAndWait(run);
	}

	protected void paint(Graphics g) {
		Shell shell = getShell();
		if (shell == null) {
			return;
		}
		g.setBackColor(shell.getBackColor());
		g.setColor(shell.getForeColor());
		g.setFont(shell.getFont());

		shell.paint(g);

		for (int i = 0; i < this.windows.size(); i++) {
			RootControl window = (RootControl) this.windows.elementAt(i);

			if (g.getClipX() < window.getX() + window.getWidth()
					&& g.getClipY() < window.getY() + window.getHeight()
					&& window.getX() < g.getClipX() + g.getClipWidth()
					&& window.getY() < g.getClipY() + g.getClipHeight()) {

				Graphics wg = g.createGraphics(window.getX(), window.getY(), window.getWidth(),
						window.getHeight());
				wg.setBackColor(window.getBackColor());
				wg.setColor(window.getForeColor());
				wg.setFont(window.getFont());
				window.paint(wg);
			}
		}
	}

	private void setCurrentRoot(RootControl root) {
		if (root != currentWindow) {
			currentWindow = root;
			if (root != null) {
				setMenuItemNavi(root.getMenuItem());
				Control focus = root.getFocus();
				if (focus != null) {
					setPopupMenuItemNavi(focus.getPopupMenuItem());
				} else {
					setPopupMenuItemNavi(null);
				}
			} else {
				setMenuItemNavi(null);
				setPopupMenuItemNavi(null);
			}
		}
	}

	void addDialog(RootControl rootControl) {
		if (rootControl != null) {
			this.windows.addElement(rootControl);
			rootControl.setDisplay(this);
			setCurrentRoot(rootControl);

			if (rootControl.getFocus() == null && !rootControl.setFocus()) {
				rootControl.transferFocusForward();
			}
		}
	}

	void removeDialog(RootControl rootControl) {
		if (rootControl != null) {
			this.windows.removeElement(rootControl);
			rootControl.setDisplay(null);
			if (this.windows.size() == 0) {
				setCurrentRoot(shell);
			} else {
				setCurrentRoot((RootControl) this.windows.lastElement());
			}
			if (this.currentWindow != null) {
				if (this.currentWindow.getFocus() == null && !this.currentWindow.setFocus()) {
					this.currentWindow.transferFocusForward();
				}
			}
		}
	}

	RootControl getCurrentWindow() {
		return this.currentWindow;
	}
}