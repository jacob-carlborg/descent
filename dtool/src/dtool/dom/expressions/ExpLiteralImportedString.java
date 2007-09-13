package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.FileExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralImportedString extends Expression {
	
	final public Resolvable exp; 

	public ExpLiteralImportedString(FileExp node) {
		convertNode(node);
		this.exp = Expression.convert(node.e1); 
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);	 
	}

}
