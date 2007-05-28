package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.Condition;
import descent.internal.core.dom.ConditionalDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationConditional extends ASTNeoNode {
	
	public Condition condition; // TODO convert Condition
	public Declaration[] thendecls;
	public Declaration[] elsedecls;

	public DeclarationConditional(ConditionalDeclaration elem) {
		setSourceRange(elem);
		this.condition = elem.condition;
		thendecls = Declaration.convertMany(elem.a);
		elsedecls = Declaration.convertMany(elem.aelse);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, thendecls);
			TreeVisitor.acceptChildren(visitor, elsedecls);
		}
		visitor.endVisit(this);
	}

}
