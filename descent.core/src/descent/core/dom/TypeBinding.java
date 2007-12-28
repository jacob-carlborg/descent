package descent.core.dom;

import descent.core.IJavaElement;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

public class TypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final IType type;
	private final String key;
	
	public TypeBinding(DefaultBindingResolver resolver, IType type, String key) {
		this.bindingResolver = resolver;
		this.type = type;
		this.key = key;
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
		return type.getElementName();
	}

	public IPackageBinding getPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQualifiedName() {
		return type.getFullyQualifiedName();
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
		try {
			return type.isClass();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}

	public boolean isEnum() {
		try {
			return type.isEnum();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}
	
	public boolean isStruct() {
		try {
			return type.isStruct();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}
	
	public boolean isUnion() {
		try {
			return type.isUnion();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}

	public boolean isFromSource() {
		return true;
	}

	public boolean isGenericType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInterface() {
		try {
			return type.isInterface();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
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
		return false;
	}

	public boolean isParameterizedType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPrimitive() {
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
		return type;
	}

	public String getKey() {
		return key;
	}

	public int getKind() {
		return TYPE;
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
