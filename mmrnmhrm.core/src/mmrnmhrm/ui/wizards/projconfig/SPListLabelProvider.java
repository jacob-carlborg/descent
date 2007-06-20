package mmrnmhrm.ui.wizards.projconfig;

import melnorme.util.ui.jface.SimpleLabelProvider;
import mmrnmhrm.core.model.lang.ILangSourceRoot;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.swt.graphics.Image;

import util.Assert;
import util.tree.IElement;

public class SPListLabelProvider extends SimpleLabelProvider {

	
	public Image getImage(Object element) {
		Assert.isNotNull(element);
		Assert.isTrue(element instanceof ILangSourceRoot);
		return DeeElementImageProvider.getElementImage((IElement) element);
	}
}
