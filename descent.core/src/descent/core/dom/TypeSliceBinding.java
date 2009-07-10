package descent.core.dom;

import descent.internal.compiler.parser.TypeSlice;

public class TypeSliceBinding extends PrimitiveTypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final TypeSlice type;

	public TypeSliceBinding(DefaultBindingResolver bindingResolver, TypeSlice type, String signature) {
		super(signature);
		this.bindingResolver = bindingResolver;
		this.type = type;
	}

	public ITypeBinding getComponentType() {
		return bindingResolver.resolveType(type.next);
	}

	public int getDimension() {
		return 0;
	}

	public ITypeBinding getKeyType() {
		return null;
	}
	
	public int getLowerBound() {
		descent.internal.compiler.parser.Expression exp = type.lwr;
		return exp.toInteger(bindingResolver.context).intValue();
	}
	
	public int getUpperBound() {
		descent.internal.compiler.parser.Expression exp = type.upr;
		return exp.toInteger(bindingResolver.context).intValue();
	}

	public String getName() {
		// TODO optimize
		return getComponentType() + "[" + getLowerBound() + " .. " + getUpperBound() + "]";
	}

	public ITypeBinding[] getParametersTypes() {
		return null;
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
		return false;
	}
	
	public boolean isSlice() {
		return true;
	}

	public boolean isSubTypeCompatible(ITypeBinding type) {
		// TODO Auto-generated method stub
		return false;
	}

}
