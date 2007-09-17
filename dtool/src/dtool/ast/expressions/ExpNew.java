package dtool.ast.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.NewExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;

public class ExpNew extends Expression {

	Resolvable[] allocargs;
	Reference type;
	Resolvable[] args;

	public ExpNew(NewExp elem) {
		convertNode(elem);
		if(elem.newargs != null)
			this.allocargs = Expression.convertMany(elem.newargs); 
		this.type = ReferenceConverter.convertType(elem.type);
		if(elem.arguments != null)
			this.args = Expression.convertMany(elem.arguments); 
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, allocargs);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}

}
