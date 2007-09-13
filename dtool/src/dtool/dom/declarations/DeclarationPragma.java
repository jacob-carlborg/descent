package dtool.dom.declarations;


import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.PragmaDeclaration;
import descent.internal.compiler.parser.PragmaStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.Symbol;
import dtool.dom.expressions.Expression;
import dtool.dom.expressions.Resolvable;
import dtool.dom.statements.IStatement;

public class DeclarationPragma extends DeclarationAttrib implements IStatement {

	public Symbol ident;
	public Resolvable[] expressions;
	
	public DeclarationPragma(PragmaDeclaration elem) {
		super(elem, elem.decl);
		this.ident = new Symbol(elem.ident);
		this.expressions = Expression.convertMany(elem.args);
	}
	
	public DeclarationPragma(PragmaStatement elem) {
		super(elem, elem.body);
		this.ident = new Symbol(elem.ident);
		this.expressions = Expression.convertMany(elem.args);
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, expressions);
			TreeVisitor.acceptChildren(visitor, body.nodes);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[pragma("+ident+",...)]";
	}
}
