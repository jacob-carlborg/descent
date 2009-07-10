package descent.core.dom;

public abstract class AbstractBinding implements IBinding {
	
	protected final static IVariableBinding[] NO_VARIABLES = new IVariableBinding[0];
	protected final static IMethodBinding[] NO_METHODS = new IMethodBinding[0];
	protected final static ITypeBinding[] NO_TYPES = new ITypeBinding[0];
	protected final static ICompilationUnitBinding[] NO_UNITS = new ICompilationUnitBinding[0];

	@Override
	public final boolean equals(Object obj) {
		return this == obj;
	}
	
	public final boolean isEqualTo(IBinding binding) {
		if (this == binding)
			return true;
		
		if (binding == null)
			return false;
		
		return getKey().equals(binding.getKey());
	}
	
}
