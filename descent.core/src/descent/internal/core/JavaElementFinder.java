package descent.internal.core;

import java.util.HashMap;
import java.util.Map;

import descent.core.ICompilationUnit;
import descent.core.IConditional;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.Global;
import descent.internal.core.util.Util;

/**
 * Utility class to find a java element in a java project given its signature.
 */
public class JavaElementFinder {
	
	private final static Map<String, String> corrections = new HashMap<String, String>();
	private final static Map<String, String> uncorrections = new HashMap<String, String>();
	
	static {
		corrections.put("C6Object", "C6object6Object");
		corrections.put("C9ClassInfo", "C6object9ClassInfo");
		corrections.put("C8TypeInfo", "C6object8TypeInfo");
		corrections.put("C16TypeInfo_Typedef", "C6object16TypeInfo_Typedef");
		corrections.put("C13TypeInfo_Enum", "C6object13TypeInfo_Enum");
		corrections.put("C16TypeInfo_Pointer", "C6object16TypeInfo_Pointer");
		corrections.put("C14TypeInfo_Array", "C6object14TypeInfo_Array");
		corrections.put("C20TypeInfo_StaticArray", "C6object20TypeInfo_StaticArray");
		corrections.put("C25TypeInfo_AssociativeArray", "C6object25TypeInfo_AssociativeArray");
		corrections.put("C17TypeInfo_Function", "C6object17TypeInfo_Function");
		corrections.put("C17TypeInfo_Delegate", "C6object17TypeInfo_Delegate");
		corrections.put("C14TypeInfo_Class", "C6object14TypeInfo_Class");
		corrections.put("C18TypeInfo_Interface", "C6object13TypeInfo_Enum");
		corrections.put("C15TypeInfo_Struct", "C6object15TypeInfo_Struct");
		corrections.put("C14TypeInfo_Tuple", "C6object14TypeInfo_Tuple");
		corrections.put("C14TypeInfo_Const", "C6object14TypeInfo_Const");
		corrections.put("C18TypeInfo_Invariant", "C6object18TypeInfo_Const");
		corrections.put("C9Exception", "C6object9Exception");
		
		for(Map.Entry<String, String> kv : corrections.entrySet()) {
			uncorrections.put(kv.getValue(), kv.getKey());
		}
	}
	
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
			String correction = corrections.get(signature);
			if (correction != null) {
				return correction;
			}
		}
		return signature;
	}
	
	/**
	 * Uncorrects some signatures. For example, it transforms C6object6Object
	 * into C6Object.
	 * @param signature the signature to correct
	 * @return the corrected signature
	 * 
	 * TODO: move this method somewhere else
	 */
	public static String uncorrect(String signature) {
		if (signature != null) {
			String correction = uncorrections.get(signature);
			if (correction != null) {
				return correction;
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
			if (mustSearchInChildren(child)) {
				IJavaElement result = searchInChildren((IParent) child, name);
				if (result != null) {
					return result;
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
	
	private static Global global = new Global();
	
	/*
	 * Determines if an element is to be searched in it's children. For example,
	 * if element is an align declaration, this method returns true. If it's
	 * a version condition, the current version identifiers are checked to see
	 * if any of them is the one in the version condition, etc.
	 */
	public static boolean mustSearchInChildren(IJavaElement element) {
		if (!(element instanceof IParent)) {
			return false;
		}
		
		try {
			if (element.getElementType() == IJavaElement.INITIALIZER) {
				IInitializer init = (IInitializer) element;
				// TODO consider others
				if (init.isAlign()) {
					return true;
				}
			}
			
			// For now, hack and use the predefined versions
			if (element instanceof IConditional) {
				IConditional conditional = (IConditional) element;
				if (conditional.isVersionDeclaration()) {
					char[] version = conditional.getElementName().toCharArray();
					for(char[] v : global.params.versionids) {
						if (CharOperation.equals(v, version)) {
							return true;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return false;
	}

}
