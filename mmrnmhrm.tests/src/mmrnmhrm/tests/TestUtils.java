package mmrnmhrm.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import melnorme.miscutil.FileUtil;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


public class TestUtils {

	public static <T> String readStringFromResource(String filename, Class<T> clss) throws IOException {
		InputStream is = clss.getResourceAsStream(filename);
		InputStreamReader isr = new InputStreamReader(is);
		return FileUtil.readStringFromReader(isr);
	}

	public static String readTestDataFile(String pathstr) throws IOException {
		Bundle bundle = Platform.getBundle(DeePlugin.PLUGIN_ID);
		InputStream is = FileLocator.openStream(bundle, new Path("testdata/"+pathstr), false);
		InputStreamReader isr = new InputStreamReader(is);
		String src = FileUtil.readStringFromReader(isr);
		return src;
	}

	public static void createRecursive(IFolder container, boolean force) throws CoreException {
		if(!container.getParent().exists()) {
			if(container.getParent().getType() == IResource.FOLDER)
				createRecursive((IFolder)container.getParent(), force);
		}
		container.create(force, true, null);
	}
	
	public static void createRecursive(IFile file, InputStream is, boolean force) throws CoreException {
		if(!file.getParent().exists()) {
			if(file.getParent().getType() == IResource.FOLDER)
				createRecursive((IFolder)file.getParent(), force);
		}
		file.create(is, force, null);
	}

}
