package descent.internal.ui.compare;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import descent.internal.ui.javaeditor.JavaEditor;

/**
 * A delegate for JavaHistoryActionImpls.
 */
public abstract class JavaHistoryAction extends Action implements IActionDelegate { 
	
	private JavaHistoryActionImpl fDelegate;	
	private JavaEditor fEditor;
	private String fTitle;
	private String fMessage;
	
	JavaHistoryAction() {
	}
	
	private JavaHistoryActionImpl getDelegate() {
		if (fDelegate == null) {
			fDelegate= createDelegate();
			if (fEditor != null && fTitle != null && fMessage != null)
				fDelegate.init(fEditor, fTitle, fMessage);
		}
		return fDelegate;
	}
	
	protected abstract JavaHistoryActionImpl createDelegate();
	
	final void init(JavaEditor editor, String text, String title, String message) {
		Assert.isNotNull(editor);
		Assert.isNotNull(title);
		Assert.isNotNull(message);
		fEditor= editor;
		fTitle= title;
		fMessage= message;
		//getDelegate().init(editor, text, title, message);
		setText(text);
		//setEnabled(getDelegate().checkEnabled());
	}
	
	/**
	 * Executes this action with the given selection.
	 */
	public final void run(ISelection selection) {
		getDelegate().run(selection);
	}

	public final void run() {
		getDelegate().runFromEditor(this);
	}

	final void update() {
		getDelegate().update(this);
	}
	
 	//---- IActionDelegate
	
	public final void selectionChanged(IAction uiProxy, ISelection selection) {
		getDelegate().selectionChanged(uiProxy, selection);
	}
	
	public final void run(IAction action) {
		getDelegate().run(action);
	}
}
