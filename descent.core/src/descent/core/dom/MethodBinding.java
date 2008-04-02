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

	public IBinding getDeclaringSymbol() {
		return bindingResolver.resolveDsymbol(node.parent);
	}

	public IMethodBinding getMethodDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public IBinding[] getParameterTypes() {
		FuncDeclaration func = ((FuncDeclaration) node);
		TypeFunction typeFunction = ((TypeFunction) func.type);
		IBinding[] params = new IBinding[typeFunction.parameters.size()];
		for (int i = 0; i < params.length; i++) {
			params[i] = bindingResolver.resolveType(typeFunction.parameters.get(i).type);
		}
		return params;
	}

	public IBinding getReturnType() {
		FuncDeclaration func = ((FuncDeclaration) node);
		TypeFunction typeFunction = ((TypeFunction) func.type);
		return bindingResolver.resolveType(typeFunction.next);
	}

	public ITypeBinding[] getTypeArguments() {
		// TODO Auto-generated method stub
		return PrimitiveTypeBinding.NO_TYPES;
	}

	public ITypeBinding[] getTypeParameters() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRawMethod() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubsignature(IMethodBinding otherMethod) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isTemplate() {
		// TODO
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

	public boolean isEqualTo(IBinding binding) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

}
