package dtool.dom.statements;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.ScopeStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends Statement implements IScopeNode {
	
	public List<IStatement> statements;
	public boolean hasCurlyBraces; // syntax-structural?

	@SuppressWarnings("unchecked")
	public BlockStatement(descent.internal.core.dom.CompoundStatement elem) {
		convertNode(elem);
		this.statements = DescentASTConverter.convertManyL(elem.as, statements);
		
		for(@SuppressWarnings("unused")	IStatement decl : statements) {
			// just check class cast
		}
	}

	public BlockStatement(ScopeStatement elem) {
		convertNode(elem);
		if(elem.s instanceof descent.internal.core.dom.CompoundStatement) {
			descent.internal.core.dom.CompoundStatement compoundSt = 
				(descent.internal.core.dom.CompoundStatement) elem.s;
			this.statements = DescentASTConverter.convertManyL(compoundSt.as, statements);
			this.hasCurlyBraces = true;
		} else {
			this.statements = DescentASTConverter.convertManyL(
					new ASTNode[] {elem.s}, statements);
			setSourceRange(elem.s);
		}
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statements);
		}
		visitor.endVisit(this);
	}


	@SuppressWarnings("unchecked")
	public Iterator<ASTNode> getMembersIterator() {
		return (Iterator) statements.iterator();
	}

	public List<IScope> getSuperScopes() {
		return null;
	}

}
