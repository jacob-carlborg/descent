package descent.core.dom;


public class TypePointerBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final ITypeBinding type;

	public TypePointerBinding(DefaultBindingResolver bindingResolver, ITypeBinding type, String signature) {
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
		return type;
	}

	public int getDimension() {
		return 0;
	}

	public String getName() {
		return type.getName() + "*";
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

	public boolean isEqualTo(IBinding binding) {
		// TODO Auto-generated method stub
		return false;
	}

}
