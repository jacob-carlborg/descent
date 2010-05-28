package mmrnmhrm.tests;

import mmrnmhrm.ui.DeePlugin;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;


public class BaseDeePluginUITest extends BaseDeePluginTest {

	@BeforeClass
	public static void staticTestInit() throws Exception {
		IWorkbenchPage page = DeePlugin.getActivePage();
		page.closeAllEditors(false);
	}
	
	@AfterClass
	public static void staticTestEnd() throws Exception {
	}
	
	@Override
	public void checkLogErrorListener() throws Throwable {
		SWTTestUtils.runEventQueueUntilEmpty(PlatformUI.getWorkbench().getDisplay());
		super.checkLogErrorListener();
	}
	
}