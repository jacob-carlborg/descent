package scratchpad.actions;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import testproduct.views.SampleView;

public class TestViews {

	public static void test() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		try {
			page.showView(SampleView.ID, "123", IWorkbenchPage.VIEW_ACTIVATE);
			page.showView(SampleView.ID, "321", IWorkbenchPage.VIEW_ACTIVATE);
			page.showView(SampleView.ID, "321", IWorkbenchPage.VIEW_ACTIVATE);
			page.showView(SampleView.ID);
			page.showView(SampleView.ID);
			page.showView(SampleView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
	
}
