package descent.ui.text;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import descent.ui.DescentUI;
import descent.ui.text.scanners.DPartitionerScanner;

public class DDocumentProvider extends FileDocumentProvider {
	
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new MyPartitioner(
					new DPartitionerScanner(),
					DPartitionerScanner.LEGAL_CONTENT);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
	
	class MyPartitioner extends FastPartitioner {
		
		public MyPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) {
			super(scanner, legalContentTypes);
		}
		
		@Override
		public void connect(IDocument document, boolean delayInitialization) {
			super.connect(document, delayInitialization);
			//printPartitions(document);
		}

		public void printPartitions(IDocument document)
		{
		    StringBuffer buffer = new StringBuffer();

		    ITypedRegion[] partitions = computePartitioning(0, document.getLength());
		    for (int i = 0; i < partitions.length; i++)
		    {
		        try
		        {
		            buffer.append("Partition type: " 
		              + partitions[i].getType() 
		              + ", offset: " + partitions[i].getOffset()
		              + ", length: " + partitions[i].getLength());
		            buffer.append("\n");
		            buffer.append("Text:\n");
		            buffer.append(document.get(partitions[i].getOffset(), 
		             partitions[i].getLength()));
		            buffer.append("\n---------------------------\n\n\n");
		        }
		        catch (BadLocationException e)
		        {
		        	DescentUI.log(e);
		        }
		    }
		    System.out.print(buffer);
		}
		
	}

}
