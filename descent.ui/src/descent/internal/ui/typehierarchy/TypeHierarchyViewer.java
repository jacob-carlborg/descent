package descent.internal.ui.typehierarchy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.ViewerFilter;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

import descent.core.IMember;
import descent.core.IType;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.util.JavaUIHelp;
import descent.internal.ui.viewsupport.DecoratingJavaLabelProvider;
import descent.internal.ui.viewsupport.ProblemTreeViewer;

import descent.ui.JavaElementLabels;
import descent.ui.actions.OpenAction;
 
public abstract class TypeHierarchyViewer extends ProblemTreeViewer {
	
	private OpenAction fOpen;
	private HierarchyLabelProvider fLabelProvider;
			
	public TypeHierarchyViewer(Composite parent, IContentProvider contentProvider, TypeHierarchyLifeCycle lifeCycle,  IWorkbenchPart part) {
		super(new Tree(parent, SWT.SINGLE));

		fLabelProvider= new HierarchyLabelProvider(lifeCycle);
	
		setLabelProvider(new DecoratingJavaLabelProvider(fLabelProvider, true));
		setUseHashlookup(true);
			
		setContentProvider(contentProvider);
		setSorter(new HierarchyViewerSorter(lifeCycle));
		
		fOpen= new OpenAction(part.getSite());
		addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				fOpen.run();
			}
		});
		
		JavaUIHelp.setHelp(this, IJavaHelpContextIds.TYPE_HIERARCHY_VIEW);
	}
	
	public void setQualifiedTypeName(boolean on) {
		if (on) {
			fLabelProvider.setTextFlags(fLabelProvider.getTextFlags() | JavaElementLabels.T_POST_QUALIFIED);
		} else {
			fLabelProvider.setTextFlags(fLabelProvider.getTextFlags() & ~JavaElementLabels.T_POST_QUALIFIED);
		}
		refresh();
	}
	
	/**
	 * Attaches a contextmenu listener to the tree
	 */
	public void initContextMenu(IMenuListener menuListener, String popupId, IWorkbenchPartSite viewSite) {
		MenuManager menuMgr= new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(menuListener);
		Menu menu= menuMgr.createContextMenu(getTree());
		getTree().setMenu(menu);
		viewSite.registerContextMenu(popupId, menuMgr, this);
	}

	/**
	 * Fills up the context menu with items for the hierarchy viewer
	 * Should be called by the creator of the context menu
	 */	
	public void contributeToContextMenu(IMenuManager menu) {
	}

	/**
	 * Set the member filter
	 */
	public void setMemberFilter(IMember[] memberFilter) {
		TypeHierarchyContentProvider contentProvider= getHierarchyContentProvider();
		if (contentProvider != null) {
			contentProvider.setMemberFilter(memberFilter);
		}
	}

	/**
	 * Returns if method filtering is enabled.
	 */	
	public boolean isMethodFiltering() {
		TypeHierarchyContentProvider contentProvider= getHierarchyContentProvider();
		if (contentProvider != null) {
			return contentProvider.getMemberFilter() != null;
		}
		return false;
	}

	public void setWorkingSetFilter(ViewerFilter filter) {
		fLabelProvider.setFilter(filter);
		TypeHierarchyContentProvider contentProvider= getHierarchyContentProvider();
		if (contentProvider != null) {
			contentProvider.setWorkingSetFilter(filter);
		}		
	}
	
	/**
	 * Returns true if the hierarchy contains elements. Returns one of them
	 * With member filtering it is possible that no elements are visible
	 */ 
	public Object containsElements() {
		TypeHierarchyContentProvider contentProvider= getHierarchyContentProvider();
		if (contentProvider != null) {
			Object[] elements= contentProvider.getElements(null);
			if (elements.length > 0) {
				return elements[0];
			}
		}
		return null;
	}	
	
	/**
	 * Returns true if the hierarchy contains elements. Returns one of them
	 * With member filtering it is possible that no elements are visible
	 */ 
	public IType getTreeRootType() {
		TypeHierarchyContentProvider contentProvider= getHierarchyContentProvider();
		if (contentProvider != null) {		
			Object[] elements=  contentProvider.getElements(null);
			if (elements.length > 0 && elements[0] instanceof IType) {
				return (IType) elements[0];
			}
		}
		return null;
	}	
			
	/**
	 * Returns true if the hierarchy contains element the element.
	 */ 
	public boolean isElementShown(Object element) {
		return findItem(element) != null;
	}
	
	/**
	 * Updates the content of this viewer: refresh and expanding the tree in the way wanted.
	 */
	public abstract void updateContent(boolean doExpand);	
	
	/**
	 * Returns the title for the current view
	 */
	public abstract String getTitle();
	
	/*
	 * @see StructuredViewer#setContentProvider
	 * Content provider must be of type TypeHierarchyContentProvider
	 */
	public void setContentProvider(IContentProvider cp) {
		Assert.isTrue(cp instanceof TypeHierarchyContentProvider);
		super.setContentProvider(cp);
	}

	protected TypeHierarchyContentProvider getHierarchyContentProvider() {
		return (TypeHierarchyContentProvider)getContentProvider();
	}
	
}
