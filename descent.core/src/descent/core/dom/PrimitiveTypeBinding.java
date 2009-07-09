package descent.core.dom;

import descent.core.IJavaElement;

public abstract class PrimitiveTypeBinding implements ITypeBinding {
	
	protected final static IVariableBinding[] NO_VARIABLES = new IVariableBinding[0];
	protected final static IMethodBinding[] NO_METHODS = new IMethodBinding[0];
	protected final static ITypeBinding[] NO_TYPES = new ITypeBinding[0];
	
	protected String signature;

	public PrimitiveTypeBinding(String signature) {
		this.signature = signature;
	}

	public final IVariableBinding[] getDeclaredFields() {
		return NO_VARIABLES;
	}

	public final IMethodBinding[] getDeclaredMethods() {
		return NO_METHODS;
	}

	public final int getDeclaredModifiers() {
		return 0;
	}

	public final ITypeBinding[] getDeclaredTypes() {
		return NO_TYPES;
	}

	public final ITypeBinding getDeclaringType() {
		return null;
	}

	public final IMethodBinding getDeclaringMethod() {
		return null;
	}

	public final ITypeBinding[] getInterfaces() {
		return NO_TYPES;
	}

	public final long getModifiers() {
		return 0;
	}

	public final IPackageBinding getPackage() {
		return null;
	}

	public final ITypeBinding getSuperclass() {
		return null;
	}
	
	public final String getQualifiedName() {
		return getName();
	}

	public final boolean isAnonymous() {
		return false;
	}

	public final boolean isClass() {
		return false;
	}

	public final boolean isEnum() {
		return false;
	}

	public final boolean isFromSource() {
		return false;
	}

	public final boolean isInterface() {
		return false;
	}

	public final boolean isLocal() {
		return false;
	}

	public final boolean isMember() {
		return false;
	}

	public final boolean isNested() {
		return false;
	}

	public final boolean isParameterizedType() {
		return false;
	}

	public final boolean isStruct() {
		return false;
	}

	public final boolean isUnion() {
		return false;
	}
	
	public boolean isTemplate() {
		return false;
	}

	public final IJavaElement getJavaElement() {
		return null;
	}

	public final String getKey() {
		return signature;
	}

	public final int getKind() {
		return TYPE;
	}

	public final boolean isDeprecated() {
		return false;
	}

	public final boolean isSynthetic() {
		return false;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IBinding)) {
			return false;
		}
			
		IBinding other = (IBinding) obj;
		return getKey().equals(other.getKey());
	}

}
