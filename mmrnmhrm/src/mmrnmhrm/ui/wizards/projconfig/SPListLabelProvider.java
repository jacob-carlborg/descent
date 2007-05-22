package mmrnmhrm.ui.wizards.projconfig;

import melnorme.util.ui.jface.SimpleLabelProvider;
import mmrnmhrm.core.model.ILangSourceRoot;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.swt.graphics.Image;

import dtool.dom.ast.ASTNode;

import util.Assert;

public class SPListLabelProvider extends SimpleLabelProvider {

	
	public Image getImage(Object element) {
		Assert.isNotNull(element);
		Assert.isTrue(element instanceof ILangSourceRoot);
		return DeeElementImageProvider.getElementImage((ASTNode) element);
	}
}
