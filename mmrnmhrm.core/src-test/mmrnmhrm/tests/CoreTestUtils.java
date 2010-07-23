package mmrnmhrm.tests;

import static melnorme.miscutil.Assert.assertFail;
import static melnorme.miscutil.Assert.assertNotNull;
import static melnorme.miscutil.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import melnorme.miscutil.MiscUtil;
import mmrnmhrm.core.CoreUtils;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.launch.DeeDmdInstallType;
import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.core.model.ModelUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.launching.ScriptRuntime;



public class CoreTestUtils {
	
	public static IScriptProject createAndOpenProject(String name) throws CoreException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		
		IProject project;
		project = workspaceRoot.getProject(name);
		if(project.exists()) {
			project.delete(true, null);
		}
		project.create(null);
		project.open(null);
		setupDeeProject(project);
		return DLTKCore.create(project);
	}
	
	public static void setupDeeProject(IProject project) throws CoreException {
		MiscUtil.loadClass(BaseDeePluginTest.class); // TODO BM, improve these dependencies
		
		assertTrue(project.exists());
		ModelUtil.addNature(project, DeeNature.NATURE_ID);
		
		IBuildpathEntry entry = DLTKCore.newContainerEntry(ScriptRuntime.newDefaultInterpreterContainerPath()
				.append(DeeDmdInstallType.INSTALLTYPE_ID).append(BaseDeePluginTest.DEFAULT_DMD2_INSTALL));
		
		
		IScriptProject dltkProj = DLTKCore.create(project);
		dltkProj.setRawBuildpath(new IBuildpathEntry[] {entry}, null);
		
		assertNotNull(ScriptRuntime.getInterpreterInstall(dltkProj));
	}
	
	public static void createSrcFolderInProject(String bundleDir, IContainer destFolder) 
			throws CoreException, URISyntaxException, IOException, ModelException {
		copyDeeCoreDirToWorkspace(bundleDir, destFolder);
		createSrcFolder(destFolder);
	}

	public static void createSrcFolder(IContainer destFolder) throws CoreException, ModelException {
		IScriptProject dltkProj = DLTKCore.create(destFolder.getProject());
		ModelUtil.createAddSourceFolder(dltkProj, destFolder);
		//dltkProj.save(null, false);
	}
	
	protected static IResourceVisitor vcsFilter = new IResourceVisitor() {
		@Override
		public boolean visit(IResource resource) throws CoreException {
			return !(resource.getType() == IResource.FOLDER && resource.getName().equals(".svn"));
		}
	};
	
	
	static void copyDeeCoreDirToWorkspace(final String srcPath, final IContainer destFolder) 
			throws CoreException, URISyntaxException, IOException {
		String pluginId = DeeCore.PLUGIN_ID;
		String basePath = ITestDataConstants.TESTDATA;
		copyBundleDirToWorkspace(pluginId, destFolder, new Path(basePath + srcPath));
	}
	
//	static void copyDToolDirToWorkspace(final String srcPath, final IContainer destFolder) 
//			throws CoreException, URISyntaxException, IOException {
//		String pluginId = DeeCore.PLUGIN_ID;
//		String basePath = ITestDataConstants.TESTDATA;
//		copyBundleDirToWorkspace(pluginId, destFolder, new Path(basePath + srcPath));
//	}
	
	
	public static void copyBundleDirToWorkspace(String bundleId, final IContainer destFolder, IPath bundlesrcpath) 
			throws CoreException, IOException {
		URL sourceURL = FileLocator.find(Platform.getBundle(bundleId), bundlesrcpath, null);
		assertNotNull(sourceURL);
		
		URI uri = getURI_Assured(FileLocator.toFileURL(sourceURL));
		CoreUtils.copyURLResourceToWorkspace(uri, destFolder, vcsFilter);
	}
	
	/** Return a URI for given url, which must comply to RFC 2396. */
	private static URI getURI_Assured(URL url) {
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			throw assertFail();
		}
	}
	
	public static void copyURLResourceToWorkspace(URI uri, final IContainer destFolder) throws CoreException {
		CoreUtils.copyURLResourceToWorkspace(uri, destFolder, vcsFilter);
	}
	
}
