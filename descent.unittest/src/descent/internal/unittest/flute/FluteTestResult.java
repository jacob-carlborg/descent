package descent.internal.unittest.flute;

public final class FluteTestResult
{
	// The simplicity of this stuff being public outweighs the benefits of
	// encapsulation here, IMO.
	
	/** The result type */
	public final ResultType resultType;
	
	/** The file in which the exception was thrown for a FAILED result,
	 *  or null for a PASSED/ERROR result. */
	public final String file;
	
	/** The line on which the exception was thrown for a FAILED result,
	 *  or <b>-1</b> for a PASSED/ERROR result. */
	public final int line;
	
	/** The exception message. Will always be null for a PASSED result, 
	 *  and may be null if there was no error message specified for a 
	 *  FAILED or ERROR result. */
	public final String message;
	
	/** The class of the exception for an ERROR result, or null for a 
	 *  PASSED/FAILED result. */
	public final String exceptionType;
	
	/** The stack trace of the thrown exception. Will always be null for
	 *  a PASSED result, and may be null for a FAILED/ERROR result if the
	 *  exception wasn't traced. */
	public final StackTraceElement[] stackTrace;
	
	public static enum ResultType
	{
		PASSED,
		FAILED,
		ERROR
	}
	
	public static final class StackTraceElement
	{
		/** The name of the function. */
		public final String function;
		
		/** The file the function is defined in, or null if only the
		 *  address was found. */
		public final String file;
		
		/** The line the function is defined on, or -1 if only the
		 *  address was found. */
		public final int line;
		
		/** The address of the executing code or -1 if the file/line was
		 *  found. */
		public final long addr;
		
		private StackTraceElement(String function, String file,
				int line, long addr)
		{
			this.function = function;
			this.file = file;
			this.line = line;
			this.addr = addr;
		}
		
		public static StackTraceElement line(String function, String file,
				int line)
		{
			return new StackTraceElement(
					function, // function
					file,     // file
					line,     // line
					-1);      // addr
		}
		
		public static StackTraceElement address(String function, long addr)
		{
			return new StackTraceElement(
					function, // function
					null,     // file
					-1,       // line
					addr);    // addr
		}
		
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			
			buf.append(function);
			buf.append(" (");
			if(addr >= 0)
				buf.append(String.format("0x%1$x", addr));
			else
				buf.append(file + ":" + line);
			buf.append(")");
			
			return buf.toString();
		}
	}
	
	public static FluteTestResult passed()
	{
		return new FluteTestResult(
				ResultType.PASSED, // resultType
				null,              // file
				-1,                // line
				null,              // message
				null,              // exceptionType
				null);             // stackTrace
	}
	
	public static FluteTestResult failed(String file, int line, String
			message, StackTraceElement[] stackTrace)
	{	
		return new FluteTestResult(
				ResultType.FAILED, // resultType
				file,              // file
				line,              // line
				message,           // message
				null,              // exceptionType
				stackTrace);       // stackTrace
	}
	
	public static FluteTestResult error(String message, 
			String exceptionType, StackTraceElement[] stackTrace)
	{
		return new FluteTestResult(
				ResultType.ERROR,  // resultType
				null,              // file
				-1,                // line
				message,           // message
				exceptionType,     // exceptionType
				stackTrace);       // stackTrace
	}

	private FluteTestResult(ResultType resultType, String file, int line,
			String message, String exceptionType, 
			StackTraceElement[] stackTrace)
	{
		this.resultType = resultType;
		this.file = file;
		this.line = line;
		this.message = message;
		this.exceptionType = exceptionType;
		this.stackTrace = stackTrace;
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("resultType: " + resultType + "\n");
		buf.append("file: " + file + "\n");
		buf.append("line: " + line + "\n");
		buf.append("exceptionType: " + exceptionType + "\n");
		buf.append("message: " + message + "\n");
		if(stackTrace != null)
		{
			buf.append("stackTrace: [\n");
			for(StackTraceElement ste: stackTrace)
				buf.append("   " + ste.toString() + "\n");
			buf.append("]");
		}
		else
			buf.append("stackTrace: null");
		
		return buf.toString();
	}
}
