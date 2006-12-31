package mmrnmhrm;

import mmrnmhrm.text.DeeCodeScanner;
import mmrnmhrm.ui.preferences.ColorManager;

public class DeeUICore {
	private static DeeCodeScanner defaultDeeCodeScanner;
	
	static {
		defaultDeeCodeScanner = new DeeCodeScanner(ColorManager.getInstance());
	}
	
	public static DeeCodeScanner getDefaultDeeCodeScanner() {
		return defaultDeeCodeScanner;
	}
}
