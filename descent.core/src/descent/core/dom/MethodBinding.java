package descent.core.dom;

import descent.core.IMethod;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.core.util.Util;

public class MethodBinding extends JavaElementBasedBinding implements IMethodBinding {
	
	private final String signature;

	public MethodBinding(DefaultBindingResolver bindingResolver, FuncDeclaration node, String signature) {
		super(bindingResolver, node);
		this.signature = signature;		
	}

	public ITypeBinding getDeclaringClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getDefaultValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding[] getExceptionTypes() {
		// TODO Auto-generated method stub
		return null;
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

	public boolean isAnnotationMember() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isConstructor() {
		try {
			return ((IMethod) getJavaElement()).isConstructor();
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
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
