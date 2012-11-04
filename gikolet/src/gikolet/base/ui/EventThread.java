/*
 * Created on 2004/04/19
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gikolet.base.ui;

/**
 * @author tetsutaro
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class EventThread extends Thread {
	private EventQueue	queue;
	private boolean		dispose	= false;

	EventThread(EventQueue queue) {
		if (queue == null) {
			throw new NullPointerException("queue");
		}
		this.queue = queue;
	}

	static boolean isEventThrad() {
		return Thread.currentThread() instanceof EventThread;
	}

	public void run() {
		//System.out.println("Event Thread Start");

		EventQueue.Event eqe;
		while (!this.dispose) {
			synchronized (this.queue) {
				while (this.queue.isEmpty()) {
					try {
						this.queue.wait();
					} catch (InterruptedException e) {}
				}
				eqe = this.queue.pop();
			}
			if (eqe != null) {
				try {
					this.queue.dispatch(eqe);
				} catch (Throwable t) {
					System.out.println(t);
					t.printStackTrace();
				}
			}
		}
		//System.out.println("Event Thread Dispose");
	}

	public void dispose() {
		this.dispose = true;
	}
}