package scratchpad.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import testdependencies.Foo_PlatformRestricted;

public class BundleReferenceTest {

	public static void testBundleReferences(IWorkbenchWindow window) {
		String calcString;
		if(Foo_PlatformRestricted.hasFunc()) {
			calcString = Foo_PlatformRestricted.calcString()
				+ "-> " + Foo_PlatformRestricted.TEST_CONSTANT + " " + Foo_PlatformRestricted.TEST_CONSTANT2;
		} else {
			calcString = "No dep" 
				+ "-> " + Foo_PlatformRestricted.TEST_CONSTANT + " " + Foo_PlatformRestricted.TEST_CONSTANT2;
		}
		MessageDialog.openInformation(window.getShell(), "ScratchPad",
				calcString
				);
	}
	
}
