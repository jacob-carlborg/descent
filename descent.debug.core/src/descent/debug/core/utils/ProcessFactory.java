package descent.debug.core.utils;

import java.io.File;
import java.io.IOException;

public class ProcessFactory {
	
	static private ProcessFactory instance;
	private Runtime runtime;

	private ProcessFactory() {
		runtime = Runtime.getRuntime();
	}

	public static ProcessFactory getFactory() {
		if (instance == null)
			instance = new ProcessFactory();
		return instance;
	}

	public Process exec(String cmd) throws IOException {
		return runtime.exec(cmd);
	}

	public Process exec(String[] cmdarray) throws IOException {
		return runtime.exec(cmdarray);
	}

	public Process exec(String[] cmdarray, String[] envp) throws IOException {
		return runtime.exec(cmdarray, envp);
	}

	public Process exec(String cmd, String[] envp) throws IOException {
		return runtime.exec(cmd, envp);
	}

	public Process exec(String cmd, String[] envp, File dir)
		throws IOException {
		return runtime.exec(cmd, envp, dir);
	}

	public Process exec(String cmdarray[], String[] envp, File dir)
		throws IOException {
		return runtime.exec(cmdarray, envp, dir);
	}

}
