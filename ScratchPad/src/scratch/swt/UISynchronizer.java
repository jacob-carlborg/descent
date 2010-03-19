package scratch.swt;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;

/**
 * Helper class to wait on pending UI events, but that can also be awaken by other means.
 */
public class UISynchronizer  {

	private final Object mutex = new Object();
	
	private final AtomicBoolean awakeRequested = new AtomicBoolean(false);

	/**
	 * Does a {@link Display#syncExec(Runnable)} to wait on all pending UI events, 
	 * and can also be awaken by {@link #awake()}. 
	 * After awake is called, {@link #waitOnUI()} is not guaranteed to wait for UI events. 
	 * @return returns true if the it was prematurely awoken and there possible pending events, false otherwise
	 */
	public void waitOnUI() throws InterruptedException {

		synchronized(mutex) {
			if(!awakeRequested.get()) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						synchronized(mutex) {
							mutex.notifyAll();
						}
					} 
				});

				mutex.wait();
			}
		}
	}
	
	/**
	 * Awakes any thread waiting on this synchronizer.
	 */
	public void awake() {
		synchronized(mutex) {
			awakeRequested.set(true);
			mutex.notifyAll();
		}
	}	
}