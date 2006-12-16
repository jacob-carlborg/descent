package descent.ui.text.outline;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

import descent.core.dom.IModifiersContainer;
import descent.internal.core.dom.Modifier;
import descent.ui.DescentUI;
import descent.ui.IImages;

public class DOutlineLightweightLabelDecorator extends LabelProvider implements ILightweightLabelDecorator {
	
	private ImageDescriptor abstractOverlay;
	private ImageDescriptor finalOverlay;
	
	public DOutlineLightweightLabelDecorator() {
		abstractOverlay = DescentUI.getImageDescriptor(IImages.ABSTRACT);
		finalOverlay = DescentUI.getImageDescriptor(IImages.FINAL);
	}
	
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IModifiersContainer) {
			IModifiersContainer cont = (IModifiersContainer) element;
			Modifier m = cont.getModifier();
			if (m.isAbstract()) {
				decoration.addOverlay(abstractOverlay, IDecoration.TOP_RIGHT);
			}
			if (m.isFinal()) {
				decoration.addOverlay(finalOverlay, IDecoration.TOP_RIGHT);
			}
		}
	}
	
}
