package mmrnmhrm.tests;

import static melnorme.miscutil.Assert.assertEquals;
import static melnorme.miscutil.Assert.assertFail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import dtool.DToolBundle;
import dtool.tests.DToolTestResources;

/**
 * Initialize DToolResources to run under a Eclipse/OSGI bundle environment
 */
public class DToolResourcesPluginAdapter {

	public static void initialize() {
		
		Location instanceLocation = Platform.getInstanceLocation();
//		System.setProperty(DToolTestResources.D_TOOL_TEST_RESOURCES_WORKING_DIR, "");
		URI uri;
		try {
			uri = instanceLocation.getURL().toURI();
		} catch (URISyntaxException e) {
			throw assertFail();
		}
		
		String workingDirPath = new File(uri).getAbsolutePath();
		System.setProperty(DToolTestResources.D_TOOL_TEST_RESOURCES_WORKING_DIR, workingDirPath);
		
		DToolTestResources.getInstance(); // initialize DToolTestResources;
		
		assertEquals(workingDirPath, DToolTestResources.getInstance().getWorkingDir().getAbsolutePath());
		//assertEquals(workingDirPath, DToolTestResources.getInstance().getResourcesDir());
		
	}
	
}
