package descent.core.dom;

import descent.internal.compiler.parser.TypeFunction;

public class TypeFunctionOrDelegateBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final TypeFunction type;
	private final boolean isFunction;

	public TypeFunctionOrDelegateBinding(DefaultBindingResolver bindingResolver, TypeFunction type, boolean isFunction, String signature) {
		super(signature);
		this.bindingResolver = bindingResolver;
		this.type = type;
		this.isFunction = isFunction;
	}
	
	public ITypeBinding getComponentType() {
		return null;
	}
	
	public int getDimension() {
		return 0;
	}
	
	public ITypeBinding getKeyType() {
		return null;
	}
	
	public String getName() {
		// TODO implement
		return "";
	}
	
	public IBinding[] getParametersTypes() {
		IBinding[] params = new IBinding[type.parameters.size()];
		for (int i = 0; i < params.length; i++) {
			params[i] = bindingResolver.resolveType(type.parameters.get(i).type);
		}
		return params;
	}
	
	public IBinding getReturnType() {
		return bindingResolver.resolveType(type.next);
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
		return !isFunction;
	}

	public boolean isDynamicArray() {
		return false;
	}

	public boolean isEqualTo(IBinding binding) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFunction() {
		return isFunction;
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
