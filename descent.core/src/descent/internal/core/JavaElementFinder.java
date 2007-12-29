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
	
	/**
	 * Corrects some signatures. For example, it transforms C6Object
	 * into C6object6Object.
	 * @param signature the signature to correct
	 * @return the corrected signature
	 * 
	 * TODO: move this method somewhere else
	 */
	public static String correct(String signature) {
		if (signature != null) {
			if (signature.equals("C6Object")) {
				signature = "C6object6Object";
			} else if (signature.equals("C9ClassInfo")) {
				signature = "C6object9ClassInfo";
			} else if (signature.equals("C8TypeInfo")) {
				signature = "C6object8TypeInfo";
			} else if (signature.equals("C16TypeInfo_Typedef")) {
				signature = "C6object16TypeInfo_Typedef";
			} else if (signature.equals("C13TypeInfo_Enum")) {
				signature = "C6object13TypeInfo_Enum";
			} else if (signature.equals("C16TypeInfo_Pointer")) {
				signature = "C6object16TypeInfo_Pointer";
			} else if (signature.equals("C14TypeInfo_Array")) {
				signature = "C6object14TypeInfo_Array";
			} else if (signature.equals("C20TypeInfo_StaticArray")) {
				signature = "C6object20TypeInfo_StaticArray";
			} else if (signature.equals("C25TypeInfo_AssociativeArray")) {
				signature = "C6object25TypeInfo_AssociativeArray";
			} else if (signature.equals("C17TypeInfo_Function")) {
				signature = "C6object17TypeInfo_Function";
			} else if (signature.equals("C17TypeInfo_Delegate")) {
				signature = "C6object17TypeInfo_Delegate";
			} else if (signature.equals("C14TypeInfo_Class")) {
				signature = "C6object14TypeInfo_Class";
			} else if (signature.equals("C18TypeInfo_Interface")) {
				signature = "C6object18TypeInfo_Interface";
			} else if (signature.equals("C15TypeInfo_Struct")) {
				signature = "C6object15TypeInfo_Struct";
			} else if (signature.equals("C15TypeInfo_Struct")) {
				signature = "C6object15TypeInfo_Struct";
			} else if (signature.equals("C14TypeInfo_Tuple")) {
				signature = "C6object14TypeInfo_Tuple";
			} else if (signature.equals("C14TypeInfo_Const")) {
				signature = "C6object14TypeInfo_Const";
			} else if (signature.equals("C18TypeInfo_Invariant")) {
				signature = "C6object18TypeInfo_Const";
			} else if (signature.equals("C9Exception")) {
				signature = "C6object9Exception";
			}
		}
		return signature;
	}
	
	/**
	 * Finds the java element denoted by the given signature.
	 */
	public IJavaElement find(String signature) {
		// TODO and other types too
		signature = correct(signature);
		
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
	
	public static IJavaElement findChild(IJavaElement current, String name) throws JavaModelException {
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
	
	public static IJavaElement searchInChildren(IParent parent, String name) throws JavaModelException {
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
	
	public static IJavaElement findChild(IJavaProject project, String name) throws JavaModelException {
		IPackageFragment[] fragments = project.getPackageFragments();
		for(IPackageFragment fragment : fragments) {
			IJavaElement child = findChild(fragment, name);
			if (child != null) {
				return child;
			}
		}
		return null;
	}
	
	public static IJavaElement findChild(IPackageFragment fragment, String name) throws JavaModelException {
		if (fragment.isDefaultPackage()) {
			ICompilationUnit unit;
			
			// First class files, because if a class file is open, then there
			// will be a working copy *which does not have an underlying resource
			// with the CompilationUnit semantics*, and something fails.
			unit = fragment.getClassFile(name + ".d");
			if (unit != null && unit.exists()) {
				return unit;
			}
			unit = fragment.getCompilationUnit(name + ".d");
			if (unit != null && unit.exists()) {
				return unit;
			}
		} else if (fragment.getElementName().equals(name)) {
			return fragment;
		}
		
		return searchInChildren(fragment, name);
	}

}
