package mmrnmhrm.ui.views;

import melnorme.util.ui.jface.ElementContentProvider;

import org.eclipse.jface.viewers.IContentProvider;

import util.tree.IElement;

public class ASTViewerContentProvider extends ElementContentProvider implements
		IContentProvider {
	
	ASTViewer view;


	public ASTViewerContentProvider(ASTViewer view) {
		this.view = view;
	}
	
	public Object[] getElements(Object inputElement) {
		if(view.fRoot != null)
			return getChildren(view.fRoot.getModule());
		return IElement.NO_ELEMENTS;
	}

}
