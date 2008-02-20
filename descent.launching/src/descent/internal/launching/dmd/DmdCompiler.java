package descent.internal.launching.dmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import descent.launching.AbstractVMInstall;
import descent.launching.IVMInstallType;
import descent.launching.compiler.ICompilerInterface;

public class DmdCompiler extends AbstractVMInstall {

	public DmdCompiler(IVMInstallType type, String id) {
		super(type, id);
	}
	
	@Override
	public String getJavaVersion() {
		File bin = getCompilerLocation();
		try {
			Process process = Runtime.getRuntime().exec(bin.getAbsolutePath());
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			String returnVersion = null;
			String line = br.readLine();
			if (line != null) {
				int lastSpace = line.lastIndexOf(' ');
				if (lastSpace != - 1) {
					String version = line.substring(lastSpace + 1);
					if (version.startsWith("v")) { //$NON-NLS-1$
						returnVersion = version.substring(1);
					} else {
						returnVersion = version;
					}
				}
			}
			br.close();
			isr.close();
			is.close();
		
			if (returnVersion == null) {
				return super.getJavaVersion();
			}
			
			return returnVersion;
		} catch (IOException e) {
			return super.getJavaVersion();	
		}
	}
	
	private File getCompilerLocation() {
		File file = new File(getInstallLocation(), "bin/dmd.exe");
		if (file.exists()) {
			return file;
		}
		return new File(getInstallLocation(), "bin/dmd");
	}

	public ICompilerInterface getCompilerInterface()
	{
		return DmdCompilerInterface.getInstance();
	}
}
