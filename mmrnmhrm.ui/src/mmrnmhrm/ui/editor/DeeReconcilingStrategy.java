package mmrnmhrm.ui.editor;

import melnorme.miscutil.ExceptionAdapter;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

public class DeeReconcilingStrategy implements IReconcilingStrategy {

	private IDocument document;
	
	public void setDocument(IDocument document) {
		this.document = document;
	}
	
	public void reconcile(IRegion partition) {
		if(true)
			return; // TODO
		
		try {
			document.get(partition.getOffset(), partition.getLength());
		} catch (BadLocationException e) {
			throw ExceptionAdapter.unchecked(e);
		}
		//Logg.model.println("Reconcile:", partition);
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		//Logg.model.println("Reconcile:", dirtyRegion, " , ", subRegion);

	}



}
