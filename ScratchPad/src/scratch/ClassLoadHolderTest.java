package scratch;

import melnorme.miscutil.MiscUtil;

/* This shows how classloading is *serial* but not stop-the-world or even guarantees 
 * correct synchronization. */
public class ClassLoadHolderTest {
	
	public static void main(String[] args) {
		
		final Runnable runnable = new Runnable() {
			public void run() {
				MiscUtil.sleepUnchecked(1000);
				while(true) {
					MiscUtil.sleepUnchecked(100);
					System.out.println("Thread " + Foo.number + " " + ClassLoadHolder.number);
				}
			}
		};
		
		final Thread thread = new Thread(runnable);
		thread.start();
		
		new ClassLoadHolder();
	}
	
}
