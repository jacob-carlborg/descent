package melnorme.miscutil.log;

import java.io.PrintStream;

public class StreamPrinter extends AbstractPrinter {

	private PrintStream ps;

	public StreamPrinter(PrintStream ps) {
		this.ps = ps;
	}

	@Override
	public void print(String str) {
		ps.print(str);
	}

	@Override
	public void println() {
		ps.println();
	}

}
