package descent.ui.actions;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

import descent.core.IField;
import descent.core.ILocalVariable;
import descent.core.search.IJavaSearchConstants;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.javaeditor.JavaEditor;
import descent.internal.ui.search.SearchMessages;

/**
 * Finds field read accesses of the selected element in the workspace.
 * The action is applicable to selections representing a Java interface field.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class FindReadReferencesAction extends FindReferencesAction {

	/**
	 * Creates a new <code>FindReadReferencesAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public FindReadReferencesAction(IWorkbenchSite site) {
		super(site);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Java editor
	 */
	public FindReadReferencesAction(JavaEditor editor) {
		super(editor);
	}
	
	Class[] getValidTypes() {
		return new Class[] { IField.class, ILocalVariable.class };
	}

	void init() {
		setText(SearchMessages.Search_FindReadReferencesAction_label); 
		setToolTipText(SearchMessages.Search_FindReadReferencesAction_tooltip); 
		setImageDescriptor(JavaPluginImages.DESC_OBJS_SEARCH_REF);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FIND_READ_REFERENCES_IN_WORKSPACE_ACTION);
	}
	
	int getLimitTo() {
		return IJavaSearchConstants.READ_ACCESSES;
	}	

	String getOperationUnavailableMessage() {
		return SearchMessages.JavaElementAction_operationUnavailable_field; 
	}
}
