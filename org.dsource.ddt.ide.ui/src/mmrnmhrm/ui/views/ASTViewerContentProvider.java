package mmrnmhrm.ui.views;

import melnorme.miscutil.tree.IElement;
import melnorme.util.ui.jface.ElementContentProvider;
import mmrnmhrm.core.dltk.DeeParserUtil;

public class ASTViewerContentProvider extends ElementContentProvider {
	
	ASTViewer view;

	public ASTViewerContentProvider(ASTViewer view) {
		this.view = view;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		IElement input;
		if(view.fUseOldAst == true)
			input = DeeParserUtil.parseModule(view.fCUnit.modUnit).dmdModule;
		else
			input = view.fCUnit.getModule();
		if(input == null)
			return null;
			//return IElement.NO_ELEMENTS;

		return input.getChildren();
	}

}
