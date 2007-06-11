package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.core.dom.IStatement;
import descent.internal.core.dom.CompoundStatement;
import descent.internal.core.dom.Condition;
import descent.internal.core.dom.ConditionalDeclaration;
import descent.internal.core.dom.ConditionalStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationConditional extends ASTNeoNode implements IStatement {
	
	public Condition condition; // TODO convert Condition
	public ASTNode[] thendecls;
	public ASTNode[] elsedecls;

	public DeclarationConditional(ConditionalDeclaration elem) {
		setSourceRange(elem);
		this.condition = elem.condition;
		thendecls = Declaration.convertMany(elem.a);
		elsedecls = Declaration.convertMany(elem.aelse);
	}
	
	public DeclarationConditional(ConditionalStatement elem) {
		setSourceRange(elem);
		this.condition = elem.condition;
		CompoundStatement cpst;
		cpst = (CompoundStatement) elem.ifbody;
		thendecls = DescentASTConverter.convertMany(cpst.as.toArray(), thendecls);
		cpst = (CompoundStatement) elem.elsebody;
		elsedecls = DescentASTConverter.convertMany(cpst.as.toArray(), elsedecls);
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
