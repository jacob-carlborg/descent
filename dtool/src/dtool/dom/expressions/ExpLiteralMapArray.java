package dtool.dom.expressions;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.AssocArrayLiteralExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralMapArray extends Expression {

	public final Expression[] keys;
	public final Expression[] values;
	
	public ExpLiteralMapArray(AssocArrayLiteralExp node) {
		convertNode(node);
		Assert.isTrue(node.keys.size() == node.values.size());
		this.keys = new Expression[node.keys.size()];
		DescentASTConverter.convertMany(node.keys, this.keys);
		this.values = new Expression[node.values.size()];
		DescentASTConverter.convertMany(node.keys, this.keys);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keys);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);	 
	}

}
