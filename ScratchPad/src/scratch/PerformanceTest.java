package scratch;

public class PerformanceTest {
	
	public static final boolean debug1 = false;
	public static final boolean debug2 = Boolean.parseBoolean("false");
	
	public static void main(String[] args) {
		//asdfsd
		int times = 200;
		
		long delta1 = 0;
		long delta2 = 0;

		while(times-- > 0) {
			long t = System.nanoTime();
			for (int i = 0; i < 1000*1000*10; i++) {
				if (debug1) {
					doNothing();
				}
			}
			delta1 += System.nanoTime() - t;
			
			
			long t2 = System.nanoTime();
			for (int i = 0; i < 1000*1000*10; i++) {
				if (debug2) {
					doNothing();
				}
			}
			delta2 += System.nanoTime() - t2;
		}
		System.out.println(delta1);
		System.out.println(delta2);

		throw new RuntimeException("The End");
	}
	
	public static void doNothing() {
		System.out.println("NEVER HAPPEN");
	}
}