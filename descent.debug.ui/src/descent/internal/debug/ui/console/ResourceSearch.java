package descent.internal.debug.ui.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.core.CancelableNameEnvironment;
import descent.internal.core.JavaProject;

/*package*/ class ResourceSearch
{
	
	private INameEnvironment env;
	
	public IFile search(String filename)
	{
		if (this.env == null) {
			IJavaProject activeProject = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getActiveProject();
			try {
				this.env = new CancelableNameEnvironment((JavaProject) activeProject, null, null);
			} catch (CoreException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		char[][] compoundName = getCompoundName(filename);
		ICompilationUnit unit = env.findCompilationUnit(compoundName);
		if (unit != null)
			return (IFile) unit.getResource();
		return null;
	}
	
	private static char[][] getCompoundName(String filename) {
		if (filename.endsWith(".d")) {
			filename = filename.substring(0, filename.length() - 2);
		} else if (filename.endsWith(".di")) {
			filename = filename.substring(0, filename.length() - 3);
		} 
		
		char separator;
		if (filename.indexOf('/') != -1) {
			separator = '/';
		} else {
			separator = '\\';
		}
		
		char[] chr = filename.toCharArray();
		return CharOperation.splitOn(separator, chr);
	}
}
