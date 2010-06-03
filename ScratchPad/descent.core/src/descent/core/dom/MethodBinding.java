package descent.core.dom;

import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.TypeFunction;

public class MethodBinding extends JavaElementBasedBinding implements IMethodBinding {
	
	private final String signature;

	public MethodBinding(DefaultBindingResolver bindingResolver, FuncDeclaration node, String signature) {
		super(bindingResolver, node);
		this.signature = signature;		
	}

	public ITypeBinding getDeclaringSymbol() {
		IBinding binding = bindingResolver.resolveDsymbol(node.parent, null);
		if (!(binding instanceof ITypeBinding)) {
			return null;
		}
		return (ITypeBinding) binding;
	}

	public IMethodBinding getMethodDeclaration() {
		return this;
	}

	public ITypeBinding[] getParameterTypes() {
		FuncDeclaration func = ((FuncDeclaration) node);
		TypeFunction typeFunction = ((TypeFunction) func.type);
		if (typeFunction == null) {
			System.out.println(123456);
			return NO_TYPES;
		}
		ITypeBinding[] params = new ITypeBinding[typeFunction.parameters.size()];
		for (int i = 0; i < params.length; i++) {
			params[i] = bindingResolver.resolveType(typeFunction.parameters.get(i).type, null);
		}
		return params;
	}

	public ITypeBinding getReturnType() {
		FuncDeclaration func = ((FuncDeclaration) node);
		TypeFunction typeFunction = ((TypeFunction) func.type);
		if (typeFunction == null) {
			System.out.println(123456);
			return null;
		}
		return bindingResolver.resolveType(typeFunction.next, null);
	}

	public ITypeBinding[] getTypeArguments() {
		// TODO Auto-generated method stub
		return NO_TYPES;
	}

	public ITemplateParameterBinding[] getTypeParameters() {
		return getTypeParameters(node, bindingResolver);
	}

	public boolean isConstructor() {
		return node instanceof CtorDeclaration;
	}

	public boolean isDefaultConstructor() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isGenericMethod() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isParameterizedMethod() {
		return node.templated();
	}

	public boolean isRawMethod() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubsignature(IMethodBinding otherMethod) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getVarargs() {
		FuncDeclaration func = ((FuncDeclaration) node);
		TypeFunction typeFunction = ((TypeFunction) func.type);
		return typeFunction.varargs;
	}

	public boolean overrides(IMethodBinding method) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getKey() {
		return signature;
	}

	public int getKind() {
		return METHOD;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

}
