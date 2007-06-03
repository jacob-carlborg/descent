package mmrnmhrm.ui.editor.outline;

import melnorme.util.ui.jface.SimpleLabelProvider;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.swt.graphics.Image;

import util.tree.IElement;


public class DeeOutlineLabelProvider extends SimpleLabelProvider {

	public Image getImage(Object element) {
		return DeeElementImageProvider.getElementImage((IElement) element);
	}

	public String getText(Object elem) {
		return elem.toString();
	}

}