package mmrnmhrm.ui.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
/**
 * A D partitioner that allways returns the same partition type.
 */
public class DeePartitionScanner_Fast implements IPartitionTokenScanner{

	private IToken deeDefaultPartitionType;
	//private IDocument document;
	//private int rangeStart;
	private int rangeEnd;
	private int offset;
	private int lastoffset;

	public DeePartitionScanner_Fast() {

		deeDefaultPartitionType = new Token(EDeePartitions.DEE_CODE);
	}
	

	public void setRange(IDocument document, int offset, int length) {
		//this.document = document;
		//this.rangeStart = offset;
		this.rangeEnd = offset + length;
		this.offset = offset;
	}
	

	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		// XXX: Jeez Eclipse, thank you for not telling that partitionOffset can be -1??
		if(partitionOffset == -1)
			setRange(document, offset, length);
		else
			setRange(document, partitionOffset, offset - partitionOffset + length);
		
	}
	

	public IToken nextToken() {
		lastoffset = offset;
		if(offset < rangeEnd) {
			offset = rangeEnd;
			return deeDefaultPartitionType;
		} else {
			return EOFToken.getDefault();
		}
			
	}

	public int getTokenLength() {
		//Assert.isTrue(offset < rangeEnd);
		return offset - lastoffset;
	}

	public int getTokenOffset() {
		return lastoffset;
	}

}
