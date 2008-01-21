package descent.core.dom;

import descent.core.IJavaElement;

public class BuiltinPropertyBinding implements IVariableBinding {
	
	private final ITypeBinding typeBinding;
	private final String signature;
	private final String property;
	private final ITypeBinding myBinding;

	public BuiltinPropertyBinding(ITypeBinding typeBinding, ITypeBinding myBinding, String property, String signature) {
		this.typeBinding = typeBinding;
		this.myBinding = myBinding;
		this.property = property;
		this.signature = signature;
	}

	public Object getConstantValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getDeclaringClass() {
		return typeBinding;
	}

	public IMethodBinding getDeclaringMethod() {
		return null;
	}

	public String getName() {
		return property;
	}

	public IBinding getType() {
		return myBinding;
	}

	public IVariableBinding getVariableDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getVariableId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isAlias() {
		return false;
	}

	public boolean isEnumConstant() {
		return false;
	}

	public boolean isLocal() {
		return false;
	}

	public boolean isParameter() {
		return false;
	}

	public boolean isTypedef() {
		return false;
	}

	public boolean isVariable() {
		return true;
	}

	public IJavaElement getJavaElement() {
		return null;
	}

	public String getKey() {
		return signature;
	}

	public int getKind() {
		return VARIABLE;
	}

	public long getModifiers() {
		return 0;
	}

	public boolean isDeprecated() {
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
