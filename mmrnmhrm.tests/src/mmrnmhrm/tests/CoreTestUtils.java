package mmrnmhrm.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import melnorme.miscutil.StringUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.EModelStatus;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


public class CoreTestUtils {

	
	public static CompilationUnit testParseCUnit(final String cunitsource) throws CoreException {

		// create an orphaned Compilation Unit
		CompilationUnit cunit = new CompilationUnit(null) {
			@Override
			public String getSource() {
				return cunitsource;
			}
			
			@Override
			protected void clearErrorMarkers() {
				// do nothing
			}
		};
		//cunit.updateElement();
		//cunit.getDocument().set(cunitsource);
		cunit.reconcile();
		BaseTest.assertTrue(cunit.parseStatus == EModelStatus.OK,
				"Module failed to parse Correctly" + 
				"\n " + StringUtil.collToString(cunit.problems, "\n "));
		//System.out.print(ASTPrinter.toStringAST(cunit.getModule()));
		return cunit;
	}

	static IFolder createWorkspaceFolderFromBundle(String srcpath, IContainer parent, String destname)
			throws CoreException, URISyntaxException, IOException {
		Bundle bundle = Platform.getBundle(DeeTestsPlugin.PLUGIN_ID);
		IPath bundlesrcpath = new Path(DeeTestsPlugin.TESTDATA + srcpath);
		URL sampleURL = FileLocator.find(bundle, bundlesrcpath, null);
		IFolder folder = parent.getFolder(new Path("__"+destname+"link"));
		folder.createLink(FileLocator.toFileURL(sampleURL).toURI(), IResource.NONE, null);
		
		IPath projpath = parent.getFullPath(); 
		folder.copy(projpath.append(destname), false, null);
		folder.delete(false, null);
		return parent.getFolder(new Path(destname));
	}
}
