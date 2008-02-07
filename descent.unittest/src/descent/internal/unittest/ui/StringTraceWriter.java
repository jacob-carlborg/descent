package descent.internal.unittest.ui;

import descent.internal.unittest.ui.FailureTableDisplay.LineType;

public class StringTraceWriter implements ITraceWriter
{
	private static final StringBuffer buf = new StringBuffer();
	
	public void writeLine(String line, LineType type)
	{
		buf.append(line + "\n");
	}

	@Override
	public String toString()
	{
		return buf.toString();
	}
}
