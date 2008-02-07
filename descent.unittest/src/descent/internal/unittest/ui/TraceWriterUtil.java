package descent.internal.unittest.ui;

import descent.internal.unittest.ui.FailureTableDisplay.LineType;
import descent.unittest.IStackTraceElement;
import descent.unittest.ITestResult;

public class TraceWriterUtil
{
	public static String getTraceAsString(ITestResult result)
	{
		StringTraceWriter writer = new StringTraceWriter();
		TraceWriterUtil.writeTrace(result, writer);
		return writer.toString();
	}
	
	public static void writeTrace(ITestResult result, ITraceWriter writer)
	{
		String message = result.getMessage();
		switch(result.getResultType())
		{
			case PASSED:
				// I'm guesing this won't ever happen, but just in case
				return;
			case FAILED:
				writer.writeLine(String.format(
						"%1$s<%2$s:%3$d>",
						message != null ? message + " " : "",
						result.getFile(),
						result.getLine()),
						LineType.EXCEPTION);
				break;
			case ERROR:
				writer.writeLine(String.format(
						"%1$s%2$s",
						result.getExceptionType(),
						message != null ? ": " + message : ""),
						LineType.EXCEPTION);
		}
		IStackTraceElement[] stackTrace = result.getStackTrace();
		if(null != stackTrace && stackTrace.length > 0)
			displayStackTrace(stackTrace, writer);
	}
	
	private static void displayStackTrace(IStackTraceElement[] stackTrace,
			ITraceWriter writer)
	{
		for(IStackTraceElement ste : stackTrace)
		{
			if(ste.lineInfoFound())
			{
				writer.writeLine(String.format(
						" in %1$s <%2$s:%3$d>",
						ste.getFunction(),
						ste.getFile(),
						ste.getLine()),
						LineType.STACK_FRAME);
			}
			else
			{
				writer.writeLine(String.format(
						" in %1$s <0x%2$x>",
						ste.getFunction(),
						ste.getAddress()),
						LineType.STACK_FRAME);
			}
		}
	}
}
