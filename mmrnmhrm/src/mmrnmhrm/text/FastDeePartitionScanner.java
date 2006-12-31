package mmrnmhrm.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.*;

public class FastDeePartitionScanner implements ITokenScanner{

	public FastDeePartitionScanner() {

		IToken deeDefault = new Token(EDeePartitions.DEE_DEFAULT);
		IToken deeDoc = new Token(EDeePartitions.DEE_DOC);

	}

	public int getTokenLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTokenOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IToken nextToken() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRange(IDocument document, int offset, int length) {
		// TODO Auto-generated method stub
		
	}
}
