package descent.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;

/**
 * An action to 
 * @author Familia
 *
 */
public class ToggleOutlineSorter extends Action {
	
	private StructuredViewer viewer;
	private ViewerSorter normalSorter;
	private ViewerSorter lexicalSorter;
	
	public ToggleOutlineSorter(StructuredViewer viewer, ViewerSorter normalSorter, ViewerSorter lexicalSorter) {
		super("Sort", SWT.CHECK);
		
		this.viewer = viewer;
		this.normalSorter = normalSorter;
		this.lexicalSorter = lexicalSorter;
	}
	
	@Override
	public void run() {
		if (isChecked()) {
			viewer.setSorter(lexicalSorter);
		} else {
			viewer.setSorter(normalSorter);
		}
	}
	
}
