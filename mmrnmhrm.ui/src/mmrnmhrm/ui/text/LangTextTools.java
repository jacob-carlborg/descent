package mmrnmhrm.ui.text;

import melnorme.miscutil.log.Logg;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;


public class LangTextTools {
	

	private static LangTextTools instance = new LangTextTools();

	public static LangTextTools getInstance() {
		return instance;
	}


	public void setupLangDocumentPartitioner(IDocument document, String partitioning) {
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
			IDocumentPartitioner partitioner = createDocumentPartitioner();
			extension3.setDocumentPartitioner(partitioning, partitioner);
			partitioner.connect(document);
			String str = document.get();
			Logg.codeScanner.println(" Setup Document Partitioning: ", 
					str.length() > 20 ? str.subSequence(0, 20) : str);
		}
	}

	protected IDocumentPartitioner createDocumentPartitioner() {
		return new FastPartitioner(
				getPartitionScanner(), IDeePartitions.legalContentTypes);
	}

	private IPartitionTokenScanner getPartitionScanner() {
		return new DeePartitionScanner();
	}

}
