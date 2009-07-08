package descent.core.dom;

import descent.core.IType;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;

public class TypeBinding extends JavaElementBasedBinding implements ITypeBinding {
	
	private final String key;
	
	public TypeBinding(DefaultBindingResolver resolver, Dsymbol node, String key) {
		super(resolver, node);
		this.key = key;
	}
	
	public ITypeBinding getComponentType() {
		return null;
	}
	
	public IVariableBinding[] getDeclaredFields() {
		return null;
	}
	
	public IMethodBinding[] getDeclaredMethods() {
		return null;
	}
	
	public int getDeclaredModifiers() {
		return 0;
	}
	
	public ITypeBinding[] getDeclaredTypes()  {
		return null;
	}
	
	public IMethodBinding getDeclaringMethod() {
		return null;
	}
	
	public ITypeBinding getDeclaringType() {
		return null;
	}
	
	public int getDimension() {
		return 0;
	}
	
	public ITypeBinding[] getInterfaces() {
		if (!(node instanceof ClassDeclaration))
			return null;
		
		ClassDeclaration c = (ClassDeclaration) node;
		if (c.interfaces == null || c.interfaces.isEmpty()) {
			return new ITypeBinding[0];
		}
		
		ITypeBinding[] types = new ITypeBinding[c.interfaces.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = (ITypeBinding) bindingResolver.resolveDsymbol(c.interfaces.get(i).base);
		}
		return types;
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
		if (!(node instanceof ClassDeclaration))
			return null;
		
		ClassDeclaration c = (ClassDeclaration) node;
		return (ITypeBinding) bindingResolver.resolveDsymbol(c.baseClass);
	}

	public ITypeBinding getValueType() {
		return null;
	}

	public boolean isAnonymous() {
		return false;
	}

	public boolean isAssignmentCompatible(ITypeBinding variableType) {
		return false;
	}

	public boolean isAssociativeArray() {
		return false;
	}

	public boolean isCastCompatible(ITypeBinding type) {
		return false;
	}

	public boolean isClass() {
		return node instanceof ClassDeclaration 
			&& !(node instanceof InterfaceDeclaration);
	}

	public boolean isDelegate() {
		return false;
	}

	public boolean isDynamicArray() {
		return false;
	}

	public boolean isEnum() {
		return node instanceof EnumDeclaration;
	}

	public boolean isEqualTo(IBinding binding) {
		return false;
	}
	
	public boolean isFromSource() {
		return true;
	}
	
	public boolean isFunction() {
		return false;
	}

	public boolean isGenericType() {
		return false;
	}

	public boolean isInterface() {
		return node instanceof InterfaceDeclaration;
	}

	public boolean isLocal() {
		return false;
	}

	public boolean isMember() {
		return false;
	}

	public boolean isNested() {
		return false;
	}

	public boolean isNullType() {
		return false;
	}

	public boolean isParameterizedType() {
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
		return node instanceof StructDeclaration &&
			!(node instanceof UnionDeclaration);
	}

	public boolean isSubTypeCompatible(ITypeBinding type) {
		return false;
	}

	public boolean isSynthetic() {
		return false;
	}
	
	public boolean isTemplate() {
		return node instanceof TemplateDeclaration ||
			((node instanceof descent.internal.compiler.parser.AggregateDeclaration) && 
					((descent.internal.compiler.parser.AggregateDeclaration) node).templated);
	}

	public boolean isTopLevel() {
		return false;
	}

	public boolean isUnion() {
		return node instanceof UnionDeclaration;
	}
	
	@Override
	public String toString() {
		return getJavaElement().toString();
	}

	public int getLowerBound() {
		return 0;
	}

	public int getUpperBound() {
		return 0;
	}

	public boolean isSlice() {
		return false;
	}

}
