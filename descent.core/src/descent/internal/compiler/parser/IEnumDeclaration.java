package descent.internal.compiler.parser;

public interface IEnumDeclaration extends IScopeDsymbol {
	
	Type memtype();
	
	integer_t defaultval();
	
	integer_t minval();
	
	integer_t maxval();

}
