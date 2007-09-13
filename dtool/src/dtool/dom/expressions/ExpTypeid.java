package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeidExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
import dtool.dom.references.ReferenceConverter;

public class ExpTypeid extends Expression {

	Reference type;
	
	public ExpTypeid(TypeidExp elem) {
		convertNode(elem);
		this.type = ReferenceConverter.convertType(elem.type);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}

}
