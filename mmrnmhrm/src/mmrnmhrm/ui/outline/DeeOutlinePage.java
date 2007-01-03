package mmrnmhrm.ui.outline;

import mmrnmhrm.ui.editors.DeeEditor;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class DeeOutlinePage extends ContentOutlinePage {

	private IEditorInput input;
	//private ITextEditor editor;
	
	public DeeOutlinePage(DeeEditor editor) {
		super();
		this.input = editor.getEditorInput();
		//this.editor = editor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		
		DeeOutlineContentProvider contentProvider;
		DeeOutlineLabelProvider labelProvider;
	
		contentProvider = new DeeOutlineContentProvider();
		viewer.setContentProvider(contentProvider);
		
		labelProvider = new DeeOutlineLabelProvider();
		viewer.setLabelProvider(labelProvider);
		
		viewer.addSelectionChangedListener(this);
		//control is created after input is set
		viewer.setInput(input);
		update();
		
	}
	
	
	/**
	 * Sets the input of the outline page
	 */
	/*public void setInput(Object input) {
		this.input = (IEditorInput) input;
		update();
	}*/

	public void update() {
		TreeViewer viewer = getTreeViewer();
		if (viewer != null) {
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);
				viewer.setInput(input);
				viewer.expandAll();
				control.setRedraw(true);
			}
		}
	
	}
	
	/*
	  public void selectionChanged(SelectionChangedEvent event)
	{
    super.selectionChanged(event);
    // find out which item in tree viewer we have selected, and set
    // highlight range accordingly

    ISelection selection = event.getSelection();
    if (selection.isEmpty())
        editor.resetHighlightRange();
    else
    {
        IStructuredSelection sel = (IStructuredSelection) selection;
        XMLElement element = (XMLElement) sel.getFirstElement();

        int start = element.getPosition().getOffset();
        int length = element.getPosition().getLength();
        try
        {
            editor.setHighlightRange(start, length, true);
        }
        catch (IllegalArgumentException x)
        {
            editor.resetHighlightRange();
        }
    } }*/


}
