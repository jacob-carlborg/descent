package descent.core.dom;

import descent.internal.compiler.parser.TypePointer;


public class TypePointerBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final TypePointer type;

	public TypePointerBinding(DefaultBindingResolver bindingResolver, TypePointer type, String signature) {
		super(signature);
		this.bindingResolver = bindingResolver;
		this.type = type;
	}
	
	public boolean isPointer() {
		return true;
	}
	
	public ITypeBinding getKeyType() {
		return null;
	}
	
	public boolean isStaticArray() {
		return false;
	}
	
	public boolean isFunction() {
		return false;
	}
	
	public boolean isDelegate() {
		return false;
	}
	
	public ITypeBinding[] getParametersTypes() {
		return NO_TYPES;
	}
	
	public ITypeBinding getValueType() {
		return null;
	}
	
	public boolean isDynamicArray() {
		return false;
	}
	
	public ITypeBinding getReturnType() {
		return null;
	}
	
	public boolean isAssociativeArray() {
		return false;
	}

	public ITypeBinding getComponentType() {
		return bindingResolver.resolveType(type.next, null);
	}

	public int getDimension() {
		return 0;
	}

	public String getName() {
		// TODO optimize
		return getComponentType().getName() + "*";
	}

	public boolean isAssignmentCompatible(ITypeBinding variableType) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCastCompatible(ITypeBinding type) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNullType() {
		return false;
	}

	public boolean isPrimitive() {
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
