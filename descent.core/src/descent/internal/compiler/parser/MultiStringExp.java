package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class MultiStringExp extends Expression {
	
	public List<StringExp> strings = new ArrayList<StringExp>(1);
	
	public MultiStringExp() {
		super(TOK.TOKstring);
	}
	
	@Override
	public int kind() {
		return MULTI_STRING_EXP;
	}

}
