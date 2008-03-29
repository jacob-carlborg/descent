package descent.core.dom;

import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.core.util.Util;

public class TypeBinding extends JavaElementBasedBinding implements ITypeBinding {
	
	private final String key;
	
	public TypeBinding(DefaultBindingResolver resolver, IType element, Dsymbol node, String key) {
		super(resolver, element, node);
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
		return ((IType) getJavaElement()).getFullyQualifiedName();
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
		if (node != null) {
			return node instanceof ClassDeclaration &&
				!(node instanceof InterfaceDeclaration);
		}
		
		try {
			return ((IType) getJavaElement()).isClass();
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
		if (node != null) {
			return node instanceof EnumDeclaration;
		}
		
		try {
			return ((IType) getJavaElement()).isEnum();
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
		if (node != null) {
			return node instanceof InterfaceDeclaration;
		}
		
		try {
			return ((IType) getJavaElement()).isInterface();
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
		if (node != null) {
			return node instanceof StructDeclaration &&
				!(node instanceof UnionDeclaration);
		}
		
		try {
			return ((IType) getJavaElement()).isStruct();
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
		if (node != null) {
			return node instanceof TemplateDeclaration ||
				((node instanceof descent.internal.compiler.parser.AggregateDeclaration) && 
						((descent.internal.compiler.parser.AggregateDeclaration) node).templated);
		}
		
		try {
			return ((IType) getJavaElement()).isTemplate();
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
		if (node != null) {
			return node instanceof UnionDeclaration;
		}
		
		try {
			return ((IType) getJavaElement()).isUnion();
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getJavaElement().toString();
	}

}
