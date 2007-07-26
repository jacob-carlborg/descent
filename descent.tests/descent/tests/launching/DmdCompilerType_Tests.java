package descent.tests.launching;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import descent.internal.launching.dmd.DmdCompilerType;
import descent.launching.LibraryLocation;

public class DmdCompilerType_Tests extends TestCase {
	
	private final static String dmdPath = "C:\\ary\\programacion\\d\\1.007\\dmd";
	
	public void testInvalidLocation() {
		DmdCompilerType type = new DmdCompilerType();
		IStatus status = type.validateInstallLocation(new File("C:\\"));
		assertFalse(status.isOK());
	}
	
	public void testValidLocation() {
		DmdCompilerType type = new DmdCompilerType();
		IStatus status = type.validateInstallLocation(new File(dmdPath));
		assertTrue(status.isOK());
	}
	
	public void testDefaultLibraryLocation() {
		DmdCompilerType type = new DmdCompilerType();
		LibraryLocation[] defaultLibraryLocations = type.getDefaultLibraryLocations(new File(dmdPath));
		assertEquals(1, defaultLibraryLocations.length);
		assertEquals(new File(dmdPath, "lib\\phobos.lib"), defaultLibraryLocations[0].getSystemLibraryPath().toFile());
		assertEquals(new File(dmdPath, "src\\phobos"), defaultLibraryLocations[0].getSystemLibrarySourcePath().toFile());
		assertEquals(Path.EMPTY, defaultLibraryLocations[0].getPackageRootPath());
		assertNull(defaultLibraryLocations[0].getJavadocLocation());
	}

}
