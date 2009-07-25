package descent.core.dom;

import descent.internal.compiler.parser.TypeSArray;

public class TypeSArrayBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final TypeSArray type;

	public TypeSArrayBinding(DefaultBindingResolver bindingResolver, TypeSArray type, String signature) {
		super(signature);
		this.bindingResolver = bindingResolver;
		this.type = type;
	}
	
	public ITypeBinding getComponentType() {
		return bindingResolver.resolveType(type.next, null);
	}
	
	public int getDimension() {
		descent.internal.compiler.parser.Expression exp = type.dim;
		return exp.toInteger(bindingResolver.context).intValue();
	}
	
	public ITypeBinding getKeyType() {
		return null;
	}
	
	public String getName() {
		// TODO optimize
		return getKeyType().getName() + "[" + getDimension() + "]";
	}
	
	public ITypeBinding[] getParametersTypes() {
		return NO_TYPES;
	}
	
	public ITypeBinding getReturnType() {
		return null;
	}
	
	public ITypeBinding getValueType() {
		return null;
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
		return true;
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
