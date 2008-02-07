package descent.internal.unittest.ui;

import descent.internal.unittest.ui.FailureTableDisplay.LineType;

public interface ITraceWriter
{
	public void writeLine(String line, LineType type);
}