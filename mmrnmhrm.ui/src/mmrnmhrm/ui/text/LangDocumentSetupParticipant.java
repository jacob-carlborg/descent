package mmrnmhrm.ui.text;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;

public class LangDocumentSetupParticipant implements IDocumentSetupParticipant {

	public void setup(IDocument document) {
		LangTextTools tools= LangTextTools.getInstance();
		tools.setupLangDocumentPartitioner(document, IDeePartitions.DEE_PARTITIONING);
	}

}
