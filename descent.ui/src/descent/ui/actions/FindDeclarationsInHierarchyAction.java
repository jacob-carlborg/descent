package descent.ui.actions;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.ILocalVariable;
import descent.core.IMethod;
import descent.core.IType;
import descent.core.ITypeParameter;
import descent.core.JavaModelException;
import descent.core.search.IJavaSearchScope;
import descent.core.search.SearchEngine;

import descent.ui.search.ElementQuerySpecification;
import descent.ui.search.QuerySpecification;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.javaeditor.JavaEditor;
import descent.internal.ui.search.JavaSearchScopeFactory;
import descent.internal.ui.search.SearchMessages;

/**
 * Finds declarations of the selected element in its hierarchy.
 * The action is applicable to selections representing a Java element.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class FindDeclarationsInHierarchyAction extends FindDeclarationsAction {

	/**
	 * Creates a new <code>FindDeclarationsInHierarchyAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public FindDeclarationsInHierarchyAction(IWorkbenchSite site) {
		super(site);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Java editor
	 */
	public FindDeclarationsInHierarchyAction(JavaEditor editor) {
		super(editor);
	}
	
	Class[] getValidTypes() {
		return new Class[] { IField.class, IMethod.class, ILocalVariable.class, ITypeParameter.class };
	}
	
	void init() {
		setText(SearchMessages.Search_FindHierarchyDeclarationsAction_label); 
		setToolTipText(SearchMessages.Search_FindHierarchyDeclarationsAction_tooltip); 
		setImageDescriptor(JavaPluginImages.DESC_OBJS_SEARCH_DECL);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FIND_DECLARATIONS_IN_HIERARCHY_ACTION);
	}
	
	QuerySpecification createQuery(IJavaElement element) throws JavaModelException {
		JavaSearchScopeFactory factory= JavaSearchScopeFactory.getInstance();
		
		IType type= getType(element);
		if (type == null) {
			return super.createQuery(element);
		}
		
		IJavaSearchScope scope= SearchEngine.createHierarchyScope(type);
		
		String description= factory.getHierarchyScopeDescription(type);
		return new ElementQuerySpecification(element, getLimitTo(), scope, description);
	}
}
