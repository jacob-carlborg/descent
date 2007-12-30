package descent.core.dom;

public class TypeAArrayBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final ITypeBinding key;
	private final ITypeBinding value;

	public TypeAArrayBinding(DefaultBindingResolver bindingResolver, ITypeBinding key, ITypeBinding value, String signature) {
		super(signature);
		this.bindingResolver = bindingResolver;
		this.key = key;
		this.value = value;
	}
	
	public ITypeBinding getComponentType() {
		return null;
	}
	
	public int getDimension() {
		return 0;
	}
	
	public ITypeBinding getKeyType() {
		return key;
	}
	
	public String getName() {
		return value.getName() + "[" + key.getName() + "]";
	}

	public ITypeBinding[] getParametersTypes() {
		return NO_TYPES;
	}
	
	public ITypeBinding getReturnType() {
		return null;
	}

	public ITypeBinding getValueType() {
		return value;
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
		return false;
	}

	public boolean isSubTypeCompatible(ITypeBinding type) {
		// TODO Auto-generated method stub
		return false;
	}

}
