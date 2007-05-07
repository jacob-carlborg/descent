package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.DeclarationStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.Definition;

public class StatementDeclaration extends ASTNeoNode {
	
	Definition definition;

	public StatementDeclaration(DeclarationStatement elem) {
		setSourceRange(elem);
		
		this.definition = (Definition) DescentASTConverter.convertElem(elem.d);  
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, definition);
		}
		visitor.endVisit(this);	 
	}

}
