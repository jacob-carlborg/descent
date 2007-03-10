package mmrnmhrm.ui.outline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import dtool.dom.ast.ASTPrinter;
import dtool.dom.base.ASTNode;


public class DeeOutlineLabelProvider implements ILabelProvider {

	public void addListener(ILabelProviderListener listener) {
	}
	
	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getImage(Object element) {
		return DeeElementImageProvider.getLabelImage(element);
	}

	public String getText(Object elem) {
		return ASTPrinter.toStringElement((ASTNode) elem);
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}


	
	public void dispose() {
	}
	
}