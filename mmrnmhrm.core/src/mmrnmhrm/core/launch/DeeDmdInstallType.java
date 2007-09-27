package mmrnmhrm.core.launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import melnorme.miscutil.Assert;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.LangCore;
import mmrnmhrm.core.model.DeeNature;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.LibraryLocation;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class DeeDmdInstallType extends AbstractInterpreterInstallType {

	private static String[] interpreterNames = { "dmd" };
	
	public static class DeeLaunchingPlugin extends DeeCore {
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
		return "DMD + Phobos";
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
	public synchronized LibraryLocation[] getDefaultLibraryLocations(File installLocation) {
		return super.getDefaultLibraryLocations(installLocation);
	}
	
	@SuppressWarnings("unchecked")
	protected org.eclipse.jface.operation.IRunnableWithProgress createLookupRunnable(
			final File installLocation, final List locations) {
		//return super.createLookupRunnable(installLocation, locations);
		final List<LibraryLocation> locs = locations;
		
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				//String[] env = extractEnvironment();
				
				IPath path;
				try {
					path = new Path(installLocation.getCanonicalPath());
					path = path.removeLastSegments(2);
					path = path.append("src").append("phobos");
					LibraryLocation loc = new LibraryLocation(path);
					locs.add(loc);
				} catch (IOException e) {
					getLog().log(createStatus(IStatus.ERROR,
									"Unable to lookup library paths", e));
				}
			}
		};
	}

	@Override
	protected File createPathFile() throws IOException {
		Assert.fail("Not Used"); return null;
	}

	@Override
	public IStatus validateInstallLocation(File installLocation) {
		return super.validateInstallLocation(installLocation);
	}
	
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

}
