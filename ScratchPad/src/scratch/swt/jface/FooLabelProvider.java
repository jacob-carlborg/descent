package scratch.swt.jface;

import org.eclipse.jface.viewers.LabelProvider;

public class FooLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((FooElement) element).getName();
	}
}
