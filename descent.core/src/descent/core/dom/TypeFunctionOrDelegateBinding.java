package descent.core.dom;

import descent.internal.compiler.parser.LINK;

public class TypeFunctionOrDelegateBinding extends PrimitiveTypeBinding implements ITypeBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final ITypeBinding[] args;
	private final ITypeBinding retType;
	private final boolean varargs;
	private final LINK linkage;
	public boolean isFunction;

	public TypeFunctionOrDelegateBinding(DefaultBindingResolver bindingResolver, ITypeBinding[] args, ITypeBinding retType, boolean varargs, LINK linkage, String signature, boolean isFunction) {
		super(signature);
		this.bindingResolver = bindingResolver;
		this.args = args;
		this.retType = retType;
		this.varargs = varargs;
		this.linkage = linkage;
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
		StringBuilder sb = new StringBuilder();
		sb.append(retType.getName());
		sb.append(" ");
		if (isFunction) {
			sb.append("function");
		} else {
			sb.append("delegate");
		}
		sb.append("(");
		for(int i = 0; i < args.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(args[i].getName());
		}
		sb.append(")");
		return sb.toString();
	}
	
	public ITypeBinding[] getParametersTypes() {
		return args;
	}
	
	public ITypeBinding getReturnType() {
		return retType;
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

}
