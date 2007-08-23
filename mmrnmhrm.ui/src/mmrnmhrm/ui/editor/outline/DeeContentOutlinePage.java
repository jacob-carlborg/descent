package mmrnmhrm.ui.editor.outline;

import melnorme.lang.ui.EditorUtil;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import dtool.dom.definitions.Module;


/**
 * D outline page. 
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
		Module module = EditorUtil.getNeoModuleFromEditor(editor);
		TreeViewer viewer = getTreeViewer();
		viewer.getControl().setRedraw(false);
		viewer.setInput(module);
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
