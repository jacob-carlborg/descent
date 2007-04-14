package mmrnmhrm.ui.wizards.projconfig;

import mmrnmhrm.core.model.IDeeSourceRoot;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import util.Assert;

public class BPListLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		Assert.isNotNull(element);
		Assert.isTrue(element instanceof IDeeSourceRoot);
		return DeePluginImages.getImage(DeePluginImages.ELEM_SOURCEFOLDER);
	}
}
