package scratch;

public class InitializationOnDemandHolderIdiom {
	
	private static class LazyHolder {
		private static final InitializationOnDemandHolderIdiom something = new InitializationOnDemandHolderIdiom();
	}
	
	public static InitializationOnDemandHolderIdiom getInstance() {
		return LazyHolder.something;
	}
	
	private InitializationOnDemandHolderIdiom() {
		synchronized(mutex) {
		}
	}
	
	
	static Object mutex = new Object();
	
	public static void main(String[] args) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				synchronized(mutex) {
					System.out.println("Lock Mutex");
					while(true) {
					}
				}
			}
		};
		thread.start();

		getInstance(); // Dead Lock
		System.out.println("End");
	}
}
