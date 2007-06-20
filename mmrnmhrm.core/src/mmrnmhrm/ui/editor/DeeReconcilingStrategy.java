package mmrnmhrm.ui.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

public class DeeReconcilingStrategy implements IReconcilingStrategy {

	public void reconcile(IRegion partition) {
		//Logg.model.println("Reconcile:", partition);
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		//Logg.model.println("Reconcile:", dirtyRegion, " , ", subRegion);

	}

	public void setDocument(IDocument document) {
		// TODO Auto-generated method stub
	}

}
