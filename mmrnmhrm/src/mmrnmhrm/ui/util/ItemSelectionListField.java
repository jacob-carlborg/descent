package mmrnmhrm.ui.util;

import org.eclipse.jdt.internal.ui.util.PixelConverter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;

public class ItemSelectionListField extends DialogField {

	/** Category: named class to hold a list of ColoringListItem. */
	public static class SelectionListCategory {
		public String name;
		public SelectionListItem[] items;
		
		public SelectionListCategory(String name, SelectionListItem[] items) {
			this.name = name;
			this.items = items;
		}
		
		public String toString() {
			return name;
		}
	}

	/** A configurable unit of code syntax coloring. */
	public static class SelectionListItem {
		public String name;
		
		public SelectionListItem(String name) {
			this.name = name;
		}
		
		public String toString() {
			return name;
		}

	}
	
	/** Content provider for the coloring items and categories. */
	private class DeeColoringContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object inputElement) {
			return catRoot;
		}
		
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof SelectionListCategory) {
				SelectionListCategory elem = (SelectionListCategory) parentElement;
				return elem.items;
			}
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return element instanceof SelectionListCategory;
		}


		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	/** Tree viewer for the item selection */
	private TreeViewer fTreeViewer;
	/** the tree Control */
	private Tree fTreeControl;
	
	/** Root array holding all coloring categories */
	protected SelectionListCategory[] catRoot;
	
	
	public ItemSelectionListField(SelectionListCategory[] catRoot) {
		this.catRoot = catRoot;

	}
	
	/** Perfoms a fill without specifying layout data. */
	public Control[] doFillWithoutGrid(Composite parent) {
		Composite content = createContent(parent);

		return new Control[] { content };
	}
	
	@Override
	public Control[] doFillIntoGrid(Composite parent, int nColumns) {
		assertEnoughColumns(nColumns);
		
		Composite content = createContent(parent);
		GridData gd = new GridData();
		gd.horizontalSpan= nColumns;
		gd.horizontalAlignment= GridData.FILL;
		content.setLayoutData(gd);
		return new Control[] { content };
	}

	private Composite createContent(Composite parent) {
		Composite content = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout(1, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		content.setLayout(gl);
		
		Label label= getLabelControl(content);
		label.setLayoutData(gridDataForLabel(1));
		
		TreeViewer treeViewer = getSelectionTreeViewer(content);
		
		PixelConverter converter= new PixelConverter(parent);
		GridData gd = new GridData();
		gd.widthHint = converter.convertWidthInCharsToPixels(25);
		gd.heightHint = converter.convertHeightInCharsToPixels(10);
		treeViewer.getControl().setLayoutData(gd);
		return content;
	}

	public TreeViewer getSelectionTreeViewer(Composite parent) {
		if(fTreeViewer == null) {
			assertCompositeNotNull(parent);

			fTreeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.BORDER);
		    fTreeViewer.setLabelProvider(new LabelProvider());
		    fTreeViewer.setContentProvider(new DeeColoringContentProvider());
		    fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					dialogFieldChanged();
				}
		    });
		    fTreeViewer.setInput(this); // input doesn't matter
		    fTreeViewer.expandAll();
		} 
		return fTreeViewer;
		
	}
	
	public Tree getTreeControl(Composite parent) {
		return (Tree) getSelectionTreeViewer(parent).getControl();
	}
	
	@Override
	protected void updateEnableState() {
		super.updateEnableState();
		if (isOkToUse(fTreeControl)) {
			fTreeControl.setEnabled(isEnabled());
		}	
	}
	
	/** Return the selected non-category element, or null if none is selected */
	public SelectionListItem getSelectedItem() {
		IStructuredSelection selection; 
		selection = (IStructuredSelection) fTreeViewer.getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof SelectionListItem)
			return (SelectionListItem) element;
		return null;
	}
}
