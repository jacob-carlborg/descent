package mmrnmhrm.tests;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;

import util.ExceptionAdapter;

public class BaseUITestClass extends BaseTestClass {

	protected boolean exceptionThrown;
	protected Throwable exception;

	@Before
	public void setUpExceptionListener() throws Exception {
		exceptionThrown = false;
		exception = null;
		Platform.addLogListener(new ILogListener() {
			public void logging(IStatus status, String plugin) {
				System.err.println(status);
				if(status.getSeverity() == IStatus.ERROR) {
					exceptionThrown = true;
					exception = status.getException();
				}
			}
		
		});
	}
	

	protected void flushUI() {
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {
				try {
					if(false)
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					throw ExceptionAdapter.unchecked(e);
				}
			}});
		while(Display.getCurrent().readAndDispatch() == true)
			;
	}
	


}