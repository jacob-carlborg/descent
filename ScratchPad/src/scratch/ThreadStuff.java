package scratch;

import melnorme.miscutil.MiscUtil;

public class ThreadStuff {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		Thread thread = new Thread() {
			@Override
			public void run() {
				System.out.println("Foo");
				MiscUtil.sleepUnchecked(500);
			}
		};
		
		thread.start();
		try {
			thread.join();
			thread.join();
			thread.start();
			thread.join();
		} catch (InterruptedException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
	
}
