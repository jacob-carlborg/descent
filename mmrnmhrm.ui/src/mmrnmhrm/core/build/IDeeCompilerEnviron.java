package mmrnmhrm.core.build;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public interface IDeeCompilerEnviron {
	
	void compileModules(List<IResource> dmodules) throws CoreException ;
}