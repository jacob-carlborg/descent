package mmrnmhrm.ui.views;

import util.tree.IElement;
import melnorme.util.ui.jface.ElementContentProvider;

public class ASTViewerContentProvider extends ElementContentProvider {
	
	ASTViewer view;

	public ASTViewerContentProvider(ASTViewer view) {
		this.view = view;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		IElement input;
		if(view.fUseOldAst == true)
			input = view.fCUnit.getOldModule();
		else
			input = view.fCUnit.getModule();
		if(input == null)
			return null;
			//return IElement.NO_ELEMENTS;

		return input.getChildren();
	}

}
