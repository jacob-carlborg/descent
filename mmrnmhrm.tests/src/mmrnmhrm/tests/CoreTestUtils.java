package mmrnmhrm.tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.ModelUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.osgi.framework.Bundle;

import static melnorme.miscutil.Assert.assertNotNull;



public class CoreTestUtils {

	
	/** FIXME: copy each file ourselfs, to prevent copying .svn files. */
	static IFolder createWorkspaceFolderFromBundle(String srcpath, IContainer parent, String destname)
			throws CoreException, URISyntaxException, IOException {
		Bundle bundle = Platform.getBundle(DeeTestsPlugin.PLUGIN_ID);
		IPath bundlesrcpath = new Path(DeeTestsPlugin.TESTDATA + srcpath);
		URL sourceURL = FileLocator.find(bundle, bundlesrcpath, null);
		assertNotNull(sourceURL);
		IFolder linkFolder = parent.getFolder(new Path("__"+destname+"link"));
		linkFolder.createLink(FileLocator.toFileURL(sourceURL).toURI(), IResource.NONE, null);
		
		IPath projpath = parent.getFullPath(); 
		linkFolder.copy(projpath.append(destname), false, null);
		linkFolder.delete(false, null);
		return parent.getFolder(new Path(destname));
	}

	public static DeeProject createAndOpenDeeProject(String name)
			throws CoreException {
		return new DeeProject(createAndOpenProject(name));
	}
	
	public static IScriptProject createAndOpenProject(String name)
		throws CoreException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		
		IProject project;
		project = workspaceRoot.getProject(name);
		if(project.exists())
			project.delete(true, null);
		project.create(null);
		project.open(null);
		ModelUtil.createDeeProject(project);
		return DLTKCore.create(project);
	}


	public static IFolder createFolderInProject(IProject project,
			String bundleDir, String destDir, boolean addSrcFolder)
			throws CoreException, URISyntaxException, IOException {
		IFolder folder;
		IScriptProject dltkProj = DLTKCore.create(project);
		folder = createWorkspaceFolderFromBundle(bundleDir,
				project, destDir);
		if(addSrcFolder) {
			ModelUtil.createAddSourceFolder(dltkProj, folder);
			dltkProj.save(null, false);
		}
		return folder;
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
