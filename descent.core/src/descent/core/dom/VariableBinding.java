package descent.core.dom;

import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

public class VariableBinding implements IVariableBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final IField field;
	private final String signature;

	public VariableBinding(DefaultBindingResolver bindingResolver, IField field, String signature) {
		this.bindingResolver = bindingResolver;
		this.field = field;
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

	public String getName() {
		return field.getElementName();
	}

	public ITypeBinding getType() {
		try {
			String signature = field.getTypeSignature();
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
			return field.isEnumConstant();
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}

	public boolean isField() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isParameter() {
		// TODO Auto-generated method stub
		return false;
	}

	public IAnnotationBinding[] getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	public IJavaElement getJavaElement() {
		return field;
	}

	public String getKey() {
		return signature;
	}

	public int getKind() {
		return VARIABLE;
	}

	public int getModifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isDeprecated() {
		// TODO Auto-generated method stub
		return false;
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
