package dtool.ast.expressions;

import descent.internal.compiler.parser.MultiStringExp;
import descent.internal.compiler.parser.StringExp;
import dtool.ast.IASTNeoVisitor;

public class ExpLiteralString extends Expression {

	char[][] strings;
	
	public ExpLiteralString(StringExp elem) {
		convertNode(elem);
		this.strings = new char[][] { elem.string };
	}
	
	public ExpLiteralString(MultiStringExp elem) {
		convertNode(elem);
		this.strings = new char[elem.strings.size()][];
		for (int i = 0; i < elem.strings.size(); i++) {
			this.strings[i] = elem.strings.get(i).string;
		}
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}

}
