package descent.internal.unittest.flute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import descent.internal.unittest.ui.JUnitMessages;

import static descent.internal.unittest.flute.FluteTestResult.ResultType;
import static descent.internal.unittest.flute.FluteTestResult.StackTraceElement;
import static descent.unittest.ITestResult.ResultType.PASSED;
import static descent.unittest.ITestResult.ResultType.FAILED;
import static descent.unittest.ITestResult.ResultType.ERROR;


class RunningOneTest implements IState
{
	private static final String PASSED_MSG = "PASSED"; //$NON-NLS-1$
	private static final String FAILED_MSG = "FAILED"; //$NON-NLS-1$
	private static final String ERROR_MSG = "ERROR"; //$NON-NLS-1$
	
	private static final Pattern ASSERTION_FAILURE = Pattern.compile(
			"Assertion failed in (\\S*) at line (\\d*)(?:\\: (.*))?"); //$NON-NLS-1$
	private static final Pattern ERROR_CONDITION = Pattern.compile(
			"Exception ([^\\:]*): (.*)"); //$NON-NLS-1$
	private static final Pattern STACK_TRACE_ELEMENT = Pattern.compile(
			"\\<\\<ST\\>\\> (.*) \\((?:(?:([^\\:]*)\\:(.*))|(?:0x(\\w*)))\\)"); //$NON-NLS-1$
	
	private static final StackTraceElement[] NO_STACK_TRACE =
		new StackTraceElement[] {};
	
	private final FluteApplicationInstance cli;
	
	// Are we appending output to the message buffer (to support multi-
	// line error messages)?
	private boolean isAppending = false;
	
	private ResultType resultType = null;
	private String file;
	private int line;
	private String exceptionType;
	private StringBuffer message;
	private List<StackTraceElement> stackTrace;
	private String internalError;
	
	RunningOneTest(FluteApplicationInstance cli)
	{
		this.cli = cli;
	}

	public void interpret(String text) throws IOException
	{
		// Implementation note: these conditions are order-dependent. I'm
		// not proud of this function, but I'm too lazy to do anything
		// about it.
		
		// If it's time to return, do it
		if(text.equals(FluteApplicationInstance.AWAITING_INPUT))
		{
			cli.notifyStateReturn();
			return;
		}
		
		// Check for a PASSED/FAILED/ERROR if the PASSED/FAILED/ERROR
		// hasn't been set yet (if it has been set the exception message
		// just might have that in it, so treat it as part of the message)
		if(null == resultType)
		{
			if(text.equals(PASSED_MSG))
				resultType = PASSED;
			if(text.equals(FAILED_MSG))
				resultType = FAILED;
			if(text.equals(ERROR_MSG))
				resultType = ERROR;
			
			if(null != resultType)
				return;
		}
		
		// If it's a stack trace element, add it to the stack trace
		Matcher m = STACK_TRACE_ELEMENT.matcher(text);
		if(m.find())
		{
			
			// If we've gotten to the stack trace, we're done with
			// processing error messages.
			isAppending = false;
			
			if(null == stackTrace)
				stackTrace = new ArrayList<StackTraceElement>(16);
			
			String function = m.group(1);
			String module = m.group(2);
			String lineStr = m.group(3);
			String addrStr = m.group(4);
			
			if(module != null)
			{
				int line;
				try
				{
					line = null != lineStr ? Integer.parseInt(lineStr) : -1;
				}
				catch(NumberFormatException e)
				{
					line = -1;
				}
				stackTrace.add(StackTraceElement.line(function, module, line));
			}
			else
			{
				long addr;
				try
				{
					addr = null != addrStr ? Long.parseLong(addrStr, 16) : -1;
				}
				catch(NumberFormatException e)
				{
					addr = -1;
				}
				stackTrace.add(StackTraceElement.address(function, addr));
			}
			
			return;
		}
		
		// If we're appending to the error message, tack this on (and put
		// the line break back in)
		if(isAppending)
		{
			message.append("\r\n" + text); //$NON-NLS-1$
			return;
		}
		
		// If it's an assertion failure, grab the file/line and message
		m = ASSERTION_FAILURE.matcher(text);
		if(m.find())
		{
			file = m.group(1);
			
			String lineStr = m.group(2);
			try
			{
				line = null != lineStr ? Integer.valueOf(lineStr) : -1;
			}
			catch(NumberFormatException e)
			{
				line = -1;
			}
			
			String msgStr = m.group(3);
			if(msgStr != null)
			{
				message = new StringBuffer();
				message.append(msgStr);
				isAppending = true;
			}
			
			return;
		}
		
		// If it's an error condition (unexpected exception), grab the
		// exception type and message
		m = ERROR_CONDITION.matcher(text);
		if(m.find())
		{
			exceptionType = m.group(1);
			
			String msgStr = m.group(2);
			if(msgStr != null)
			{
				message = new StringBuffer();
				message.append(msgStr);
				isAppending = true;
			}
			
			return;
		}
		
		// If we get here, it's probably invalid text. There's been an internal
		// error (this may desynchronize a lot of other stuff, but in that case
		// there's bigger stuff to worry about
		internalError = text;
		cli.notifyStateReturn();
	}
	
	FluteTestResult getResult()
	{
	    if(internalError != null)
	    {
	        return FluteTestResult.error(
	                String.format(JUnitMessages.RunningOneTest_internal_error_with_message,
	                        internalError),
	                "", //$NON-NLS-1$
	                NO_STACK_TRACE);
	    }
	    
		if(resultType == null)
		{   
			return FluteTestResult.error(
					JUnitMessages.RunningOneTest_internal_error,
					"", //$NON-NLS-1$
					NO_STACK_TRACE);
		}
		
		switch(resultType)
		{
			case PASSED:
				return FluteTestResult.passed();
			case FAILED:
				return FluteTestResult.failed(
						file,
						line, 
						message != null ? message.toString() : null, 
						stackTrace != null ?
								stackTrace.toArray(new StackTraceElement[stackTrace.size()]) :
								null);
			case ERROR:
				return FluteTestResult.error(
						message != null ? message.toString() : null,
						exceptionType,
						stackTrace != null ?
								stackTrace.toArray(new StackTraceElement[stackTrace.size()]) :
								null);
			default:
				throw new IllegalStateException();
		}
	}
}
