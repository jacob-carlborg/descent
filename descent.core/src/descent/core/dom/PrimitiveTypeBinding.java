package descent.core.dom;

import descent.core.IJavaElement;

public abstract class PrimitiveTypeBinding extends AbstractBinding implements ITypeBinding {
	
	protected String signature;

	public PrimitiveTypeBinding(String signature) {
		this.signature = signature;
	}
	
	public ITypeBinding getAliasedType() {
		return null;
	}
	
	public IBinding getAliasedSymbol() {
		return null;
	}
	
	public ITypeBinding getTypedefedType() {
		return null;
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
	
	public final boolean isTemplate() {
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
	
	public final boolean isAlias() {
		return false;
	}
	
	public final boolean isTypedef() {
		return false;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
