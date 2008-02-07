package descent.internal.unittest.ui;

import descent.internal.unittest.ui.FailureTableDisplay.LineType;

public class TableTraceWriter implements ITraceWriter
{
	private static final int MAX_LABEL_LENGTH = 256;
	
	private final FailureTableDisplay table;
	
	TableTraceWriter(FailureTableDisplay table)
	{
		this.table = table;
	}
	
	public void writeLine(String line, LineType type)
	{
		final int labelLength = line.length();
		if (labelLength < MAX_LABEL_LENGTH)
		{
			table.addTraceLine(type, line);
		}
		else
		{
			// workaround for bug 74647: JUnit view truncates
			// failure message
			table.addTraceLine(type, line.substring(0, MAX_LABEL_LENGTH));
			int offset = MAX_LABEL_LENGTH;
			while (offset < labelLength) {
				int nextOffset = Math.min(labelLength, offset + MAX_LABEL_LENGTH);
				table.addTraceLine(LineType.NORMAL, line.substring(offset,
						nextOffset));
				offset = nextOffset;
			}
		}
	}
}
