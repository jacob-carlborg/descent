package mmrnmhrm.ui.outline;

import mmrnmhrm.ui.deditor.DeeEditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import dtool.dom.base.ASTNode;

/**
 * D outline page. 
 * XXX: Does an input change in the outlines lifecycle??
 */
public class DeeContentOutlinePage extends ContentOutlinePage {

	private DeeEditor editor;
	
	public DeeContentOutlinePage(DeeEditor editor) {
		super();
		this.editor = editor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new DeeOutlineContentProvider());
		viewer.setLabelProvider(new DeeOutlineLabelProvider());

		//viewer.addSelectionChangedListener(this);
		updateInput();
	}

	public void updateInput() {
		getTreeViewer().setInput(editor.getEditorInput());
		update();
	}
	
	public void update() {
		TreeViewer viewer = getTreeViewer();
		viewer.getControl().setRedraw(false);
		viewer.refresh();
		viewer.expandAll();
		viewer.getControl().setRedraw(true);
	}
	
	
	public void selectionChanged(SelectionChangedEvent event) {
		// fire event
		super.selectionChanged(event);
		// react to event
		onSelectionChanged(event);
	}

	private void onSelectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection.isEmpty())
			editor.resetHighlightRange();
		else {
			IStructuredSelection sel = (IStructuredSelection) selection;
			ASTNode element = (ASTNode) sel.getFirstElement();

			// Use parent for source range
			while(element.hasNoSourceRangeInfo())
				element = element.parent;
			
			int start = element.getOffset();
			int offset = element.getLength();
			try {
				editor.setHighlightRange(start, offset, true);
				editor.setSelection(start, offset);
			} catch (IllegalArgumentException x) {
				editor.resetHighlightRange();
			}
		}
	}
}
