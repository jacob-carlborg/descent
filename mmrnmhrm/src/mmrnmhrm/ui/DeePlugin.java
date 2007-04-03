package mmrnmhrm.ui;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.ui.text.DeeCodeScanner;

public class DeePlugin extends DeeUI {
	
	public static DeeCodeScanner getDefaultDeeCodeScanner() {
		return DeeCore.getDefaultDeeCodeScanner();
	}
}
