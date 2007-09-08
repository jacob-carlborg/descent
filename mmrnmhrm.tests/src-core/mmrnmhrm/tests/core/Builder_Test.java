package mmrnmhrm.tests.core;

import java.io.IOException;
import java.net.URISyntaxException;

import mmrnmhrm.tests.BasePluginExceptionWatcherTest;
import mmrnmhrm.tests.CoreTestUtils;
import mmrnmhrm.tests.ITestDataConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;
import org.junit.Test;

public class Builder_Test extends BasePluginExceptionWatcherTest {

	@Test
	public void test() throws CoreException, URISyntaxException, IOException {
		IScriptProject deeProj = CoreTestUtils.createAndOpenProject("__BuilderProject");
		IProject project = deeProj.getProject();
		
		doProjectBuild(deeProj);
		
		CoreTestUtils.createFolderInProject(project, ITestDataConstants.BUILD_SRC, "buildSrc", true);
		doProjectBuild(deeProj);
		//UITestUtils.runEventLoop();
		
		CoreTestUtils.createFolderInProject(project, ITestDataConstants.SAMPLE_SRC1, "src1", true);
		doProjectBuild(deeProj);

		CoreTestUtils.createFolderInProject(project, ITestDataConstants.SAMPLE_SRC3, "src3", true);
		doProjectBuild(deeProj);

		CoreTestUtils.createFolderInProject(project, ITestDataConstants.SAMPLE_SRC1, "src1-copy", true);
		doProjectBuild(deeProj);
	}

	private void doProjectBuild(IScriptProject deeProj) throws CoreException {
		IProject project = deeProj.getProject();
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
}
