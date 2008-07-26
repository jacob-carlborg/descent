package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class FileInitExp extends DefaultInitExp {

	public FileInitExp(Loc loc) {
		super(loc, TOK.TOKfile);
	}

	@Override
	public int getNodeType() {
		return FILE_INIT_EXP;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		type = Type.tchar.invariantOf(context).arrayOf(context);
		return this;
	}

	@Override
	public Expression resolve(Loc loc, Scope sc, SemanticContext context) {
		char[] s = loc != null && loc.filename != null ? loc.filename
				: sc.module.ident.toChars().toCharArray();
		Expression e = new StringExp(loc, s);
		e = e.semantic(sc, context);
		e = e.castTo(sc, type, context);
		return e;
	}

}
