package mmrnmhrm.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import util.Logg;


/**
 * Simple extension of DefaultPartitioner with printPartitions() method to
 * assist with printing out partition information
 */
public class DebugPartitioner extends FastPartitioner
{

	public DebugPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) {
		super(scanner, legalContentTypes);
	}


	public void connect(IDocument document, boolean delayInitialization) {
		super.connect(document, delayInitialization);
		// TODO: learn partitioning lifecycle
			
		Logg.print(toStringPartitions(document));
	}

	public static String toStringPartitions(IDocument document) {
		
		ITypedRegion[] partitions;
		try {
			partitions = document.computePartitioning(0, document.getLength());
		} catch (BadLocationException e1) {
			return "ERROR: BadLocationException";
		}
		
		StringBuffer buffer = new StringBuffer();
	
		for (int i = 0; i < partitions.length; i++)	{
			try	{
				buffer.append("======== type: " + partitions[i].getType() 
						+ " range: " + partitions[i].getOffset()
						+ ", " + partitions[i].getLength());
				buffer.append(" ========\n");
				String str = document.get(partitions[i].getOffset(), partitions[i].getLength());
				buffer.append(str.replaceAll("\r\n", "\n"));
				buffer.append("\n");
				//buffer.append("\n---------------------------\n");
			}
			catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return buffer.toString(); 
	}
}
