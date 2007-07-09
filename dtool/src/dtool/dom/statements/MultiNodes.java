package dtool.dom.statements;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.core.dom.IDeclaration;
import descent.core.dom.IDescentElement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;

/**
 * A compound statement which does not introduces a new Scope.
 */
public class MultiNodes extends ASTNeoNode {
	
	public ASTNode[] decls;


	public MultiNodes(IDescentElement elem, IDeclaration[] olddecls) {
		convertNode((ASTNode)elem);
		this.decls = DescentASTConverter.convertMany(olddecls, new ASTNode[olddecls.length]); 
	}
	
	public MultiNodes(IDescentElement elem, List<? extends IDescentElement> olddecls) {
		convertNode((ASTNode)elem);
		this.decls = DescentASTConverter.convertMany(olddecls); 
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
	}

}
