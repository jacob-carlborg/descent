package descent.internal.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

public class DLaunchableTester extends PropertyTester {
	
	private final static Set<String> knownFilesThatAreNotExecutables;
	static {
		knownFilesThatAreNotExecutables = new HashSet<String>();
		for(String s : new String[] { "d", "di", "java", "py", "txt", "class", "properties", "jpg", "jpeg", "gif", "bmp", "png" }) {
			knownFilesThatAreNotExecutables.add(s);
		}
	}
	
	/**
	 * name for the PROPERTY_PROJECT_NATURE property
	 */
	private static final String PROPERTY_PROJECT_NATURE = "hasProjectNature"; //$NON-NLS-1$
	
	/**
	 * name for the IS_EXECUTABLE property
	 */
	private static final String PROPERTY_IS_EXECUTABLE = "isExecutable"; //$NON-NLS-1$

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		IResource resource = null;
		if (receiver instanceof IAdaptable) {
			resource = (IResource) ((IAdaptable)receiver).getAdapter(IResource.class);
			if(resource != null) {
				if(!resource.exists()) {
					return false;
				}
			}
		}
		if(PROPERTY_PROJECT_NATURE.equals(property)) {
			return hasProjectNature(resource, (String)args[0]);
		}
		if (PROPERTY_IS_EXECUTABLE.equals(property)) {
			return isExecutable(resource);
		}
		return false;
	}
	
	private boolean isExecutable(IResource resource) {
		if (!(resource instanceof IFile))
			return false;
		
		String extension = resource.getFileExtension();
		if (knownFilesThatAreNotExecutables.contains(extension)) {
			return false;
		}
		return true;
	}

	/**
     * determines if the project selected has the specified nature
     * @param resource the resource to get the project for
     * @param ntype the specified nature type
     * @return true if the specified nature matches the project, false otherwise
     */
    private boolean hasProjectNature(IResource resource, String ntype) {
    	try {
    		if(resource != null) {
	            IProject proj = resource.getProject();
	            return proj.isAccessible() && proj.hasNature(ntype);
    		}
	    	return false;
        }
    	catch (CoreException e) {return false;}
    }

}
