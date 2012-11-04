/*
 * Created on 2004/04/19 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gikolet.base.ui;

import gikolet.base.ui.events.KeyEvent;

import java.util.Vector;

/**
 * @author tetsutaro To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class EventQueue {
	final static int	APP_START	= 5;
	final static int	APP_PAUSE	= 6;
	final static int	APP_DESTROY	= 7;

	final static int	REPAINT		= 0;
	final static int	REVALIDATE	= 1;
	final static int	KEY_ACTION	= 2;
	final static int	ACTION		= 3;

	private Vector		queue		= new Vector();
	private Display		_display;

	EventQueue(Display display) {
		_display = display;
	}

	private synchronized void post(Event data) {
		this.queue.addElement(data);
		this.notifyAll();
	}

	synchronized Event pop() {
		if (this.queue.isEmpty()) {
			return null;
		}
		Event event = (Event) this.queue.firstElement();
		this.queue.removeElementAt(0);

		return event;
	}

	void dispatch(Event event) {
		if (event.type == KEY_ACTION) {
			_display.keyAction(event.keyActionType, event.keyCode);
		} else if (event.type == REPAINT) {
			_display.paint(event.x, event.y, event.width, event.height, false);
		} else if (event.type == REVALIDATE) {
			_display.layout(event.source);
		} else if (event.type == ACTION) {
			if (event.lock != null) {
				try {
					event.run.run();
				} catch (Exception e) {
					event.exception = e;
				}
				lockNotify(event);
			} else {
				event.run.run();
			}
		} else if (event.type == APP_START) {
			_display.fireApplicationStartCore();
			lockNotify(event);
		} else if (event.type == APP_PAUSE) {
			_display.fireApplicationPauseCore();
			lockNotify(event);
		} else if (event.type == APP_DESTROY) {
			_display.fireApplicationDestroyCore();
			lockNotify(event);
		}
	}

	private void lockNotify(Event e) {
		if (e.lock != null) {
			synchronized (e.lock) {
				Object lock = e.lock;
				e.lock = null;
				lock.notifyAll();
			}
		}
	}

	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	void appStart() {
		Event event = new Event();
		event.type = APP_START;

		if (!_display.isEventThread()) {
			postAndWait(event);
		} else {
			synchronized (this) {
				post(event);
				while (!queue.isEmpty()) {
					Event e = pop();
					dispatch(e);
				}
			}
		}
	}

	void appPause() {
		Event event = new Event();
		event.type = APP_PAUSE;

		if (!_display.isEventThread()) {
			postAndWait(event);
		} else {
			synchronized (this) {
				post(event);
				while (!queue.isEmpty()) {
					Event e = pop();
					dispatch(e);
				}
			}
		}
	}

	void appDestroy() {
		Event event = new Event();
		event.type = APP_DESTROY;

		if (!_display.isEventThread()) {
			postAndWait(event);
		} else {
			synchronized (this) {
				post(event);
				while (!queue.isEmpty()) {
					Event e = pop();
					dispatch(e);
				}
			}
		}
	}

	private void postAndWait(Event event) {
		Object lock = new Object();
		event.lock = lock;

		synchronized (event.lock) {
			post(event);

			while (event.lock != null) {
				try {
					event.lock.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public synchronized void repaint(int x, int y, int width, int height) {
		for (int i = 0; i < this.queue.size(); i++) {
			Event event = (Event) this.queue.elementAt(i);
			if (event.type == REPAINT) {
				int minX = (event.x > x) ? x : event.x;
				int minY = (event.y > y) ? y : event.y;
				int maxX = (event.x + event.width > x + width) ? event.x
						+ event.width : x + width;
				int maxY = (event.y + event.height > y + height) ? event.y
						+ event.height : y + height;
				x = minX;
				y = minY;
				width = maxX - minX;
				height = maxY - minY;

				this.queue.removeElementAt(i);

				break;
			}
		}
		Event event = new Event();
		event.type = REPAINT;
		event.x = x;
		event.y = y;
		event.width = width;
		event.height = height;

		post(event);
	}

	public synchronized void validate(ContainerControl source, boolean direct) {
		vfind: for (int i = 0; i < this.queue.size(); i++) {
			Event event = (Event) this.queue.elementAt(i);
			if (event.type == REVALIDATE) {
				ContainerControl vroot;

				vroot = source;
				do {
					if (vroot == event.source) {
						this.queue.removeElementAt(i);
						source = vroot;
						break vfind;
					}
					vroot = vroot.getParentValidRoot();
				} while (vroot != null);

				vroot = event.source;
				do {
					if (vroot == source) {
						this.queue.removeElementAt(i);
						break vfind;
					}
					vroot = vroot.getParentValidRoot();
				} while (vroot != null);
			}
		}
		if (direct) {
			_display.layout(source);
		} else {
			Event event = new Event();
			event.type = REVALIDATE;
			event.source = source;

			post(event);
		}
	}

	public synchronized void keyAction(int keyType, int keyCode) {
		if (keyType == KeyEvent.REPEATED) {
			for (int i = 0; i < this.queue.size(); i++) {
				Event event = (Event) this.queue.elementAt(i);
				if (event.type == KEY_ACTION && event.keyActionType == keyType
						&& event.keyCode == keyCode) {
					return;
				}
			}
		}
		Event event = new Event();
		event.type = KEY_ACTION;
		event.keyActionType = keyType;
		event.keyCode = keyCode;

		post(event);
	}

	public void invoke(Runnable run) {
		Event event = new Event();
		event.type = ACTION;
		event.run = run;
		post(event);
	}

	public Exception invokeAndWait(Runnable run) {
		Event event = new Event();
		event.type = ACTION;
		event.run = run;

		event.lock = new Object();

		postAndWait(event);
		return event.exception;
	}

	synchronized void removeAllEvents() {
		this.queue.removeAllElements();
	}

	class Event {
		int					type;
		ContainerControl	source;

		Object				lock;
		Runnable			run;
		Exception			exception;

		int					x, y, width, height;
		int					keyActionType, keyCode;
	}
}