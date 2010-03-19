package foo;

import static melnorme.miscutil.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class VirtualTreeViewer {
	
	protected final Tree tree;
	protected IVirtualTreeContentProvider elementProvider;
	protected LabelProvider labelProvider;
	protected Object input;

	public VirtualTreeViewer(Tree tree, IVirtualTreeContentProvider elementProvider, LabelProvider labelProvider) {
		this.tree = tree;
		this.elementProvider = elementProvider;
		this.labelProvider = labelProvider;
	}
	
	public void install() {
		tree.addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event event) {
				VirtualTreeViewer.this.handleSetData(event);
			}
		});
	}
	
	public void setInput(Object input) {
		this.input = input;
	}
	
	public void handleSetData(Event event) {
		materializeItem((TreeItem) event.item, event.index);
	}
		
	private void materializeItem(TreeItem item, int index) {
//		assertTrue(isValid());
		TreeItem parentTreeItem = item.getParentItem();
		
		
		Object parentElement = (parentTreeItem == null) ? input : parentTreeItem.getData();
		assertNotNull(parentElement, "Parent item has not been materialized yet");
		Object element = safeGetElement(parentElement, index);
		assertNotNull(element);
		
		Tree tree = item.getParent();
		tree.setRedraw(false);
		try {				
			setupItemData(item, element);
			item.setData(element);
//			item.setExpanded(element.isExpanded());
			item.setExpanded(false);
			virtualExpand(item, element);
		} finally {
			tree.setRedraw(true);
		}
		return;
	}

	private Object safeGetElement(Object parent, int index) {
		if(parent == null || index < 0 || !(parent instanceof Object[]) ||
				index >= ((Object[]) parent).length)
			return null;
		return ((Object[]) parent)[index];
	}
	
	/** Does a recursive virtual expand: sets the itemcount of children, in a virtual way, avoiding calling SetData */
	private void virtualExpand(TreeItem item, Object element) {
		
		int itemCount = (element instanceof Object[]) ? ((Object[])element).length : 0;
					
		if(itemCount == 0) {
			item.setItemCount(0);
		} else {
			boolean isExpanded = false;

			if(isExpanded) {
				item.setItemCount(itemCount);
			} else {
				item.setItemCount(1); // Don't set the real itemCount, for performance reasons
			}
			item.setExpanded(isExpanded);
				
			if(isExpanded) {
				TreeItem[] items = item.getItems();
				for (int i = 0; i < items.length; i++) {
					TreeItem childItem = items[i];
//					materializeItem(childItem, i);
					Object childElement = safeGetElement(element, i);
					virtualExpand(childItem, childElement);
				}
			}
		}
	}
	

	private void setupItemData(final TreeItem item, Object element) {

		List<Integer> indexPath = VirtualTreeViewer.getTreeItemIndexPath(item);
		System.out.println(indexPath);
		
		if(element instanceof Object[]) {
			final String itemText = "Node: " + indexPath +"  "+ System.currentTimeMillis() % 10000;
			item.setText(itemText);

			System.out.println("Materialized: " + itemText + " Itemcount: " + ((Object[]) element).length);
			item.setItemCount(((Object[]) element).length);
			//item.setItemCount(0);
			item.setExpanded(true);
			//item.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
		} else {
			final String leaf = (String) element;
			item.setText(leaf);
			item.setItemCount(0);
			item.setExpanded(false);
		}
		item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		return;
	}

	public void refreshStructure(int refreshCorrectly) {
		int modelItemCount = elementProvider.getElementCount();
		System.out.println(" ================ refreshStructure count: " + modelItemCount + " ================ ");
		
		try {
			tree.setRedraw(false);
			System.out.println(">REDRAW FALSE");

			if (refreshCorrectly == 0) {
				tree.setItemCount(modelItemCount);
				tree.clearAll(true);
			} else {
				tree.removeAll();
				tree.setItemCount(modelItemCount);
			}
		} finally {
			tree.setRedraw(true);
			System.out.println(">REDRAW TRUE");
		}
	}
	
	/* ------ */

	public static int getIndexOf(TreeItem treeItem) {
		TreeItem parentTreeItem = treeItem.getParentItem();
		if(parentTreeItem == null) {
			return treeItem.getParent().indexOf(treeItem); 
		} else {
			return parentTreeItem.indexOf(treeItem);
		}
	}

	
	public static List<Integer> getTreeItemIndexPath(TreeItem item) {
		List<Integer> intList = new ArrayList<Integer>();
		TreeItem parentItem = item.getParentItem();
		while(parentItem != null) {
			intList.add(0, parentItem.indexOf(item));
			item = parentItem;
			parentItem = item.getParentItem();
		}
		intList.add(0, item.getParent().indexOf(item));
		return intList;
	}
	
}
