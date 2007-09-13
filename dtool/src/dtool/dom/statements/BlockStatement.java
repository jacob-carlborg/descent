package dtool.dom.statements;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ScopeStatement;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.descentadapter.DescentASTConverter;
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
	public BlockStatement(descent.internal.compiler.parser.CompoundStatement elem) {
		convertNode(elem);
		this.statements = DescentASTConverter.convertManyL(elem.statements, statements);
		
		for(@SuppressWarnings("unused")	IStatement decl : statements) {
			// just check class cast
		}
	}

	public BlockStatement(ScopeStatement elem) {
		convertNode(elem);
		if(elem.statement instanceof descent.internal.compiler.parser.CompoundStatement) {
			descent.internal.compiler.parser.CompoundStatement compoundSt = 
				(descent.internal.compiler.parser.CompoundStatement) elem.statement;
			this.statements = DescentASTConverter.convertManyL(compoundSt.statements, statements);
			this.hasCurlyBraces = true;
		} else {
			this.statements = DescentASTConverter.convertManyL(
					new ASTNode[] {elem.statement}, statements);
			setSourceRange(elem.statement);
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
	
	@Override
	public IScope getAdaptedScope() {
		return this;
	}

}
