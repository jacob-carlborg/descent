package descent.internal.ui.typehierarchy;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;

import descent.core.IJavaElement;

import descent.internal.ui.packageview.SelectionTransferDropAdapter;
import descent.internal.ui.util.OpenTypeHierarchyUtil;
import descent.internal.ui.util.SelectionUtil;

public class TypeHierarchyTransferDropAdapter extends SelectionTransferDropAdapter {

	private static final int OPERATION = DND.DROP_LINK;
	private TypeHierarchyViewPart fTypeHierarchyViewPart;

	public TypeHierarchyTransferDropAdapter(TypeHierarchyViewPart viewPart, AbstractTreeViewer viewer) {
		super(viewer);
		setFullWidthMatchesItem(false);
		fTypeHierarchyViewPart= viewPart;
	}

	public void validateDrop(Object target, DropTargetEvent event, int operation) {
		event.detail= DND.DROP_NONE;
		initializeSelection();
		if (target != null){
			super.validateDrop(target, event, operation);
			return;
		}	
		if (getInputElement(getSelection()) != null) 
			event.detail= TypeHierarchyTransferDropAdapter.OPERATION;
	}
		
	/* (non-Javadoc)
	 * @see descent.internal.ui.packageview.SelectionTransferDropAdapter#isEnabled(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public boolean isEnabled(DropTargetEvent event) {
		return true;
	}	

	public void drop(Object target, DropTargetEvent event) {
		if (target != null || event.detail != TypeHierarchyTransferDropAdapter.OPERATION){
			super.drop(target, event);
			return;
		}	
		IJavaElement input= getInputElement(getSelection());
		fTypeHierarchyViewPart.setInputElement(input);
	}
	
	private static IJavaElement getInputElement(ISelection selection) {
		Object single= SelectionUtil.getSingleElement(selection);
		if (single == null)
			return null;
		IJavaElement[] candidates= OpenTypeHierarchyUtil.getCandidates(single);
		if (candidates != null && candidates.length > 0) 
			return candidates[0];
		return null;
	}
}
