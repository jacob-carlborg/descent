package descent.ui.text.outline;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import descent.core.dom.IElement;
import descent.ui.text.DEditor;
import descent.ui.text.PositionHelper;

public class DEditorContentOutlinePage extends ContentOutlinePage {
	
	private DEditor editor;
	private IEditorInput input;
	private DOutlineContentProvider contentProvider;
	private DOutlineLabelProvider labelProvider;
	private DElementSorterByPosition sorterByPosition;
	// private DElementSorterByName sorterByName;
	
	int a = 2, b = 3;
	
	public DEditorContentOutlinePage(DEditor editor) {
		super();
		this.editor = editor;
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		TreeViewer viewer = getTreeViewer();
		
		contentProvider = new DOutlineContentProvider(editor);
		viewer.setContentProvider(contentProvider);
		
		labelProvider = new DOutlineLabelProvider();
		viewer.setLabelProvider(new DecoratingLabelProvider(labelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		
		sorterByPosition = new DElementSorterByPosition();
		viewer.setSorter(sorterByPosition);
		
		// sorterByName = new DElementSorterByName();
		
		viewer.addSelectionChangedListener(this);
		
		// Add actions
		// IToolBarManager mgr = getSite().getActionBars().getToolBarManager();
		// mgr.add(new ToggleOutlineSorter(viewer, sorterByPosition, sorterByName));

		//control is created after input is set
		update();
	}
	
	/**
	 * Sets the input of the outline page
	 */
	public void setInput(Object input)
	{
		this.input = (IEditorInput) input;
		update();		
	}
	
	/**
	 * The editor is saved, so we should refresh representation
	 * 
	 * @param tableNamePositions
	 */
	public void update()
	{
		//set the input so that the outlines parse can be called
		//update the tree viewer state
		TreeViewer viewer = getTreeViewer();
		if (viewer != null)
		{
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed())
			{
				viewer.removeSelectionChangedListener(this);
				control.setRedraw(false);
				viewer.setInput(input);
				viewer.expandAll();
				control.setRedraw(true);
				
				if (editor.getOutlineElement() != null) {
					selectElement(editor.getOutlineElement());
				}
				
				viewer.addSelectionChangedListener(this);
			}
		}
	}

	/**
	 * Selects the specified element.
	 */
	public void selectElement(IElement element) {
		TreeViewer viewer = getTreeViewer();
		viewer.removeSelectionChangedListener(this);
		viewer.setSelection(new StructuredSelection(element));
		viewer.reveal(element);
		viewer.addSelectionChangedListener(this);
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		
		ISelection selection = event.getSelection();
		if (!selection.isEmpty()) {
			IElement element = (IElement) ((IStructuredSelection) selection).getFirstElement();
			Position pos = PositionHelper.getElementOfInterest(element);
			editor.selectAndReveal(pos.getOffset(), pos.getLength());
		}
	}

}
