package descent.core.dom;

import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

public class TypeBinding extends JavaElementBasedBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final String key;
	
	public TypeBinding(DefaultBindingResolver resolver, IType element, String key) {
		super(element);
		this.bindingResolver = resolver;
		this.key = key;
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
	
	public IMethodBinding getDeclaringMethod() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ITypeBinding getDeclaringType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getDimension() {
		return 0;
	}
	
	public ITypeBinding[] getInterfaces() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getKey() {
		return key;
	}

	public ITypeBinding getKeyType() {
		return null;
	}

	public int getKind() {
		return TYPE;
	}

	public IPackageBinding getPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding[] getParametersTypes() {
		return PrimitiveTypeBinding.NO_TYPES;
	}

	public String getQualifiedName() {
		return ((IType) element).getFullyQualifiedName();
	}

	public ITypeBinding getReturnType() {
		return null;
	}

	public ITypeBinding getSuperclass() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITypeBinding getValueType() {
		return null;
	}

	public boolean isAnonymous() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAssignmentCompatible(ITypeBinding variableType) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAssociativeArray() {
		return false;
	}

	public boolean isCastCompatible(ITypeBinding type) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isClass() {
		try {
			return ((IType) element).isClass();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}

	public boolean isDelegate() {
		return false;
	}

	public boolean isDynamicArray() {
		return false;
	}

	public boolean isEnum() {
		try {
			return ((IType) element).isEnum();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}

	public boolean isEqualTo(IBinding binding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isFromSource() {
		return true;
	}
	
	public boolean isFunction() {
		return false;
	}

	public boolean isGenericType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInterface() {
		try {
			return ((IType) element).isInterface();
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

	public boolean isPointer() {
		return false;
	}

	public boolean isPrimitive() {
		return false;
	}

	public boolean isStaticArray() {
		return false;
	}

	public boolean isStruct() {
		try {
			return ((IType) element).isStruct();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}

	public boolean isSubTypeCompatible(ITypeBinding type) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isTemplate() {
		try {
			return ((IType) element).isTemplate();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}

	public boolean isTopLevel() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUnion() {
		try {
			return ((IType) element).isUnion();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}
	
	@Override
	public String toString() {
		return element.toString();
	}

}
