package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.FuncExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.Definition;
import dtool.dom.declarations.DefinitionFunction;

public class ExpLiteralFunc extends Expression {
	
	DefinitionFunction func;

	public ExpLiteralFunc(FuncExp elem) {
		convertNode(elem);
		this.func = (DefinitionFunction) Definition.convert(elem.fd);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, func);
		}
		visitor.endVisit(this);	 
	}

}
