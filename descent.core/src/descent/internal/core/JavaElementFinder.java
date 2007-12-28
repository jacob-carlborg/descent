package descent.internal.core;

import descent.core.ICompilationUnit;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

/**
 * Utility class to find a java element in a java project given its signature.
 */
public class JavaElementFinder {
	
	private final IJavaProject javaProject;

	public JavaElementFinder(IJavaProject project) {
		this.javaProject = project;
	}
	
	public IJavaElement find(String signature) {
		// TODO optimize using IJavaProject#find and NameLookup
		// TODO make an "extract number" function in order to avoid duplication
		try {
			if (signature == null || signature.length() == 0) {
				// TODO signal error
				return null;
			} else {
				char first = signature.charAt(0);
				
				switch(first) {
				case 'E': // enum
				case 'C': // class
				case 'S': // struct
				case 'T': // typedef
				case 'Q': // var, alias, typedef
					IJavaElement current = javaProject;
					
					for(int i = 1; i < signature.length(); i++) {
						char c = signature.charAt(i);
						int n = 0;
						while(Character.isDigit(c)) {
							n = 10 * n + (c - '0');
							i++;
							c = signature.charAt(i);
						}
						String name = signature.substring(i, i + n);
						current = findChild(current, name);
						if (current == null) {
							// TODO signal error
							break;
						}
						i += n - 1;
					}
					
					return current;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return null;
	}
	
	private static IJavaElement findChild(IJavaElement current, String name) throws JavaModelException {
		switch(current.getElementType()) {
		case IJavaElement.JAVA_PROJECT:
			return findChild((IJavaProject) current, name);
		case IJavaElement.PACKAGE_FRAGMENT:
			return findChild((IPackageFragment) current, name);
		}
		
		if (!(current instanceof IParent)) {
			return null;
		}
		
		return searchInChildren((IParent) current, name);
	}
	
	private static IJavaElement searchInChildren(IParent parent, String name) throws JavaModelException {
		for(IJavaElement child : parent.getChildren()) {
			if (child.getElementType() == IJavaElement.INITIALIZER) {
				IInitializer init = (IInitializer) child;
				// TODO consider other possibilities, like debug, version,
				// and static ifs
				if (init.isAlign()) {
					IJavaElement result = searchInChildren(init, name);
					if (result != null) {
						return result;
					}
				}
			}
			
			String elementName = child.getElementName();
			
			if ((child instanceof ICompilationUnit && ((ICompilationUnit) child).getModuleName().equals(name))
					|| elementName.equals(name)) {
				return child;
			}
		}
		return null;
	}
	
	private static IJavaElement findChild(IJavaProject project, String name) throws JavaModelException {
		IPackageFragment[] fragments = project.getPackageFragments();
		for(IPackageFragment fragment : fragments) {
			IJavaElement child = findChild(fragment, name);
			if (child != null) {
				return child;
			}
		}
		return null;
	}
	
	private static IJavaElement findChild(IPackageFragment fragment, String name) throws JavaModelException {
		if (fragment.isDefaultPackage()) {
			ICompilationUnit unit = fragment.getCompilationUnit(name + ".d");
			if (unit != null && unit.exists()) {
				return unit;
			}
			unit = fragment.getClassFile(name + ".d");
			if (unit != null && unit.exists()) {
				return unit;
			}
		} else if (fragment.getElementName().equals(name)) {
			return fragment;
		}
		
		return searchInChildren(fragment, name);
	}

}
