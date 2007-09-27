package mmrnmhrm.core.build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import dtool.Logg;

public class ProcessUtil {
	
	private static class RedirectOutput extends Thread {

		// Stream to read and redirect to standard output
		private InputStream stream;  

		public RedirectOutput(InputStream stream) {
			this.stream = stream;
		}
		
		/**
		 * Reads text from the input stream and redirects it to standard output
		 * using a separate thread.
		 */
		public void run() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					Logg.builder.println("�� " + line);
					DeeProjectBuilder.buildListener.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					// Do nothing
				}
			}
		}
	}  // End of RedirectOutput inner class

	

	public static void waitForProcess(IProgressMonitor monitor, final Process proc)
			throws InterruptedException, IOException {
		final InterruptedException[] result = new InterruptedException[1];

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					proc.waitFor();
				} catch (InterruptedException e) {
					result[0] = e;
				} 
			}
		};
		
		Thread outThread = new RedirectOutput(proc.getInputStream());
		Thread errThread = new RedirectOutput(proc.getErrorStream());

		outThread.start();
		errThread.start();
		thread.start();
		//thread.run();
		
		try {
			do {
				if(monitor.isCanceled()) {
					proc.destroy();
					throw new OperationCanceledException();
				}
				thread.join(200);
			} while(thread.isAlive());
			
			if(result[0] != null) {
				throw result[0]; 
			}

			Logg.builder.println(">>  Exit value: " + proc.exitValue());

		} finally {
			thread.join();
			outThread.join();
			errThread.join();
		}
		
		/*try {
			Logg.builder.println(FileUtil.readStringFromStream(proc.getInputStream()));
			Logg.builder.println(FileUtil.readStringFromStream(proc.getErrorStream()));
		} catch (IOException e) {
			throw ExceptionAdapter.unchecked(e);
		}*/
	}

}
