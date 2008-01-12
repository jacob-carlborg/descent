package descent.internal.core;

import java.util.Stack;

import descent.core.ICompilationUnit;
import descent.core.IConditional;
import descent.core.IImportDeclaration;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMethod;
import descent.core.IPackageDeclaration;
import descent.core.IPackageFragment;
import descent.core.IParent;
import descent.core.ISourceReference;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.TypeBasic;
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
	
	private class InternalFinder extends SignatureRequestorAdapter {
		
		private IJavaElement element;
		private Stack<String> stack = new Stack<String>();
		private Stack<String> modifiers = new Stack<String>();
		private Stack<String> templateStack = new Stack<String>();
		private Stack<String[]> paramsAndReturnTypesStack = new Stack<String[]>();
		
		private int typeFunctionCounter;
		
		public void acceptModule(char[][] compoundName, String signature) {
			if (typeFunctionCounter == 0) {
				element = findCompilationUnit(compoundName);
			} else {
				stack.push(signature);
			}
		}
		
		public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
			if (element == null) {
				return;
			}
			
			if (typeFunctionCounter == 0) {
				if (startPosition >= 0) {
					element = JavaElementFinder.findChild(element, startPosition);
				} else {
					if (type == ISignatureConstants.FUNCTION ||
							type == ISignatureConstants.TEMPLATED_FUNCTION) {
						if (stack.isEmpty() ||  element == null || !(element instanceof IParent)) {
							return;
						}
						
						// Pop the type function signature
						stack.pop();
						
						String[] paramsAndReturnTypes = paramsAndReturnTypesStack.pop();
						
						if (type == ISignatureConstants.FUNCTION) {
							element = findFunction((IParent) element, new String(name), paramsAndReturnTypes);
						} else {
							String[] paramTypes = new String[templateStack.size()];
							int i = paramTypes.length - 1;
							while(!templateStack.isEmpty()) {
								paramTypes[i] = templateStack.pop();
								i--;
							}
							
							element = findTemplatedFunction((IParent) element, new String(name), paramsAndReturnTypes, paramTypes);
						}
					} else if (type == ISignatureConstants.TEMPLATE ||
							type == ISignatureConstants.TEMPLATED_AGGREGATE) {
						if (element == null || !(element instanceof IParent)) {
							return;
						}
						
						String[] paramTypes = new String[templateStack.size()];
						int i = paramTypes.length - 1;
						while(!templateStack.isEmpty()) {
							paramTypes[i] = templateStack.pop();
							i--;
						}
						
						if (type == ISignatureConstants.TEMPLATE) {
							element = findTemplate((IParent) element, new String(name), paramTypes);
						} else {
							element = findTemplatedAggregate((IParent) element, new String(name), paramTypes);
						}
					} else {
						element = findChild(element, new String(name));
					}
				}
			} else {
				stack.pop();
				stack.push(signature);
			}
		}

		public void acceptArgumentModifier(int stc) {
			if (stc == STC.STCin) {
				modifiers.push("");
			} else if (stc == (STC.STCout)) {
				modifiers.push("J");
			} else if (stc == (STC.STCref)) {
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

		public void acceptDelegate(String signature) {
			stack.pop();
			stack.push(signature);
		}

		public void acceptDynamicArray(String signature) {
			stack.pop();
			stack.push(signature);
		}
		
		public void enterFunctionType() {
			typeFunctionCounter++;
		}

		public void exitFunctionType(LINK link, String signature) {
			String[] paramsAndReturnTypes = new String[stack.size()];
			paramsAndReturnTypes[stack.size() - 1] = stack.pop();
			int i = stack.size() - 1;
			while(!stack.isEmpty()) {
				if (modifiers.isEmpty()) {
					return;
				}
				
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
		
		@Override
		public void acceptTemplateTupleParameter() {
			templateStack.push(String.valueOf(ISignatureConstants.TEMPLATE_TUPLE_PARAMETER));
		}
		
		@Override
		public void enterTemplateAliasParameter() {
			
		}
		
		@Override
		public void exitTemplateAliasParameter(String signature) {
			templateStack.push(signature);
		}
		
		@Override
		public void exitTemplateTypeParameter(String signature) {
			templateStack.push(signature);
		}
		
		@Override
		public void exitTemplateValueParameter(String signature) {
			// Discard the type of the template value type
			stack.pop();
			templateStack.push(signature);
		}
		
	}
	
	public ICompilationUnit findCompilationUnit(char[][] compoundName) {
		return environment.findCompilationUnit(compoundName);
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
	 */
	public static IJavaElement findFunction(IParent parent, String name, String[] paramsAndRetTypes) {
		try {
		loop:
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
					String[] mParamTypes = method.getParameterTypes();
					if (mParamTypes.length == paramsAndRetTypes.length - 1) {
						if (retType.equals(paramsAndRetTypes[paramsAndRetTypes.length - 1])) {
							for(int i = 0; i < mParamTypes.length; i++) {
								if (!mParamTypes[i].equals(paramsAndRetTypes[i])) {
									continue loop;
								}
							}
						}
					} else {
						continue;
					}
					
					return method;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return null;
	}
	
	/**
	 * Finds a function in the given parent with the given name and parameter
	 * and types signatures.
	 */
	public static IJavaElement findTemplatedFunction(IParent parent, String name, String[] paramsAndRetTypes, String[] paramTypes) {
		try {
		loop:
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
					if (!method.isTemplate()) {
						continue;
					}
					
					String retType = method.getReturnType();
					String[] mParamTypes = method.getParameterTypes();
					if (mParamTypes.length == paramsAndRetTypes.length - 1) {
						if (retType.equals(paramsAndRetTypes[paramsAndRetTypes.length - 1])) {
							for(int i = 0; i < mParamTypes.length; i++) {
								if (!mParamTypes[i].equals(paramsAndRetTypes[i])) {
									continue loop;
								}
							}
						}
					} else {
						continue;
					}
					
					mParamTypes = method.getTypeParameterSignatures();
					if (mParamTypes.length == paramTypes.length) {
						for(int i = 0; i < mParamTypes.length; i++) {
							if (!mParamTypes[i].equals(paramTypes[i])) {
								continue loop;
							}
						}
					} else {
						continue;
					}
					
					return method;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return null;
	}
	
	/**
	 * Finds a template aggregate in the given parent with the given name and parameter
	 * types signatures.
	 */
	public static IJavaElement findTemplatedAggregate(IParent parent, String name, String[] paramTypes) {
		return findTemplate(parent, name, paramTypes);
	}
	
	/**
	 * Finds a template function in the given parent with the given name and parameter
	 * types signatures.
	 */
	public static IJavaElement findTemplate(IParent parent, String name, String[] paramTypes) {
		try {
		loop:
			for(IJavaElement child : parent.getChildren()) {
				IParent searchInChildren = mustSearchInChildren(child);
				if (searchInChildren != null) {
					IJavaElement result = findTemplate(searchInChildren, name, paramTypes);
					if (result != null) {
						return result;
					}
				}
				
				if (child.getElementType() == IJavaElement.TYPE &&
						child.getElementName().equals(name)) {
					IType type = (IType) child;
					if (!type.isTemplate()) {
						continue;
					}
					String[] mParamTypes = type.getTypeParameterSignatures();
					if (mParamTypes.length == paramTypes.length) {
						for(int i = 0; i < mParamTypes.length; i++) {
							if (!mParamTypes[i].equals(paramTypes[i])) {
								continue loop;
							}
						}
					} else {
						continue;
					}
					
					return type;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return null;
	}

	public static IJavaElement findChild(IJavaElement current, String name) {
		try {
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
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return null;		
	}
	
	public static IJavaElement searchInChildren(IParent parent, String name) throws JavaModelException {
		for(IJavaElement child : parent.getChildren()) {
			if (child instanceof IPackageDeclaration ||
				child instanceof IImportDeclaration) {
				continue;
			}
			
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
	
	public static IJavaElement findChild(IJavaElement current, int startPosition) {
		if (!(current instanceof IParent)) {
			return null;
		}
		
		try {
			for(IJavaElement child : ((IParent) current).getChildren()) {
				if (!(child instanceof ISourceReference)) {
					continue;
				}
				
				int start = ((ISourceReference) child).getSourceRange().getOffset();
				if (start == startPosition) {
					return child;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return null;
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
