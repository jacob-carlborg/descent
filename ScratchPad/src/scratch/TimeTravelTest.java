package scratch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TimeTravelTest {
	
	public static void main(String[] args) {

		final BufferedReader bis = new BufferedReader(new InputStreamReader(System.in));

		long lastTime = System.currentTimeMillis();
		long lastTimeNano = System.nanoTime();
		
		while(true) {
			long currentTime = System.currentTimeMillis();
			long delta = currentTime - lastTime;
			System.out.println(currentTime + " delta: " + delta);
			
			long currentTimeNano = System.nanoTime();
			long deltaNano = currentTimeNano - lastTimeNano;
			System.out.println(currentTimeNano + " delta: " + deltaNano);
			
			lastTime = currentTime;
			lastTimeNano = currentTimeNano;
			
			try {
				bis.readLine();
			} catch (IOException e) {
				throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
			}
		}
		
	}
	
}
