package descent.core.dom;


public class TypeSArrayBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final ITypeBinding type;
	private final int dimension;

	public TypeSArrayBinding(DefaultBindingResolver bindingResolver, ITypeBinding type, int dimension, String signature) {
		super(signature);
		this.bindingResolver = bindingResolver;
		this.type = type;
		this.dimension = dimension;
	}
	
	public ITypeBinding getComponentType() {
		return type;
	}
	
	public int getDimension() {
		return dimension;
	}
	
	public ITypeBinding getKeyType() {
		return null;
	}
	
	public String getName() {
		return type.getName() + "[" + dimension + "]";
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

	public boolean isEqualTo(IBinding binding) {
		// TODO Auto-generated method stub
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

}
