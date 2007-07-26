package descent.internal.launching.gdc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import descent.launching.AbstractVMInstall;
import descent.launching.IVMInstallType;

public class GdcCompiler extends AbstractVMInstall {

	public GdcCompiler(IVMInstallType type, String id) {
		super(type, id);
	}
	
	@Override
	public String getJavaVersion() {
		File bin = getCompilerLocation();
		try {
			Process process = Runtime.getRuntime().exec(bin.getAbsolutePath() + " -v");
			InputStream is = process.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = br.readLine();
			String newLine = null;
			while((newLine = br.readLine()) != null) {
				line = newLine;
			}
			
			String version = null;
			if (line != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, " ");  //$NON-NLS-1$
				while(tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (token.equals("dmd") && tokenizer.hasMoreTokens()) { //$NON-NLS-1$
						version = tokenizer.nextToken();
						if (version.endsWith(")")) { //$NON-NLS-1$
							version = version.substring(0, version.length() - 1);
						}
					}
				}
			}
			br.close();
			isr.close();
			is.close();
			
			if (version == null) {
				return super.getJavaVersion();	
			}
			
			return version;
		} catch (IOException e) {
			return super.getJavaVersion();	
		}
	}
	
	private File getCompilerLocation() {
		File file = new File(getInstallLocation(), "bin/gdc.exe");
		if (file.exists()) {
			return file;
		}
		return new File(getInstallLocation(), "bin/gdc");
	}

}
