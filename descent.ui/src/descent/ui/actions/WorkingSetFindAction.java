package descent.ui.actions;

import org.eclipse.jface.util.Assert;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

import descent.core.IJavaElement;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.javaeditor.JavaEditor;

/**
 * Wraps a <code>JavaElementSearchActions</code> to find its results
 * in the specified working set.
 * <p>
 * The action is applicable to selections and Search view entries
 * representing a Java element.
 * 
 * <p>
 * Note: This class is for internal use only. Clients should not use this class.
 * </p>
 * 
 * @since 2.0
 */
public class WorkingSetFindAction extends FindAction {

	private FindAction fAction;

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 */
	public WorkingSetFindAction(IWorkbenchSite site, FindAction action, String workingSetName) {
		super(site);
		init(action, workingSetName);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 */
	public WorkingSetFindAction(JavaEditor editor, FindAction action, String workingSetName) {
		super(editor);
		init(action, workingSetName);
	}

	Class[] getValidTypes() {
		return null; // ignore, we override canOperateOn
	}
	
	void init() {
		// ignore: do our own init in 'init(FindAction, String)'
	}
	
	private void init(FindAction action, String workingSetName) {
		Assert.isNotNull(action);
		fAction= action;
		setText(workingSetName);
		setImageDescriptor(action.getImageDescriptor());
		setToolTipText(action.getToolTipText());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.WORKING_SET_FIND_ACTION);
	}
	
	public void run(IJavaElement element) {
		fAction.run(element);
	}

	boolean canOperateOn(IJavaElement element) {
		return fAction.canOperateOn(element);
	}

	int getLimitTo() {
		return -1;
	}

	String getOperationUnavailableMessage() {
		return fAction.getOperationUnavailableMessage();
	}

}
