package descent.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import descent.core.ICompilationUnit;
import descent.core.IConditional;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IParent;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.parser.Global;
import descent.internal.core.util.Util;

/**
 * Utility class to find a java element in a java project given its signature.
 */
public class JavaElementFinder {
	
	private final static Map<String, String> corrections = new HashMap<String, String>();
	private final static Map<String, String> uncorrections = new HashMap<String, String>();
	
	static {
		corrections.put("6Object", "6object6Object");
		corrections.put("9ClassInfo", "6object9ClassInfo");
		corrections.put("8TypeInfo", "6object8TypeInfo");
		corrections.put("16TypeInfo_Typedef", "6object16TypeInfo_Typedef");
		corrections.put("13TypeInfo_Enum", "6object13TypeInfo_Enum");
		corrections.put("16TypeInfo_Pointer", "6object16TypeInfo_Pointer");
		corrections.put("14TypeInfo_Array", "6object14TypeInfo_Array");
		corrections.put("20TypeInfo_StaticArray", "6object20TypeInfo_StaticArray");
		corrections.put("25TypeInfo_AssociativeArray", "6object25TypeInfo_AssociativeArray");
		corrections.put("17TypeInfo_Function", "6object17TypeInfo_Function");
		corrections.put("17TypeInfo_Delegate", "6object17TypeInfo_Delegate");
		corrections.put("14TypeInfo_Class", "6object14TypeInfo_Class");
		corrections.put("18TypeInfo_Interface", "6object13TypeInfo_Enum");
		corrections.put("15TypeInfo_Struct", "6object15TypeInfo_Struct");
		corrections.put("14TypeInfo_Tuple", "6object14TypeInfo_Tuple");
		corrections.put("14TypeInfo_Const", "6object14TypeInfo_Const");
		corrections.put("18TypeInfo_Invariant", "6object18TypeInfo_Invariant");
		corrections.put("9Exception", "6object9Exception");
		
		for(Map.Entry<String, String> kv : corrections.entrySet()) {
			uncorrections.put(kv.getValue(), kv.getKey());
		}
	}
	
	private final JavaProject javaProject;
	private final INameEnvironment environment;

