package scratch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import melnorme.miscutil.MiscUtil;

public class ExecutorsSnippet {
	
	public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	public static final DateFormat TIMESTAMPSHORT_FORMAT = new SimpleDateFormat("ss.SSS");


	public static void main(String[] args) {
	
		final Runnable runnable1 = new Runnable() {
			public void run() {
				System.out.println("Runable1 " + TIMESTAMP_FORMAT.format(new Date()));
			}
		};
		
		final Runnable runnable2 = new Runnable() {
			int counter = 5;
			public void run() {
				System.out.println("Runable2 " + TIMESTAMP_FORMAT.format(new Date()));
				if(counter-- > 0)
				MiscUtil.sleepUnchecked(2000);
			}
		};
		
//		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
//		scheduledThreadPool.schedule(runnable1, 1000L, TimeUnit.MILLISECONDS);
////		scheduledThreadPool.scheduleAtFixedRate(runnable1, 1000L, 500L, TimeUnit.MILLISECONDS);
//		scheduledThreadPool.scheduleAtFixedRate(runnable2, 1000L, 1000L, TimeUnit.MILLISECONDS);
//		
		
		final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		
		singleThreadExecutor.submit(runnable1);
		singleThreadExecutor.shutdown();
		singleThreadExecutor.shutdown();
		singleThreadExecutor.submit(runnable1);
		
		//scheduledThreadPool.shutdown();
		
	}
}
