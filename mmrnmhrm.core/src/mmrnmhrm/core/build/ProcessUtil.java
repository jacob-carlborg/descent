package mmrnmhrm.core.build;

import java.io.IOException;

import melnorme.miscutil.ExceptionAdapter;
import melnorme.miscutil.FileUtil;
import melnorme.miscutil.log.Logg;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

public class ProcessUtil {

	public static void waitForProcess(IProgressMonitor monitor, final Process proc)
			throws InterruptedException {
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
		//thread.setUncaughtExceptionHandler(eh)
		thread.start();
		//thread.run();
		do {
			if(monitor.isCanceled())
				throw new OperationCanceledException();
			thread.join(200);
		} while(thread.isAlive());
		
		if(result[0] != null) {
			throw result[0]; 
		}
		
		Logg.builder.println("  Exit value: " + proc.exitValue());
		try {
			Logg.builder.println(FileUtil.readStringFromStream(proc.getInputStream()));
			Logg.builder.println(FileUtil.readStringFromStream(proc.getErrorStream()));
		} catch (IOException e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}

}
