package util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Excepetion adapter to make checked exceptions less annoying. 
 * Idea from Bruce Eckel's article:
 * http://www.mindview.net/Etc/Discussions/CheckedExceptions
 */
@SuppressWarnings("serial")
public class ExceptionAdapter extends RuntimeException {

	private final String stackTrace;
	public Exception originalException;

	private ExceptionAdapter(Exception e) {
		super(e.toString());
		originalException = e;
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		stackTrace = sw.toString();
	}

	public void printStackTrace() {
		printStackTrace(System.err);
	}

	public void printStackTrace(java.io.PrintStream s) {
		synchronized (s) {
			s.print(getClass().getName() + ": ");
			s.print(stackTrace);
		}
	}

	public void printStackTrace(java.io.PrintWriter s) {
		synchronized (s) {
			s.print(getClass().getName() + ": ");
			s.print(stackTrace);
		}
	}

	public void rethrow() throws Exception {
		throw originalException;
	}

	public static RuntimeException unchecked(Throwable e) {
		if(e instanceof RuntimeException)
			return (RuntimeException) e;
		else if(e instanceof Exception)
			return new ExceptionAdapter((Exception)e);
		if(e instanceof Error)
			throw (Error) e;
		else {
			Assert.fail("uncheck: Unsupported Throwable: " + e);
			return null;
		}
	}
}
