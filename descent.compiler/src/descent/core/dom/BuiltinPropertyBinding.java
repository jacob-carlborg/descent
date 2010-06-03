package descent.core.dom;

import descent.core.IEvaluationResult;
import descent.core.IJavaElement;

public class BuiltinPropertyBinding implements IVariableBinding {
	
	private final DefaultBindingResolver bindingResolver;
	private final descent.internal.compiler.parser.Type type;
	private final String signature;
	private final String property;
	private final descent.internal.compiler.parser.Type parentType;

	public BuiltinPropertyBinding(DefaultBindingResolver bindingResolver, descent.internal.compiler.parser.Type type, descent.internal.compiler.parser.Type parentBinding, String property, String signature) {
		this.bindingResolver = bindingResolver;
		this.type = type;
		this.parentType = parentBinding;
		this.property = property;
		this.signature = signature;
	}

	public IEvaluationResult getConstantValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public IBinding getDeclaringSymbol() {
		return (IBinding) bindingResolver.resolveType(parentType, null);
	}

	public String getName() {
		return property;
	}

	public ITypeBinding getType() {
		return bindingResolver.resolveType(type, null);
	}

	public IVariableBinding getVariableDeclaration() {
		return this;
	}

	public int getVariableId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isAlias() {
		return false;
	}

	public boolean isEnumConstant() {
		return false;
	}

	public boolean isLocal() {
		return false;
	}

	public boolean isParameter() {
		return false;
	}

	public boolean isTypedef() {
		return false;
	}

	public boolean isVariable() {
		return true;
	}

	public IJavaElement getJavaElement() {
		return null;
	}

	public String getKey() {
		return signature;
	}

	public int getKind() {
		return VARIABLE;
	}

	public long getModifiers() {
		return 0;
	}

	public boolean isDeprecated() {
		return false;
	}

	public boolean isEqualTo(IBinding binding) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IBinding)) {
			return false;
		}
			
		IBinding other = (IBinding) obj;
		return getKey().equals(other.getKey());
	}

}
