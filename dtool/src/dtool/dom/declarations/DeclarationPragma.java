package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.PragmaDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.Symbol;
import dtool.dom.expressions.Expression;
import dtool.refmodel.INonScopedBlock;

public class DeclarationPragma extends ASTNeoNode implements INonScopedBlock {

	public Symbol ident;
	public Expression[] expressions;
	public ASTNode[] decls;	// can be null?

	
	public DeclarationPragma(PragmaDeclaration elem) {
		convertNode(elem);
		ident = new Symbol(elem.ident);
		this.expressions = Expression.convertMany(elem.expressions);
		this.decls = Declaration.convertMany(elem.getDeclarationDefinitions());
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, expressions);
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
	}

	public ASTNode[] getMembers() {
		return decls;
	}
	
	public Iterator<ASTNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}
}
