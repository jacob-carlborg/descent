package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.CastExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
import dtool.dom.references.ReferenceConverter;

public class ExpCast extends Expression {
	
	Resolvable exp;
	Reference type;

	public ExpCast(CastExp elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.e1); 
		this.type = ReferenceConverter.convertType(elem.type);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}

}