	public JavaElementFinder(IJavaProject project, WorkingCopyOwner owner) {
		this.javaProject = (JavaProject) project;
		try {
			this.environment = new SearchableEnvironment(javaProject, owner);
		} catch (JavaModelException e) {
			Util.log(e);
			throw new RuntimeException(e);
		}
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
		if (signature != null && signature.length() > 0) {
			switch(signature.charAt(0)) {
			case 'E':
			case 'C':
			case 'S':
			case 'T':
			case 'Q':
			case 'O':
				String sub = signature.substring(1);
				for(Entry<String, String> entry : corrections.entrySet()) {
					if (sub.startsWith(entry.getKey())) {
						return signature.charAt(0) + entry.getValue() + sub.substring(entry.getKey().length());
					}
				}
				break;
			default:
				return signature;
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
		if (signature != null && signature.length() > 0) {
			switch(signature.charAt(0)) {
			case 'E':
			case 'C':
			case 'S':
			case 'T':
			case 'Q':
			case 'O':
				String sub = signature.substring(1);
				for(Entry<String, String> entry : uncorrections.entrySet()) {
					if (sub.startsWith(entry.getKey())) {
						return signature.charAt(0) + entry.getValue() + sub.substring(entry.getKey().length());
					}
				}
				break;
			default:
				return signature;
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
		// TODO refactor this code, it's become a mess!
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
				case 'O': // function
					
					char second = signature.charAt(1);
					if (second != 'O') {
						// Gather pieces
						List<char[]> piecesList = new ArrayList<char[]>();
						
						String name = null;
						
						int i;
						for(i = 1; i < signature.length(); i++) {
							char c = signature.charAt(i);
							if (!Character.isDigit(c)) {
								break;
							}
							int n = 0;
							while(Character.isDigit(c)) {
								n = 10 * n + (c - '0');
								i++;
								c = signature.charAt(i);
							}
							name = signature.substring(i, i + n);
							piecesList.add(name.toCharArray());
							i += n - 1;
						}
						
						// Search the deepest module. For example in
						// one.two.three.Four, Four can't be a module, but
						// three can be, and also two (with three a member).
						// So search compound names up to before the last piece.
						char[][] all = (char[][]) piecesList.toArray(new char[piecesList.size()][]);
						for(int j = all.length - 1; j >= 1; j--) {
							char[][] compoundName = new char[j][];
							System.arraycopy(all, 0, compoundName, 0, j);
							
							IJavaElement current = environment.findCompilationUnit(compoundName);
							if (current != null) {
								// Keep searching
								
								for(int k = j; k < all.length; k++) {
									current = findChild(current, new String(all[k]));
									if (current == null) {
										break;
									}
								}
								
								// If it's a function, search it using the parameters
								if (first == 'O' && current != null && 
										current.getParent() != null && 
										current instanceof IParent &&
										name != null) {
									String[] paramsAndRetType = getParametersAndReturnType(signature.substring(i + 1));
									current = findFunction((IParent) current.getParent(), name, paramsAndRetType);
								}
								
								return current;
							}
						}
					} else {
						IJavaElement current = find(signature.substring(1));
						int next = signature.indexOf('@') + 1;
						
						String name = null;
						int i;
						for(i = next; i < signature.length(); i++) {
							char c = signature.charAt(i);
							if (!Character.isDigit(c)) {
								break;
							}
							int n = 0;
							while(Character.isDigit(c)) {
								n = 10 * n + (c - '0');
								i++;
								c = signature.charAt(i);
							}
							name = signature.substring(i, i + n);
							current = findChild(current, name);
							i += n - 1;
						}
						
						// If it's a function, search it using the parameters
						if (first == 'O' && current != null && 
								current.getParent() != null && 
								current instanceof IParent &&
								name != null) {
							String[] paramsAndRetType = getParametersAndReturnType(signature.substring(i + 1));
							current = findFunction((IParent) current.getParent(), name, paramsAndRetType);
						}
						
						return current;
					}
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return null;
	}

	/**
	 * Returns the parameters and return types of a function signature.
	 * The function signature must not start with the starting F (it must
	 * start with the immediately next character).
	 */
	public static String[] getParametersAndReturnType(String signature) {
		boolean foundSeparator = false;
		
		List<String> types = new ArrayList<String>();
		for(int i = 0; i < signature.length(); i++) {
			char c = signature.charAt(i);
			if (c == 'X' || c == 'Y' || c == 'Z') {
				foundSeparator = true;
				continue;
			}
			int start = i;
			int end = getEnd(signature, i);
			try {
				types.add(signature.substring(start, end));
			} catch (Throwable t) {
				t.printStackTrace();
			}
			i = end - 1;
			
			if (foundSeparator) {
				break;
			}
		}
		return (String[]) types.toArray(new String[types.size()]);
	}
	
	private static int getEnd(String signature, int i) {
		try {
			char c = signature.charAt(i);
			switch(c) {
			case 'E': // enum
			case 'C': // class
			case 'S': // struct
				i++;
				c = signature.charAt(i);
				if (Character.isDigit(c)) {				
					while(Character.isDigit(c)) {
						int n = 0;
						while(Character.isDigit(c)) {
							n = 10 * n + (c - '0');
							i++;
							c = signature.charAt(i);
						}
						i += n;
						if (i >= signature.length()) {
							break;
						}
						c = signature.charAt(i);
					}
				}
				return i;
			case 'D': // delegate
			case 'P': // pointer
			case 'A': // dynamic array
			case 'J': // out
			case 'K': // inout
			case 'L': // lazy
				return getEnd(signature, i + 1);
			case 'G': // static array
				for(i = 1; i < signature.length(); i++) {
					c = signature.charAt(i);
					while(Character.isDigit(c)) {
						i++;
						c = signature.charAt(i);
					}
					break;
				}
				return getEnd(signature, i);
			case 'H': // associative array
				i = getEnd(signature, i + 1);
				i = getEnd(signature, i);
				return i;
			case 'F': // Type function
			case 'U':
			case 'W':
			case 'V':
			case 'R':
				i = getEnd(signature, i + 1);
				
				while(i < signature.length() && 
						signature.charAt(i) != 'X' &&
						signature.charAt(i) != 'Y' &&
						signature.charAt(i) != 'Z') {
					i = getEnd(signature, i + 1);
				}
	
				i = getEnd(signature, i + 1);
				return i + 1;
			default:
				// Primitive type, or X, Y, Z
				return i + 1;
			}
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Finds a function in the given parent with the given name and parameter
	 * and types signatures.
	 * @see #getParametersAndReturnType(String)
	 */
	public static IJavaElement findFunction(IParent parent, String name, String[] paramsAndRetTypes) throws JavaModelException {
		for(IJavaElement child : parent.getChildren()) {
			IParent searchInChildren = mustSearchInChildren(child);
			if (searchInChildren != null) {
				IJavaElement result = findFunction(searchInChildren, name, paramsAndRetTypes);
				if (result != null) {
					return result;
				}
			}
			
			if (child.getElementType() == IJavaElement.METHOD &&
					child.getElementName().equals(name)) {
				IMethod method = (IMethod) child;
				String retType = method.getReturnType();
				String[] paramTypes = method.getParameterTypes();
				if (paramTypes.length == paramsAndRetTypes.length - 1) {
					if (retType.equals(paramsAndRetTypes[paramsAndRetTypes.length - 1])) {
						for(int i = 0; i < paramTypes.length; i++) {
							if (!paramTypes[i].equals(paramsAndRetTypes[i])) {
								continue;
							}
						}
						return method;
					}
				}
			}
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
			IParent searchInChildren = mustSearchInChildren(child);
			if (searchInChildren != null) {
				IJavaElement result = searchInChildren(searchInChildren, name);
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
	 * 
	 * If it must be searched, the element to search is returned.
	 */
	public static IParent mustSearchInChildren(IJavaElement element) {
		if (!(element instanceof IParent)) {
			return null;
		}
		
		try {
			if (element.getElementType() == IJavaElement.INITIALIZER) {
				IInitializer init = (IInitializer) element;
				// TODO consider others
				if (init.isAlign() || init.isExtern()) {
					return init;
				}
			}
			
			// For now, hack and use the predefined versions
			else if (element instanceof IConditional) {
				IConditional conditional = (IConditional) element;
				if (conditional.isVersionDeclaration()) {
					IJavaElement[] children = conditional.getChildren();
					
					char[] version = conditional.getElementName().toCharArray();
					for(char[] v : global.params.versionids) {
						if (CharOperation.equals(v, version)) {
							if (children.length == 2 && 
								children[0].getElementType() == IJavaElement.INITIALIZER &&
								((IInitializer) children[0]).isThen()) {
								return (IParent) children[0];
							} else {
								return conditional;
							}
						}
					}
					
					if (children.length == 2 && 
							children[1].getElementType() == IJavaElement.INITIALIZER &&
							((IInitializer) children[1]).isElse()) {
						return (IParent) children[1];
					}
				}
			}
			
			// Search in anonymous types
			else if (element instanceof IType) {
				IType type = (IType) element;
				if (type.isAnonymous()) {
					return type;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return null;
	}

}
