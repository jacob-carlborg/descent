package mmrnmhrm.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import util.Logger;


/**
 * Simple extension of DefaultPartitioner with printPartitions() method to assist with printing out partition
 * information
 * 
 * @author Phil Zoio
 */
public class DebugPartitioner extends FastPartitioner
{

	public DebugPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) {
		super(scanner, legalContentTypes);
	}


	public void connect(IDocument document, boolean delayInitialization) {
		super.connect(document, delayInitialization);
		if(Logger.debug)
			Logger.print(printPartitions(document));
			
	}

	public static String printPartitions(IDocument document)	{
		StringBuffer buffer = new StringBuffer();
		
		ITypedRegion[] partitions;
		try {
			partitions = document.computePartitioning(0, document.getLength());
		} catch (BadLocationException e1) {
			return "ERROR: BadLocationException";
		}
		for (int i = 0; i < partitions.length; i++)	{
			try	{
				buffer.append("Partition type: " + partitions[i].getType() + ", offset: " + partitions[i].getOffset()
						+ ", length: " + partitions[i].getLength());
				buffer.append("\n");
				buffer.append("Text:\n");
				buffer.append(document.get(partitions[i].getOffset(), partitions[i].getLength()));
				buffer.append("\n---------------------------\n");
			}
			catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return buffer.toString(); 
	}
}
