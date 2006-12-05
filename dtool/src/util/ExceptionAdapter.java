package util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Excepetion adapter to make checked exceptions less annoying. 
 * Idea from Bruce Eckel's article:
 * http://www.mindview.net/Etc/Discussions/CheckedExceptions
 */
public class ExceptionAdapter extends RuntimeException {
	private final String stackTrace;
	public Exception originalException;

	public ExceptionAdapter(Exception e) {
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
}
