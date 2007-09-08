package mmrnmhrm.tests;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.ui.IWorkbenchPage;
import org.junit.AfterClass;
import org.junit.BeforeClass;


public class BaseUITest extends BasePluginExceptionWatcherTest {
	
	static {
		IWorkbenchPage page = DeePlugin.getActivePage();
		page.closeAllEditors(false);
	}

	@BeforeClass
	public static void staticTestInit() throws Exception {
	}
	
	@AfterClass
	public static void staticTestEnd() throws Exception {
	}
	
}