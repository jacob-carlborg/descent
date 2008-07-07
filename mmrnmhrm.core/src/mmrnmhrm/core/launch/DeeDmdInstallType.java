package mmrnmhrm.core.launch;

import static melnorme.miscutil.Assert.assertFail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.Assert;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeNature;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.LibraryLocation;

public class DeeDmdInstallType extends AbstractInterpreterInstallType {

	private static final Path DMD_INSTALL_LIBRARY_PATH = new Path("src/phobos");

	public static class DeeLaunchingPlugin extends DeeCore {
	}
	private static String[] interpreterNames = { "dmd" };
	
	public static boolean isStandardLibraryEntry(IBuildpathEntry entry) {
		int numSegs = entry.getPath().segmentCount();
		return entry.isExternal() 
			&& entry.getPath().isAbsolute()
			&& entry.getPath().lastSegment().matches("phobos")
			&& entry.getPath().segment(numSegs-2).matches("src");
	}
	

	//@Override
	public String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected String getPluginId() {
		return DeeLaunchingPlugin.PLUGIN_ID;
	}
	
	@Override
	protected ILog getLog() {
		return DeeLaunchingPlugin.getInstance().getLog();
	}

	//@Override
	public String getName() {
		return "DMD Install";
	}

	@Override
	protected String[] getPossibleInterpreterNames() {
		return interpreterNames;
	}

	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new DeeInstall(this, id);
	}
	
	@Override
	public synchronized LibraryLocation[] getDefaultLibraryLocations(IFileHandle installLocation,
			EnvironmentVariable[] variables, IProgressMonitor monitor) {
		//return super.getDefaultLibraryLocations(installLocation, variables, monitor);
		List<LibraryLocation> locations = new ArrayList<LibraryLocation>();
		addDefaultLibraryLocations(installLocation, locations);
		return locations.toArray(new LibraryLocation[0]);
	}
	
	/** Unlike the parent class, this InstallType does not find library paths by
	 * running some kind of external executable, like Ruby or Python.
	 * It just adds some predefined path. */
	@Override
	@Deprecated
	protected ILookupRunnable createLookupRunnable(
			final IFileHandle installLocation, final List locations,
			final EnvironmentVariable[] variables) {
		assertFail();
	
		//return super.createLookupRunnable(installLocation, locations);
		@SuppressWarnings("unchecked")
		final List<LibraryLocation> locs = locations;
		

		return new ILookupRunnable() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				//String[] env = extractEnvironment();
				
//				try {
					addDefaultLibraryLocations(installLocation, locs);
//				} catch (IOException e) {
//					getLog().log(createStatus(IStatus.ERROR, "Unable to lookup library paths", e));
//				}
			}

		};
	}
	
	private void addDefaultLibraryLocations(IFileHandle installLocation, List<LibraryLocation> locs) {
		IPath path = new Path(installLocation.getCanonicalPath());
		path = path.removeLastSegments(2);
		path = path.append(DMD_INSTALL_LIBRARY_PATH);
		IEnvironment env = installLocation.getEnvironment();
		LibraryLocation loc = new LibraryLocation(EnvironmentPathUtils.getFullPath(env, path));
		locs.add(loc);
	}

	@Override
	protected IPath createPathFile(IDeployment deployment) throws IOException {
		Assert.fail("Not Used"); return null;
	}

	// Generating the InstallName not supported yet
	/*
	public String generateAutomaticInstallName(File installLocation) {
		Process process = null;
		String[] env = extractEnvironment();
		String path = installLocation.getAbsolutePath();
		String[] cmdLine = new String[]{ path };
		
		try {
			process = Runtime.getRuntime().exec(cmdLine, env);
			String line = readLine(process);
			int ix = line.indexOf(" v");
			if(ix == -1)
				return null;
			return line.substring(ix+2);
		} catch (IOException e) {
		    Status status = DeeCore.createErrorStatus(
		    		"Error running DMD to determine version", e); 
			LangCore.log(new CoreException(status));
			return null;
		}
	}

	private static String readLine(Process process) throws IOException {
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		try {
			// FIXME: when used code
			is.close(); is.close();
			isr.close(); isr.close();
			br.close(); br.close();
			return br.readLine();
		} finally {
			is.close(); is.close();
			isr.close(); isr.close();
			br.close(); br.close();
		}
	}
	*/

}
