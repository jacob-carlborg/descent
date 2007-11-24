package descent.internal.ui.compare;

/**
 * Provides "Replace from local history" for Java elements.
 */
public class JavaReplaceWithEditionAction extends JavaHistoryAction {
				
	public JavaReplaceWithEditionAction() {
	}
	
	protected JavaHistoryActionImpl createDelegate() {
		return new JavaReplaceWithEditionActionImpl(false);
	}
}
