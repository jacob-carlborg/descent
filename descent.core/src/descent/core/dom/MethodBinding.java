package descent.core.dom;

import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

public class MethodBinding extends JavaElementBasedBinding implements IMethodBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final String signature;

	public MethodBinding(DefaultBindingResolver bindingResolver, IMethod element, String signature) {
		super(element);
		this.bindingResolver = bindingResolver;
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

	public ITypeBinding[] getParameterTypes() {
		String[] parameterTypes = ((IMethod) element).getParameterTypes();
		ITypeBinding[] typeBindings = new ITypeBinding[parameterTypes.length];
		for(int i = 0; i < parameterTypes.length; i++) {
			typeBindings[i] = (ITypeBinding) bindingResolver.resolveBinding(parameterTypes[i]);
		}
		return typeBindings;
	}

	public ITypeBinding getReturnType() {
		try {
			String signature = ((IMethod) element).getReturnType();
			return (ITypeBinding) bindingResolver.resolveBinding(signature);
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return null;
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
			return ((IMethod) element).isConstructor();
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
		try {
			return ((IMethod) element).isTemplate();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}

	public boolean isVarargs() {
		// TODO Auto-generated method stub
		return false;
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
