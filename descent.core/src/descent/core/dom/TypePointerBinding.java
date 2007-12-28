package descent.core.dom;

import descent.core.IJavaElement;

public class TypePointerBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final ITypeBinding type;
	private final String signature;

	public TypePointerBinding(DefaultBindingResolver bindingResolver, ITypeBinding type, String signature) {
		this.bindingResolver = bindingResolver;
		this.type = type;
		this.signature = signature;
	}

	public String getBinaryName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getBound() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getComponentType() {
		// TODO Auto-generated method stub
		return null;
	}

	public IVariableBinding[] getDeclaredFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public IMethodBinding[] getDeclaredMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getDeclaredModifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ITypeBinding[] getDeclaredTypes() {
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

	public int getDimensions() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ITypeBinding getElementType() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getErasure() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding[] getInterfaces() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getModifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPackageBinding getPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getSuperclass() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding[] getTypeArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding[] getTypeBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getTypeDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding[] getTypeParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getWildcard() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAnnotation() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAnonymous() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isArray() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAssignmentCompatible(ITypeBinding variableType) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCapture() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCastCompatible(ITypeBinding type) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isClass() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEnum() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isStruct() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isUnion() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFromSource() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isGenericType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInterface() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLocal() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isMember() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNested() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNullType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isParameterizedType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPrimitive() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRawType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubTypeCompatible(ITypeBinding type) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTopLevel() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTypeVariable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUpperbound() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWildcardType() {
		// TODO Auto-generated method stub
		return false;
	}

	public IAnnotationBinding[] getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	public IJavaElement getJavaElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getKind() {
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
