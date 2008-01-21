package descent.core.dom;

import descent.internal.compiler.parser.Id;

public class BuiltinProperties {
	
	public final static char[][] allTypesProperties = { Id.init, Id.__sizeof, Id.alignof, Id.mangleof, Id.stringof };
	public final static char[][] integralTypesProperties = { Id.max, Id.min };
	public final static char[][] floatingPointTypesProperties = { Id.infinity, Id.nan, Id.dig, Id.epsilon, Id.mant_dig, Id.max_10_exp, Id.max_exp, Id.min_10_exp, Id.min_exp, Id.max, Id.min };
	public final static char[][] staticAndDynamicArrayProperties = { Id.dup, Id.sort, Id.length, Id.ptr, Id.reverse };
	public final static char[][] associativeArrayProperties = { Id.length, Id.keys, Id.values, Id.rehash };
	
}
