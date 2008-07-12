package descent.internal.unittest.ui;

import descent.core.Signature;
import descent.internal.unittest.ui.FailureTableDisplay.LineType;
import descent.unittest.IStackTraceElement;
import descent.unittest.ITestResult;

public class TraceUtil
{	
	public static String getModuleSignature(String moduleName)
	{
		StringBuffer sig = new StringBuffer();
		sig.append(Signature.C_MODULE);
		
		// Split takes a regex, so String.split(".") doesn't work
		String[] fragments = moduleName.split("\\."); //$NON-NLS-1$
		for(String fragment : fragments)
		{
			sig.append(String.valueOf(fragment.length()));
			sig.append(fragment);
		}
		
		return sig.toString();
	}
	
	public static String getTraceAsString(ITestResult result)
	{
		StringTraceWriter writer = new StringTraceWriter();
		TraceUtil.writeTrace(result, writer);
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
						"<%1$s:%2$d> %3$s", //$NON-NLS-1$
						result.getFile(),
						result.getLine(),
						message != null ? message : ""), //$NON-NLS-1$
						LineType.EXCEPTION);
				break;
			case ERROR:
				writer.writeLine(String.format(
						"%1$s%2$s", //$NON-NLS-1$
						result.getExceptionType(),
						message != null ? ": " + message : ""), //$NON-NLS-1$ //$NON-NLS-2$
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
						"<%1$s:%2$d> %3$s", //$NON-NLS-1$
						ste.getModule(),
						ste.getLine(),
						ste.getFunction()),
						LineType.STACK_FRAME);
			}
			else
			{
				writer.writeLine(String.format(
						"<0x%1$x> %2$s", //$NON-NLS-1$
						ste.getAddress(),
						ste.getFunction()),
						LineType.STACK_FRAME);
			}
		}
	}
}
