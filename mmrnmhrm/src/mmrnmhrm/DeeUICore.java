package mmrnmhrm;

import mmrnmhrm.text.DeeCodeScanner_Native;

import org.eclipse.jface.text.rules.ITokenScanner;

public class DeeUICore {
	private static ITokenScanner defaultDeeCodeScanner;
	
	static {
		//defaultDeeCodeScanner = new DeeCodeScanner(ColorManager.getInstance());
		defaultDeeCodeScanner = new DeeCodeScanner_Native();
	}
	
	public static ITokenScanner getDefaultDeeCodeScanner() {
		return defaultDeeCodeScanner;
	}
}
