package scratch.utils;

import static melnorme.miscutil.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class PerfUtils {
	
	public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	public static final DateFormat TIMESTAMPSHORT_FORMAT = new SimpleDateFormat("ss.SSS");

	private static HashMap<String, PerfTimer> timers = new HashMap<String, PerfTimer>();
	
	public static synchronized PerfTimer timer(String name) {
		if(timers.get(name) == null) {
			timers.put(name, new PerfTimer(name));
		}
		return timers.get(name);
	}
	
	public static class PerfTimer {
		final String name;
		final PerfStatistics stats;
		long beginTimeMs = -1;
		
		public PerfTimer(String name) {
			this.name = name;
			this.stats = stats("PerfTimer." + name);
		}
		
		public PerfStatistics getStats() {
			return stats;
		}

		@Deprecated
		public void mark() {
			markBegin();
		}
		
		public void markBegin() {
			beginTimeMs = System.currentTimeMillis();
		}
		
		public long markEnd() {
			assertTrue(beginTimeMs > 0);
			
			long newTimeMs = System.currentTimeMillis();
			long delta = newTimeMs - beginTimeMs;
			assertTrue(delta >= 0);
			stats.add((int) delta);
			
			beginTimeMs = newTimeMs;
			return delta;
		}

		public void markEndAndPrint(String msg) {
			if(beginTimeMs == -1) {
				markBegin();
			} else {
				long delta = markEnd();
				printCycle(msg, delta);
			}
		}

		public void printCycle(String msg, long delta) {
			final String formatStr = TIMESTAMP_FORMAT.format(new Date(beginTimeMs));
			final String msgPart = msg == null ? "" : ": " + msg;
			System.out.println(name + msgPart + " delta: " + delta + " Average: (" + stats.getAverageAsInt() + ") [" + formatStr + "]");
		}
		
		public void resetStats() {
			beginTimeMs = -1;
			stats.reset();
		}
		
	}
	
	private static HashMap<String, PerfStatistics> statistics = new HashMap<String, PerfStatistics>();

	public static synchronized PerfStatistics stats(String name) {
		if(statistics.get(name) == null) {
			statistics.put(name, new PerfStatistics());
		}
		return statistics.get(name);
	}

	public static class PerfStatistics {
		double timeAverage = Double.NaN;
		double timeAverage2 = Double.NaN;
		double totalTime = 0;
		long count = 0;
		
		public void add(int time) {
			if(Double.isNaN(timeAverage)) {
				timeAverage = time;
				count = 1;
			} else {
				count += 1;
				timeAverage = timeAverage * (count-1) / count + (((double) time) / count);
			}
			totalTime += time;
			timeAverage2 = totalTime / count;
		}
		
		public void reset() {
			timeAverage = -1;
			count = 0;
		}

		public int getAverageAsInt() {
			return (int) timeAverage;
		}
		
		public double getAverage() {
			return timeAverage;
		}
		
		public double getAverage2() {
			return timeAverage2;
		}

		public double getTotalTime() {
			return totalTime;
		}

	}

	
	public static class PerfCounter {
		long counter;
		
		public void counterInc() {
			counter++;
			System.out.println("INC, counter:" + counter);
		}
		
		public void counterDec() {
			counter--;
			System.out.println("DEC, counter:" + counter);
		}
	}
	
	public static final PerfCounter counter = new PerfCounter();
	
}
