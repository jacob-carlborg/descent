package descent.internal.core;

import java.util.Stack;

import descent.core.ICompilationUnit;
import descent.core.IConditional;
import descent.core.IField;
import descent.core.IImportDeclaration;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMethod;
import descent.core.IPackageDeclaration;
import descent.core.IPackageFragment;
import descent.core.IParent;
import descent.core.ISourceRange;
import descent.core.ISourceReference;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
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
	private final CompilerConfiguration config;
	
	/*
	 * Cache results for speedup, and also for not loading multiple
	 * times the same module.
	 */
	private final HashtableOfCharArrayAndObject cache = new HashtableOfCharArrayAndObject();

	public JavaElementFinder(IJavaProject project, WorkingCopyOwner owner) {
		this.javaProject = (JavaProject) project;
		this.config = new CompilerConfiguration();
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
		private int templateInstanceCounter;
		
		public void acceptModule(char[][] compoundName, String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			if (typeFunctionCounter == 0) {
				element = findCompilationUnit(compoundName);
			} else {
				stack.push(signature);
			}
		}
		
		public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			if (element == null) {
				return;
			}
			
			if (templateInstanceCounter > 0) {
				return;
			}
			
			if (typeFunctionCounter == 0) {
				if (startPosition >= 0) {
					element = findChild(element, startPosition);
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
							type == ISignatureConstants.TEMPLATED_CLASS ||
							type == ISignatureConstants.TEMPLATED_STRUCT ||
							type == ISignatureConstants.TEMPLATED_INTERFACE ||
							type == ISignatureConstants.TEMPLATED_UNION) {
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
		
		@Override
		public void acceptIdentifier(char[] name, String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			stack.push(signature);
		}

		public void acceptArgumentModifier(int stc) {
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			if (templateInstanceCounter > 0) {
				return;
			}
			
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
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			stack.pop();
			stack.pop();
			stack.push(signature);
		}

		public void acceptDelegate(String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			stack.pop();
			stack.push(signature);
		}

		public void acceptDynamicArray(String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			stack.pop();
			stack.push(signature);
		}
		
		public void enterFunctionType() {
			if (templateInstanceCounter > 0) {
				return;
			}
			
			typeFunctionCounter++;
		}

		public void exitFunctionType(LINK link, String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			
			typeFunctionCounter--;
			
			if (typeFunctionCounter >= 1) {
				stack.push(signature);
				return;
			}
			
			String[] paramsAndReturnTypes = new String[stack.size()];
			paramsAndReturnTypes[stack.size() - 1] = stack.pop();
			int i = stack.size() - 1;
			while(!stack.isEmpty()) {
				if (modifiers.isEmpty()) {
					paramsAndReturnTypes[i] = stack.pop();
				} else {
					paramsAndReturnTypes[i] = modifiers.pop() + stack.pop();
				}
				i--;
			}
			
			paramsAndReturnTypesStack.push(paramsAndReturnTypes);
			
			stack.push(signature);
		}

		public void acceptPointer(String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			stack.pop();
			stack.push(signature);
		}

		public void acceptPrimitive(TypeBasic type) {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			stack.push(type.deco);
		}

		public void acceptStaticArray(int dimension, String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			stack.pop();
			stack.push(signature);
		}
		
		@Override
		public void acceptTemplateTupleParameter() {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			templateStack.push(String.valueOf(ISignatureConstants.TEMPLATE_TUPLE_PARAMETER));
		}
		
		@Override
		public void enterTemplateAliasParameter() {
			
		}
		
		@Override
		public void exitTemplateAliasParameter(String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			templateStack.push(signature);
		}
		
		@Override
		public void exitTemplateTypeParameter(String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			templateStack.push(signature);
		}
		
		@Override
		public void exitTemplateValueParameter(String signature) {
			if (templateInstanceCounter > 0) {
				return;
			}
			if (typeFunctionCounter >= 2) {
				return;
			}
			
			// Discard the type of the template value type
			stack.pop();
			templateStack.push(signature);
		}
		
		@Override
		public void enterTemplateInstance() {
			templateInstanceCounter++;
		}
		
		@Override
		public void exitTemplateInstance(String signature) {
			templateInstanceCounter--;
		}
		
	}
	
	public ICompilationUnit findCompilationUnit(char[][] compoundName) {
		char[] name = CharOperation.concatWith(compoundName, '.');
		ICompilationUnit unit = (ICompilationUnit) cache.get(name);
		if (unit == null) {
			unit = environment.findCompilationUnit(compoundName);
			if (unit != null) {
				cache.put(name, unit);
			}
		}
		return unit;
	}
	
	/**
	 * Finds the java element denoted by the given signature.
	 */
	public IJavaElement find(String signature) {
		InternalFinder finder = new InternalFinder();
		try {
			SignatureProcessor.process(signature, finder);
		} catch (Throwable e) {
			Util.log(e, "processing signature: " + signature);
			return null;
		}
		return finder.element;
	}
	
	/**
	 * Finds a function in the given parent with the given name and parameter
	 * and types signatures.
	 */
	public IJavaElement findFunction(IParent parent, String name, String[] paramsAndRetTypes) {
		try {
		loop:
			for(IJavaElement child : parent.getChildren()) {
				IParent searchInChildren = mustSearchInChildren(child);
				if (searchInChildren != null) {
					IJavaElement result = findFunction(searchInChildren, name, paramsAndRetTypes);
					if (result != null) {
						return result;
					}
					continue;
				}
				
				if (child.getElementType() == IJavaElement.METHOD &&
						child.getElementName().equals(name)) {
					IMethod method = (IMethod) child;
					String retType = method.getReturnType();
					String[] mParamTypes = method.getParameterTypes();
					if (mParamTypes.length == paramsAndRetTypes.length - 1) {
						
						// See if any of the parameters or the return type contains an
						// unresolved identifier
						boolean isUnresolved = isUnresolved(paramsAndRetTypes) || 
							isUnresolved(retType) || isUnresolved(mParamTypes);
						
						if (!isUnresolved) {
							if (retType.equals(paramsAndRetTypes[paramsAndRetTypes.length - 1])) {
								for(int i = 0; i < mParamTypes.length; i++) {
									if (!mParamTypes[i].equals(paramsAndRetTypes[i])) {
										continue loop;
									}
								}
							}
						} else {
							// If anything is unresolved, but the parameter count
							// is the same, just return it
							// TODO Descent JavaElementFinder improve unresolved
							return method;
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
	
	private static boolean isUnresolved(String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (isUnresolved(array[i])) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isUnresolved(String elem) {
		return elem.indexOf(ISignatureConstants.IDENTIFIER) != -1;
	}
	
	/**
	 * Finds a function in the given parent with the given name and parameter
	 * and types signatures.
	 */
	public IJavaElement findTemplatedFunction(IParent parent, 
			String name, 
			String[] paramsAndRetTypes, 
			String[] paramTypes) {
		try {
		loop:
			for(IJavaElement child : parent.getChildren()) {
				IParent searchInChildren = mustSearchInChildren(child);
				if (searchInChildren != null) {
					IJavaElement result = findFunction(searchInChildren, name, paramsAndRetTypes);
					if (result != null) {
						return result;
					}
					continue;
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
	public IJavaElement findTemplatedAggregate(IParent parent, 
			String name, 
			String[] paramTypes) {
		return findTemplate(parent, name, paramTypes);
	}
	
	/**
	 * Finds a template function in the given parent with the given name and parameter
	 * types signatures.
	 */
	public IJavaElement findTemplate(IParent parent, 
			String name, String[] paramTypes) {
		try {
		loop:
			for(IJavaElement child : parent.getChildren()) {
				IParent searchInChildren = mustSearchInChildren(child);
				if (searchInChildren != null) {
					IJavaElement result = findTemplate(searchInChildren, name, paramTypes);
					if (result != null) {
						return result;
					}
					continue;
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

	public IJavaElement findChild(IJavaElement current, String name) {
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
	
	public IJavaElement searchInChildren(IParent parent, String name) throws JavaModelException {
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
				continue;
			}
			
			if (!JavaElementFinder.isReturnTarget(child)) {
				continue;
			}
			
			String elementName = child.getElementName();
			
			if ((child instanceof ICompilationUnit && ((ICompilationUnit) child).getModuleName().equals(name))
					|| elementName.equals(name)) {
				return child;
			}
		}
		return null;
	}
	
	public IJavaElement findChild(IJavaProject project, String name) throws JavaModelException {
		IPackageFragment[] fragments = project.getPackageFragments();
		for(IPackageFragment fragment : fragments) {
			IJavaElement child = findChild(fragment, name);
			if (child != null) {
				return child;
			}
		}
		return null;
	}
	
	public IJavaElement findChild(IPackageFragment fragment, String name) throws JavaModelException {
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
	
	public IJavaElement findChild(IJavaElement current, int startPosition) {
		if (!(current instanceof IParent)) {
			return null;
		}
		
		try {
			for(IJavaElement child : ((IParent) current).getChildren()) {
				if (!(child instanceof ISourceReference)) {
					continue;
				}
				
				ISourceRange range = ((ISourceReference) child).getSourceRange();
				if (range != null) {
					int start = range.getOffset();
					if (start == startPosition) {
						return child;
					}
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return null;
	}
	
	/*
	 * Determines if an element is to be searched in it's children. For example,
	 * if element is an align declaration, this method returns true. If it's
	 * a version condition, the current version identifiers are checked to see
	 * if any of them is the one in the version condition, etc.
	 * 
	 * If it must be searched, the element to search is returned.
	 */
	public IParent mustSearchInChildren(IJavaElement element) {
		if (!(element instanceof IParent)) {
			return null;
		}
		
		try {
			switch(element.getElementType()) {
			case IJavaElement.INITIALIZER:
				IInitializer init = (IInitializer) element;
				// TODO consider others
				if (init.isAlign() || init.isExtern()) {
					return init;
				}
				break;
			case IJavaElement.FIELD:
				IField field = (IField) element;
				if (field.isTemplateMixin()) {
					return field;
				}
				break;
			case IJavaElement.CONDITIONAL:
				IConditional conditional = (IConditional) element;
				if (conditional.isVersionDeclaration()) {
					IJavaElement[] children = conditional.getChildren();
					String version = conditional.getElementName();
					if (version.length() > 0 && Character.isDigit(version.charAt(0))) {
						try {
							long level = Long.parseLong(version);
							if (level >= config.versionLevel) {
								return conditionalThen(conditional, children);
							} else {
								return conditionalElse(conditional, children);
							}
						} catch (NumberFormatException e) {
							if (config.versionIdentifiers.containsKey(version.toCharArray())) {
								return conditionalThen(conditional, children);
							} else {
								return conditionalElse(conditional, children);
							}
						}
					} else {
						if (config.versionIdentifiers.containsKey(version.toCharArray())) {
							return conditionalThen(conditional, children);
						} else {
							return conditionalElse(conditional, children);
						}
					}
				} else if (conditional.isDebugDeclaration()) {
					IJavaElement[] children = conditional.getChildren();
					String version = conditional.getElementName();
					if (version.length() > 0 && Character.isDigit(version.charAt(0))) {
						try {
							long level = Long.parseLong(version);
							if (level >= config.debugLevel) {
								return conditionalThen(conditional, children);
							} else {
								return conditionalElse(conditional, children);
							}
						} catch (NumberFormatException e) {
							if (config.debugIdentifiers.containsKey(version.toCharArray())) {
								return conditionalThen(conditional, children);
							} else {
								return conditionalElse(conditional, children);
							}
						}
					} else {
						if (config.debugIdentifiers.containsKey(version.toCharArray())) {
							return conditionalThen(conditional, children);
						} else {
							return conditionalElse(conditional, children);
						}
					}
				}
				break;
			case IJavaElement.TYPE:
				IType type = (IType) element;
				if (type.isAnonymous()) {
					return type;
				}
				break;
			case IJavaElement.IMPORT_CONTAINER:
				return (IParent) element;
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return null;
	}
	
	private static IParent conditionalThen(IConditional conditional, IJavaElement[] children) throws JavaModelException {
		if (children.length == 2 && 
			children[0].getElementType() == IJavaElement.INITIALIZER &&
			((IInitializer) children[0]).isThen()) {
			return (IParent) children[0];
		} else {
			return conditional;
		}
	}
	
	private static IParent conditionalElse(IConditional conditional, IJavaElement[] children) throws JavaModelException {
		if (children.length == 2 && 
				children[1].getElementType() == IJavaElement.INITIALIZER &&
				((IInitializer) children[1]).isElse()) {
			return (IParent) children[1];
		} else {
			return conditional;
		}
	}
	
	public static boolean isReturnTarget(IJavaElement element) {
		try {
			if (element.getElementType() == IJavaElement.INITIALIZER) {
				IInitializer init = (IInitializer) element;
				if (init.isAlign() || init.isExtern()) {
					return false;
				}
			} else if (element instanceof IConditional) {
				return false;
			} else if (element instanceof IType) {
				IType type = (IType) element;
				if (type.isAnonymous()) {
					return false;
				}
			}
			
			return true;
		} catch (JavaModelException e) {
			return false;
		}
	}

}
