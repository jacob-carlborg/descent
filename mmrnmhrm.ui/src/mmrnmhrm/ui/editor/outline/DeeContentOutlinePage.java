package mmrnmhrm.ui.editor.outline;

import melnorme.lang.ui.EditorUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;


/**
 * D outline page. 
 * XXX: Does an input change in the outline's lifecycle?? -> Yes it can
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

		updateView();
	}

	public void updateView() {
		CompilationUnit cunit = editor.getCompilationUnit();
		TreeViewer viewer = getTreeViewer();
		viewer.getControl().setRedraw(false);
		viewer.setInput(cunit.getModule());
		viewer.refresh();
		//viewer.expandAll();
		viewer.getControl().setRedraw(true);
	}
	
	
	public void selectionChanged(SelectionChangedEvent event) {
		// fire event
		super.selectionChanged(event);
		// react to event
		onSelectionChanged(event);
	}

	private void onSelectionChanged(SelectionChangedEvent event) {
		EditorUtil.selectNodeInEditor(editor, event);
	}
}
