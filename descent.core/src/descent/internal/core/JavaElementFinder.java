package descent.internal.core;

import java.util.Stack;

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
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.core.SignatureProcessor.ISignatureRequestor;
import descent.internal.core.util.Util;

/**
 * Utility class to find a java element in a java project given its signature.
 */
public class JavaElementFinder {
	
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
	
	// TODO improve performance to not find a type that is part of the
	// signature of an internal function
	private class InternalFinder implements ISignatureRequestor {
		
		private IJavaElement element;
		private Stack<String> stack = new Stack<String>();
		private Stack<String> modifiers = new Stack<String>();
		private Stack<String[]> paramsAndReturnTypesStack = new Stack<String[]>();
		
		private int typeFunctionCounter;

		public void acceptArgumentBreak(char c) {
			// empty
		}

		public void acceptArgumentModifier(int stc) {
			if (stc == STC.STCin) {
				modifiers.push("");
			} else if (stc == (STC.STCout)) {
				modifiers.push("J");
			} else if (stc == (STC.STCin | STC.STCout)) {
				modifiers.push("K");
			} else if (stc == STC.STClazy) {
				modifiers.push("L");
			} else {
				throw new IllegalStateException();
			}
		}

		public void acceptAssociativeArray(String signature) {
			stack.pop();
			stack.pop();
			stack.push(signature);
		}

		public void acceptClass(char[][] compoundName, String signature) {
			acceptType(compoundName, signature);
		}

		public void acceptDelegate(String signature) {
			stack.pop();
			stack.push(signature);
		}

		public void acceptDynamicArray(String signature) {
			stack.pop();
			stack.push(signature);
		}

		public void acceptEnum(char[][] compoundName, String signature) {
			acceptType(compoundName, signature);
		}

		public void acceptFunction(char[][] compoundName, String signature) {
			// Pop the type function signature
			stack.pop();
			
			String[] paramsAndReturnTypes = paramsAndReturnTypesStack.pop();
			
			IParent parent = null;
			if (element == null) {
				acceptType(compoundName, signature);
				stack.pop();
				
				if (element == null || !(element instanceof IParent)) {
					return;
				}
				
				parent = (IParent) element.getParent();
			} else {
				parent = (IParent) element;
			}
			
			try {
				element = findFunction(parent, new String(compoundName[compoundName.length - 1]), paramsAndReturnTypes);
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		
		public void enterFunctionType() {
			typeFunctionCounter++;
		}

		public void exitFunctionType(LINK link, String signature) {
			String[] paramsAndReturnTypes = new String[stack.size()];
			paramsAndReturnTypes[stack.size() - 1] = stack.pop();
			int i = stack.size() - 1;
			while(!stack.isEmpty()) {
				paramsAndReturnTypes[i] = modifiers.pop() + stack.pop();
				i--;
			}
			
			paramsAndReturnTypesStack.push(paramsAndReturnTypes);
			
			stack.push(signature);
			
			typeFunctionCounter--;
		}

		public void acceptPointer(String signature) {
			stack.pop();
			stack.push(signature);
		}

		public void acceptPrimitive(TypeBasic type) {
			stack.push(type.deco);
		}

		public void acceptStaticArray(int dimension, String signature) {
			stack.pop();
			stack.push(signature);
		}

		public void acceptStruct(char[][] compoundName, String signature) {
			acceptType(compoundName, signature);
		}

		public void acceptVariableAliasOrTypedef(char[][] compoundName, String signature) {
			acceptType(compoundName, signature);
		}
		
		private void acceptType(char[][] all, String signature) {
			stack.push(signature);
			
			if (typeFunctionCounter > 0) {
				return;
			}
			
			try {
				if (element == null) {
					element = find(all);
				} else {
					element = findChild(element, new String(all[all.length - 1]));
				}
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		
	}
	
	/**
	 * Finds a java element denoted by the given compound name.
	 * @param compoundName the compound name
	 * @return the element, if any, or null
	 */
	public IJavaElement find(char[][] compoundName) {
		IJavaElement element = null;
		
		try {
			for(int j = compoundName.length - 1; j >= 1; j--) {
				char[][] compoundName0 = new char[j][];
				System.arraycopy(compoundName, 0, compoundName0, 0, j);
				
				element = environment.findCompilationUnit(compoundName0);
				if (element != null) {
					// Keep searching ...
					for(int k = j; k < compoundName.length; k++) {
						element = findChild(element, new String(compoundName[k]));
						if (element == null) {
							break;
						}
					}
					
					if (element != null) {
						return element;
					}
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return element;
	}
	
	/**
	 * Finds the java element denoted by the given signature.
	 */
	public IJavaElement find(String signature) {
		InternalFinder finder = new InternalFinder();
		SignatureProcessor.process(signature, finder);
		return finder.element;
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
