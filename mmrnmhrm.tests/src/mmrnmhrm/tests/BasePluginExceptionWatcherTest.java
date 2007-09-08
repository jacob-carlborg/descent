package mmrnmhrm.tests;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;

import static melnorme.miscutil.Assert.assertTrue;

public abstract class BasePluginExceptionWatcherTest extends BasePluginTest {

	protected boolean exceptionThrown;
	protected Throwable exception;
	protected ILogListener loglistener;

	
	/** Adds an exception listener to the platform log. 
	 * FIXME: this was the only way I found to detected UI exceptions in 
	 * SafeRunnable's when running as plugin test. */
	@Before
	public void setUpExceptionListener() throws Exception {
		exceptionThrown = false;
		exception = null;
		loglistener = new ILogListener() {
			public void logging(IStatus status, String plugin) {
				System.err.println(status);
				//if(plugin.equals(DeePlugin.PLUGIN_ID) || plugin.equals(DeeCore.PLUGIN_ID))
				if(status.getSeverity() == IStatus.ERROR) {
					exceptionThrown = true;
					exception = status.getException();
				}
			}
		} ;
		Platform.addLogListener(loglistener);
	}
	
	@After
	public void assertNoUIExceptionsThrown() throws Throwable {
		Platform.removeLogListener(loglistener);
		if(exceptionThrown == true) {
			if(exception == null) {
				//UITestUtils.runEventLoop(DeePlugin.getActiveWorkbenchShell());
			}
			throw exception;
		}
		assertTrue(exceptionThrown == false, "Assertion failed.");
	}
}