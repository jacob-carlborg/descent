package dtool.ast.declarations;


import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.PragmaDeclaration;
import descent.internal.compiler.parser.PragmaStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;

public class DeclarationPragma extends DeclarationAttrib implements IStatement {

	public Symbol ident;
	public Resolvable[] expressions;
	
	public DeclarationPragma(PragmaDeclaration elem) {
		super(elem, elem.decl);
		this.ident = new Symbol(elem.ident);
		if(elem.args != null)
			this.expressions = Expression.convertMany(elem.args);
	}
	
	public DeclarationPragma(PragmaStatement elem) {
		super(elem, elem.body);
		this.ident = new Symbol(elem.ident);
		if(elem.args != null)
			this.expressions = Expression.convertMany(elem.args);
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, expressions);
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(body));
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[pragma("+ident+",...)]";
	}
}
