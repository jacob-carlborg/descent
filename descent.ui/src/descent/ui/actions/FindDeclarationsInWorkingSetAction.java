package descent.ui.actions;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

import descent.core.IJavaElement;
import descent.core.JavaModelException;
import descent.core.search.IJavaSearchScope;

import descent.ui.search.ElementQuerySpecification;
import descent.ui.search.QuerySpecification;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.javaeditor.JavaEditor;
import descent.internal.ui.search.JavaSearchScopeFactory;
import descent.internal.ui.search.SearchMessages;
import descent.internal.ui.search.SearchUtil;

/**
 * Finds declarations of the selected element in working sets.
 * The action is applicable to selections representing a Java element.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class FindDeclarationsInWorkingSetAction extends FindDeclarationsAction {

	private IWorkingSet[] fWorkingSet;

	/**
	 * Creates a new <code>FindDeclarationsInWorkingSetAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>. The user will be 
	 * prompted to select the working sets.
	 * 
	 * @param site the site providing context information for this action
	 */
	public FindDeclarationsInWorkingSetAction(IWorkbenchSite site) {
		this(site, null);
	}

	/**
	 * Creates a new <code>FindDeclarationsInWorkingSetAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site			the site providing context information for this action
	 * @param workingSets	the working sets to be used in the search
	 */
	public FindDeclarationsInWorkingSetAction(IWorkbenchSite site, IWorkingSet[] workingSets) {
		super(site);
		fWorkingSet= workingSets;
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Java editor
	 */
	public FindDeclarationsInWorkingSetAction(JavaEditor editor) {
		this(editor, null);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * 
	 * @param editor the Java editor
	 * @param workingSets the working sets to be used in the search
	 */
	public FindDeclarationsInWorkingSetAction(JavaEditor editor, IWorkingSet[] workingSets) {
		super(editor);
		fWorkingSet= workingSets;
	}

	void init() {
		setText(SearchMessages.Search_FindDeclarationsInWorkingSetAction_label); 
		setToolTipText(SearchMessages.Search_FindDeclarationsInWorkingSetAction_tooltip); 
		setImageDescriptor(JavaPluginImages.DESC_OBJS_SEARCH_DECL);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FIND_DECLARATIONS_IN_WORKING_SET_ACTION);
	}

	QuerySpecification createQuery(IJavaElement element) throws JavaModelException {
		JavaSearchScopeFactory factory= JavaSearchScopeFactory.getInstance();
		
		IWorkingSet[] workingSets= fWorkingSet;
		if (fWorkingSet == null) {
			workingSets= factory.queryWorkingSets();
			if (workingSets == null)
				return null;
		}
		SearchUtil.updateLRUWorkingSets(workingSets);
		IJavaSearchScope scope= factory.createJavaSearchScope(workingSets, true);
		String description= factory.getWorkingSetScopeDescription(workingSets, true);
		return new ElementQuerySpecification(element, getLimitTo(), scope, description);
	}

}
