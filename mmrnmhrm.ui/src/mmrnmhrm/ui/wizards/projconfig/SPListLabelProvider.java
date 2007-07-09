package mmrnmhrm.ui.wizards.projconfig;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.IElement;
import melnorme.util.ui.jface.SimpleLabelProvider;
import mmrnmhrm.core.model.lang.ILangSourceRoot;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.swt.graphics.Image;


public class SPListLabelProvider extends SimpleLabelProvider {

	
	public Image getImage(Object element) {
		Assert.isNotNull(element);
		Assert.isTrue(element instanceof ILangSourceRoot);
		return DeeElementImageProvider.getElementImage((IElement) element);
	}
}
