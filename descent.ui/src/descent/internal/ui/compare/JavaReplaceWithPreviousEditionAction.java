package descent.internal.ui.compare;

/**
 * Provides "Replace from local history" for Java elements.
 */
public class JavaReplaceWithPreviousEditionAction extends JavaHistoryAction {
					
	public JavaReplaceWithPreviousEditionAction() {
	}	
	
	protected JavaHistoryActionImpl createDelegate() {
		return new JavaReplaceWithEditionActionImpl(true);
	}
}

