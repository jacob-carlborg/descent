package melnorme.miscutil.log;

import java.io.PrintWriter;

public class WriterPrinter extends AbstractPrinter {

	private PrintWriter pw;

	public WriterPrinter(PrintWriter pw) {
		this.pw = pw;
	}

	@Override
	public void print(String str) {
		pw.print(str);
	}

	@Override
	public void println() {
		pw.println();
	}

}
