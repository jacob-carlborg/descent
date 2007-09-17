package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class FuncLiteralDeclaration extends FuncDeclaration {
	
	public TOK tok; // TOKfunction or TOKdelegate
	
	public FuncLiteralDeclaration(Loc loc, Type type, TOK tok, ForeachStatement fes) {
		super(loc, null, STC.STCundefined, type);
		this.tok = tok;
		// TODO semantic
	}

	@Override
	public int getNodeType() {
		return FUNC_LITERAL_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public FuncLiteralDeclaration isFuncLiteralDeclaration() {
		return this;
	}
	
	@Override
	public boolean isNested() {
		return (tok == TOK.TOKdelegate);
	}
	

}
