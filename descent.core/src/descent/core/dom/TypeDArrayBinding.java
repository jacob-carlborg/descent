package descent.core.dom;

public class TypeDArrayBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final ITypeBinding type;

	public TypeDArrayBinding(DefaultBindingResolver bindingResolver, ITypeBinding type, String signature) {
		super(signature);
		this.bindingResolver = bindingResolver;
		this.type = type;
	}
	
	public ITypeBinding getComponentType() {
		return type;
	}
	
	public int getDimension() {
		return 0;
	}
	
	public ITypeBinding getKeyType() {
		return null;
	}
	
	public String getName() {
		return type.getName() + "[]";
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
		return true;
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
