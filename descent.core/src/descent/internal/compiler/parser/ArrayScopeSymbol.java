package descent.internal.compiler.parser;

public class ArrayScopeSymbol extends ScopeDsymbol {
	
	@Override
	public ArrayScopeSymbol isArrayScopeSymbol() {
		return this;
	}
	
	@Override
	public int getNodeType() {
		return ARRAY_SCOPE_SYMBOL;
	}

}
