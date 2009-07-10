package descent.core.dom;

import descent.internal.compiler.parser.TypeBasic;

public class TypeBasicBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	final TypeBasic type;
	
	public TypeBasicBinding(DefaultBindingResolver bindingResolver, TypeBasic type) {
		super(type.deco);
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
		return null;
	}
	
	public String getName() {
		return type.ty.name;
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
		return true;
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
