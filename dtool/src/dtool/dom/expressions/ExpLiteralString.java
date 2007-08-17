package dtool.dom.expressions;

import descent.internal.compiler.parser.MultiStringExp;
import descent.internal.compiler.parser.StringExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralString extends Expression {

	String[] strings;
	
	public ExpLiteralString(StringExp elem) {
		convertNode(elem);
		this.strings = new String[] { elem.string };
	}
	
	public ExpLiteralString(MultiStringExp elem) {
		convertNode(elem);
		this.strings = elem.strings.toArray(new String[elem.length]);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}

}
