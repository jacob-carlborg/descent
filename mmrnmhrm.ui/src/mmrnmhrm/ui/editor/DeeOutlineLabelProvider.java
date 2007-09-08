package mmrnmhrm.ui.editor;

import melnorme.miscutil.tree.IElement;
import melnorme.util.ui.jface.SimpleLabelProvider;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.swt.graphics.Image;

import dtool.dom.ast.ASTNeoNode;



public class DeeOutlineLabelProvider extends SimpleLabelProvider {

	public Image getImage(Object element) {
		return DeeElementImageProvider.getElementImage((IElement) element);
	}

	public String getText(Object elem) {
		return ((ASTNeoNode) elem).toStringAsElement();
	}
}