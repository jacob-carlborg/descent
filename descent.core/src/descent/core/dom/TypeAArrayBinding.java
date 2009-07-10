package descent.core.dom;

import descent.internal.compiler.parser.TypeAArray;

public class TypeAArrayBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final TypeAArray type;

	public TypeAArrayBinding(DefaultBindingResolver bindingResolver, TypeAArray type, String signature) {
		super(signature);
		this.bindingResolver = bindingResolver;
		this.type = type;
	}
	
	public ITypeBinding getComponentType() {
		return null;
	}
	
	public int getDimension() {
		return 0;
	}
	
	public ITypeBinding getKeyType() {
		return bindingResolver.resolveType(type.index);
	}
	
	public String getName() {
		// TODO optimize
		return getValueType().getName() + "[" + getKeyType().getName() + "]";
	}

	public ITypeBinding[] getParametersTypes() {
		return NO_TYPES;
	}
	
	public ITypeBinding getReturnType() {
		return null;
	}

	public ITypeBinding getValueType() {
		return bindingResolver.resolveType(type.next);
	}
	
	public boolean isAssignmentCompatible(ITypeBinding variableType) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isAssociativeArray() {
		return true;
	}
	
	public boolean isCastCompatible(ITypeBinding type) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDelegate() {
		return false;
	}
	
	public boolean isDynamicArray() {
		return false;
	}
	
	public boolean isFunction() {
		return false;
	}
	
	public boolean isNullType() {
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

	public boolean isSubTypeCompatible(ITypeBinding type) {
		// TODO Auto-generated method stub
		return false;
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
