package mmrnmhrm.ui.wizards.projconfig;

import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.DeeSourceLib;
import mmrnmhrm.core.model.ILangSourceRoot;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import util.Assert;

public class BPListLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		Assert.isNotNull(element);
		Assert.isTrue(element instanceof ILangSourceRoot);
		if(element instanceof DeeSourceFolder)
			return DeePluginImages.getImage(DeePluginImages.ELEM_SOURCEFOLDER);
		if(element instanceof DeeSourceLib)
			return DeePluginImages.getImage(DeePluginImages.ELEM_LIBRARY);
		return null;
	}
}
