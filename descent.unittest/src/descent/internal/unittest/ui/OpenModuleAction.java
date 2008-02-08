package descent.internal.unittest.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.ISignatureConstants;

public class OpenModuleAction extends OpenEditorAction
{
	private final String fModuleName;
	
	public OpenModuleAction(TestRunnerViewPart testRunner, String moduleName)
	{
		super(testRunner);
		fModuleName = moduleName;
	}
	
	@Override
	protected String getHelpContextId()
	{
		return IJUnitHelpContextIds.OPENMODULE_ACTION;
	}

	@Override
	protected IJavaElement findElement() throws CoreException
	{
		return findModule(getLaunchedProject(), 
				TraceUtil.getModuleSignature(fModuleName), 
				new HashSet<IJavaProject>());
	}

	@Override
	protected void reveal(ITextEditor editor)
	{
		// Do nothing; the editor is already open to the proper module
	}
	
	public String getModuleName()
	{
		return fModuleName;
	}

	private static ICompilationUnit findModule(IJavaProject project,
			String moduleSignature, Set<IJavaProject> visitedProjects)
			throws JavaModelException
	{
		if (visitedProjects.contains(project))
			return null;
		
		ICompilationUnit module;
		IJavaElement javaElement = project.findBySignature(moduleSignature);
		if(javaElement != null && javaElement instanceof ICompilationUnit
			&& javaElement.exists())
		{
			// Note: existance must be tested since an IcompilationUnit may
			// be returned for non-existant modules
			module = (ICompilationUnit) javaElement;
			return module;
		}
		
		//fix for bug 87492: visit required projects explicitly to also find not exported types
		visitedProjects.add(project);
		IJavaModel javaModel= project.getJavaModel();
		String[] requiredProjectNames= project.getRequiredProjectNames();
		for (int i= 0; i < requiredProjectNames.length; i++)
		{
			IJavaProject requiredProject= javaModel.getJavaProject(requiredProjectNames[i]);
			if (requiredProject.exists())
			{
				module = findModule(requiredProject, moduleSignature, visitedProjects);
				if (module != null)
					return module;
			}
		}
		return null;
	}
}
