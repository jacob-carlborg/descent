package mmrnmhrm.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import melnorme.miscutil.FileUtil;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


public class TestUtils {

//	public static <T> String readStringFromResource(String filename, Class<T> clss) throws IOException {
//		InputStream is = clss.getResourceAsStream(filename);
//		InputStreamReader isr = new InputStreamReader(is);
//		return FileUtil.readStringFromReader(isr);
//	}

	public static String readTestDataFile(String pathstr) throws IOException {
		Bundle bundle = Platform.getBundle(DeeCore.PLUGIN_ID);
		InputStream is = FileLocator.openStream(bundle, new Path("testdata/"+pathstr), false);
		InputStreamReader isr = new InputStreamReader(is);
		String src = FileUtil.readStringFromReader(isr);
		return src;
	}

}
