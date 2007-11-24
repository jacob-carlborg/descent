package descent.internal.ui.compare;


public class JavaAddElementFromHistory extends JavaHistoryAction {
	
	public JavaAddElementFromHistory() {
	}
	
	protected JavaHistoryActionImpl createDelegate() {
		return new JavaAddElementFromHistoryImpl();
	}
}
