package descent.internal.unittest.flute;

import descent.unittest.IStackTraceElement;
import descent.unittest.ITestResult;

public final class FluteTestResult implements ITestResult
{
	private final ResultType resultType;
	private final String file;
	private final int line;
	private final String message;
	private final String exceptionType;
	private final IStackTraceElement[] stackTrace;
	
	public static final class StackTraceElement implements IStackTraceElement
	{
		public final String function;
		public final String module;
		public final int line;
		public final long addr;
		
		private StackTraceElement(String function, String module,
				int line, long addr)
		{
			this.function = function;
			this.module = module;
			this.line = line;
			this.addr = addr;
		}
		
		public static StackTraceElement line(String function, String module,
				int line)
		{
			return new StackTraceElement(
					function, // function
					module,   // module
					line,     // line
					-1);      // addr
		}
		
		public static StackTraceElement address(String function,
				long addr)
		{
			return new StackTraceElement(
					function, // function
					null,     // module
					-1,       // line
					addr);    // addr
		}
		
		public String toString()
		{
			StringBuffer buf = new StringBuffer();
			
			buf.append(function);
			buf.append(" ("); //$NON-NLS-1$
			if(addr >= 0)
				buf.append(String.format("%1$s:0x%2$x", module, addr)); //$NON-NLS-1$
			else
				buf.append(String.format("%1$s:%2$d", module, line)); //$NON-NLS-1$
			buf.append(")"); //$NON-NLS-1$
			
			return buf.toString();
		}

		public final String getFunction()
		{
			return function;
		}

		public final String getModule()
		{
			return module;
		}

		public final int getLine()
		{
			return line;
		}

		public final long getAddress()
		{
			return addr;
		}

		public boolean lineInfoFound()
		{
			return addr < 0;
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
			message, IStackTraceElement[] stackTrace)
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
			String exceptionType, IStackTraceElement[] stackTrace)
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
			IStackTraceElement[] stackTrace)
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
		
		buf.append("resultType: " + resultType + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("file: " + file + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("line: " + line + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("exceptionType: " + exceptionType + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("message: " + message + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		if(stackTrace != null)
		{
			buf.append("stackTrace: [\n"); //$NON-NLS-1$
			for(IStackTraceElement ste: stackTrace)
				buf.append("   " + ste.toString() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("]"); //$NON-NLS-1$
		}
		else
			buf.append("stackTrace: null"); //$NON-NLS-1$
		
		return buf.toString();
	}

	public final ResultType getResultType()
	{
		return resultType;
	}

	public final String getFile()
	{
		return file;
	}

	public final int getLine()
	{
		return line;
	}

	public final String getMessage()
	{
		return message;
	}

	public final String getExceptionType()
	{
		return exceptionType;
	}

	public final IStackTraceElement[] getStackTrace()
	{
		return stackTrace;
	}
}
