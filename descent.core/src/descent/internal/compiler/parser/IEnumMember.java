package descent.internal.compiler.parser;

public interface IEnumMember extends IDsymbol {
	
	Expression value();
	
	void value(Expression value);

}
