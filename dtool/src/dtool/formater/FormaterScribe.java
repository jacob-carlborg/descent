package dtool.formater;

import java.io.OutputStream;
import java.io.PrintStream;

import util.ExceptionAdapter;
import util.StringUtil;
import descent.internal.core.dom.Token;
import dtool.dom.base.ASTElement;


public class FormaterScribe {
	
	private PrintStream out;
	private char[] source; 
	private int sourcepos;
	
	private ScannerHelper scanner;
	

	public FormaterScribe(OutputStream out, char[] sbuf) {
		this.out = new PrintStream(out);
		this.source = sbuf;
		this.sourcepos = 0;
		
		this.scanner = new ScannerHelper(this, this.sourcepos, this.source);
	}

	public void write(String str) {
		out.print(str); 
		out.flush();
	}
	
	private void writeNewLine() {
		write(FormaterOptions.newline);
	}
	
	public void advancePos(int posinc) {
		this.sourcepos += posinc;
	}
	
	public void skipToPos(int offset) {
		this.sourcepos = offset;
	}

	public void writeToPos(int newpos) {
		while(sourcepos != newpos) {
			out.print(source[sourcepos++]);
		}
	}
	
	boolean requireNL;
	int requireNLpos;
	
	public void requireNewLine(ASTElement element) {
		requireNewLine(element.getOffset() + element.getLength() );
	}

	public void requireNewLine(int pos) {
		requireNL = true;
		firstInLine = false;
		requireNLpos = pos;
	}
	
	private boolean firstInLine = true;
	int lastStartLinePos = 0;

	public void eventNewLine(int pos) {
		firstInLine = true;
		lastStartLinePos = pos;
	}
	
	int indentLevel = 0;
	
	public void indent(int endpos) {
		if(requireNL && !firstInLine) {
			writeNewLine();
			requireNL = false;
		}
		write(StringUtil.newFilledString(indentLevel, "\t"));
		skipToPos(endpos);
	}

	/* ****************************************** */
	
	public void writeToElement(ASTElement elem) {
		writeToPos(elem.getOffset());
	}
	
	public void writeElement(ASTElement elem) {
		writeToPos(elem.getOffset());
		write(new String(source, elem.getOffset(), elem.getLength())); 
		advancePos(elem.getLength());
	}
	
	public void synchToElementAndIndent(ASTElement element) {
		synchToElement(element); 
		indent(element.getOffset()); 
	}
	
	public void synchToElement(ASTElement element) {
		int targetpos = element.getOffset();
//		System.out.println(source[targetpos]);

		scanner.pos = sourcepos;

		while(scanner.pos < targetpos) {
			Token tok = scanner.nextToken();
			
			if(scanner.isWhiteSpace(tok)) {
				if(lastStartLinePos > sourcepos)
					writeToPos(lastStartLinePos);
				continue;
			}
			if(scanner.isSLComment(tok)) {
				writeToPos(scanner.pos);
				requireNL = false;
				continue;
			}
			if(scanner.isMLComment(tok) || scanner.isNestedComment(tok)) {
				//TODO: indent comment ?
				indent(tok.ptr);
				writeToPos(scanner.pos);
				requireNewLine(scanner.pos);
				continue;
			}		

			throw new ExceptionAdapter(
					new Exception("synch:Unexpected Token:" + tok));
		}
		// TODO: assert tokens match?

	}



}

