package melnorme.miscutil;


/**
 * Excepetion adapter to make checked exceptions less annoying. 
 * Based on Bruce Eckel's article:
 * http://www.mindview.net/Etc/Discussions/CheckedExceptions
 */
@SuppressWarnings("serial")
public class ExceptionAdapter extends RuntimeException {

	// The original checked exception
	private Exception originalException;
	// Number of frames that originalException traveled while checked
	private int checkedLength; 

	public ExceptionAdapter(Exception e) {
		super(e.toString());
		originalException = e;
		
		// Determine checkedLength based on the difference to this stack trace
		StackTraceElement[] est = e.getStackTrace();
		checkedLength = est.length - getStackTrace().length;
		
		StackTraceElement ste = getStackTrace()[0];
		String firstMethod = ste.getClassName() +"."+ ste.getMethodName();
		// Adjust checkedLength if EA was created in method unchecked
		if(firstMethod.endsWith("ExceptionAdapter.unchecked"))
			checkedLength++;
		
	}

/*	public ExceptionAdapter(String string) {
		this(new Exception(string));
	}
*/
	
	protected void printStackTrace(melnorme.miscutil.log.IPrinter pr) {
        synchronized (pr) {
            pr.println(this);
            StackTraceElement[] trace = originalException.getStackTrace();
            for (int i=0; i < trace.length; i++) {
                pr.print("\tat " + trace[i]);
            	if(i == checkedLength)
            		pr.print(" [UNCHECKED]");
                pr.println();
            }
        }
	}
	
	public void printStackTrace(java.io.PrintStream ps) {
		printStackTrace(new melnorme.miscutil.log.StreamPrinter(ps));
	}

	public void printStackTrace(java.io.PrintWriter pw) {
		printStackTrace(new melnorme.miscutil.log.WriterPrinter(pw));
	}


	public void rethrow() throws Exception {
		throw originalException;
	}

	public String toString() {
        //String name = getClass().getName();
        //return name + "\n>> " + getLocalizedMessage();
        return "[UE] " + getLocalizedMessage();
	}
	
	/** Creates an unchecked Throwable, if not unchecked already. */
	public static final RuntimeException unchecked(Throwable e) {
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
	
	/** Same as unchecked() but stands for a TODO call. */
	@Deprecated public static RuntimeException uncheckedTODO(Throwable e) {
		return unchecked(e);
	}
}
