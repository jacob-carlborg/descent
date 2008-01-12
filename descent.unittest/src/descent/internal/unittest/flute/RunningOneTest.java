package descent.internal.unittest.flute;

import java.io.IOException;

import static descent.internal.unittest.flute.FluteTestResult.ResultType;
import static descent.internal.unittest.flute.FluteTestResult.ResultType.PASSED;
import static descent.internal.unittest.flute.FluteTestResult.ResultType.FAILED;
import static descent.internal.unittest.flute.FluteTestResult.ResultType.ERROR;


class RunningOneTest implements IState
{
	private static final String PASSED_MSG = "PASSED";
	private static final String FAILED_MSG = "FAILED";
	private static final String ERROR_MSG = "ERROR";
	
	private static final String ASSERTION_FAILED_MSG = "Assertion failed in ";
	private static final String AT_LINE              = " at line ";
	
	private final FluteApplicationInstance cli;
	private ResultType resultType = null;
	
	RunningOneTest(FluteApplicationInstance cli)
	{
		this.cli = cli;
	}

	public void interpret(String text) throws IOException
	{	
		// If it's time to return, do it
		if(text.equals(FluteApplicationInstance.AWAITING_INPUT))
		{
			cli.notifyStateReturn();
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
		
		// If it's an assertion failure, grab the file/line and message
		if(text.startsWith(ASSERTION_FAILED_MSG) && resultType == FAILED)
		{
			text = text.substring(ASSERTION_FAILED_MSG.length());
			System.out.println("Chopped 1: " + text);
			int i = text.indexOf(AT_LINE);
			assert(i > 0);
			String file = text.substring(0, i);
			System.out.println("File: " + file);
			text = text.substring(i + AT_LINE.length());
			System.out.println("Chopped 2: " + text);
			i = text.indexOf(" ");
			assert(i > 0);
			String lineNum = text.substring(0, i);
			int line;
			try
			{
				line = Integer.valueOf(lineNum);
			}
			catch(NumberFormatException e)
			{
				line = -1;
			}
			System.out.println("Line: " + line);
		}
		// TODO finish
	}
	
	FluteTestResult getResult()
	{
		if(resultType == null)
			return null;
		
		return new FluteTestResult(resultType);
	}
}
