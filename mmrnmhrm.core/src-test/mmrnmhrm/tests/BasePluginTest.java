package mmrnmhrm.tests;


import org.junit.After;
import org.junit.Before;

/** Base Test class that adds an exception listener to the platform log. 
 * Note: this was the only way I found to detect UI exceptions in 
 * SafeRunnable's when running as plugin test. 
 */
public abstract class BasePluginTest {
	
	protected ErrorLogListener logErrorListener;
	
	
	@Before
	public void setUpExceptionListener() throws Exception {
		logErrorListener = ErrorLogListener.createAndInstall();
	}
	
	@After
	public void checkLogErrorListener() throws Throwable {
		logErrorListener.checkErrorsAndUninstall();
	}
	
}