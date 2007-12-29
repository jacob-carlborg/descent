package descent.core.dom;

import descent.core.IField;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

public class VariableBinding extends JavaElementBasedBinding implements IVariableBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final String signature;

	public VariableBinding(DefaultBindingResolver bindingResolver, IField element, String signature) {
		super(element);
		this.bindingResolver = bindingResolver;
		this.signature = signature;
	}

	public Object getConstantValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getDeclaringClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public IMethodBinding getDeclaringMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getType() {
		try {
			String signature = ((IField) element).getTypeSignature();
			return (ITypeBinding) bindingResolver.resolveBinding(signature);
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return null;
	}

	public IVariableBinding getVariableDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getVariableId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEnumConstant() {
		try {
			return ((IField) element).isEnumConstant();
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}

	public boolean isField() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isParameter() {
		// TODO Auto-generated method stub
		return false;
	}

	public IAnnotationBinding[] getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getKey() {
		return signature;
	}

	public int getKind() {
		return VARIABLE;
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
