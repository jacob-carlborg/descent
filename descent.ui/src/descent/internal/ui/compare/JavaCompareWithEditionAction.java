package descent.internal.ui.compare;

/**
 * Provides "Replace from local history" for Java elements.
 */
public class JavaCompareWithEditionAction extends JavaHistoryAction {
	
	public JavaCompareWithEditionAction() {
	}
	
	protected JavaHistoryActionImpl createDelegate() {
		return new JavaCompareWithEditionActionImpl();
	}
}

