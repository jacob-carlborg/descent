package descent.internal.ui.search;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Color;

public class ColorDecoratingLabelProvider extends DecoratingLabelProvider implements IColorProvider {

	public ColorDecoratingLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
	}

	public Color getForeground(Object element) {
		ILabelProvider labelProvider = getLabelProvider();
		if (labelProvider instanceof IColorProvider)
			return ((IColorProvider)labelProvider).getForeground(element);
		return null;
	}

	public Color getBackground(Object element) {
		ILabelProvider labelProvider = getLabelProvider();
		if (labelProvider instanceof IColorProvider)
			return ((IColorProvider)labelProvider).getBackground(element);
		return null;
	}
}
