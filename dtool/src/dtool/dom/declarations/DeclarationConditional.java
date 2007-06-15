package dtool.dom.declarations;

import util.ArrayUtil;
import util.tree.TreeVisitor;
import descent.core.dom.IConditionalDeclaration;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IIftypeDeclaration;
import descent.core.dom.IVersionDeclaration;
import descent.internal.core.dom.CompoundStatement;
import descent.internal.core.dom.Condition;
import descent.internal.core.dom.ConditionalDeclaration;
import descent.internal.core.dom.ConditionalStatement;
import descent.internal.core.dom.StaticIfDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.statements.IStatement;
import dtool.model.IDefinitionContainer;

public class DeclarationConditional extends ASTNeoNode implements IStatement, IDefinitionContainer {
	
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
		thendecls = DescentASTConverter.convertMany(cpst.as.toArray());
		cpst = (CompoundStatement) elem.elsebody;
		elsedecls = DescentASTConverter.convertMany(cpst.as.toArray());
	}

	public DeclarationConditional(IDebugDeclaration elem) {
		convertConditional(elem, Condition.DEBUG);
	}

	public DeclarationConditional(IVersionDeclaration elem) {
		convertConditional(elem, Condition.VERSION);
	}

	public DeclarationConditional(StaticIfDeclaration elem) {
		convertConditional(elem, Condition.STATIC_IF);
	}
	
	public DeclarationConditional(IIftypeDeclaration elem) {
		// TODO: here be a definition?
		convertConditional(elem, Condition.IFTYPE);
	}
	
	
	private void convertConditional(IConditionalDeclaration elem, int debug) {
		setSourceRange((ASTNode)elem);
		thendecls = DescentASTConverter.convertMany(
				elem.getIfTrueDeclarationDefinitions());
		elsedecls = DescentASTConverter.convertMany(
				elem.getIfFalseDeclarationDefinitions());
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

	public ASTNode[] getMembers() {
		if(thendecls == null)
			return elsedecls;
		if(elsedecls == null)
			return thendecls;
		
		return ArrayUtil.concat(thendecls, elsedecls);
	}

}
