package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKdelegate;

// DMD 1.020
public class FuncLiteralDeclaration extends FuncDeclaration {

	private final static char[] foreachBody = { '_', '_', 'f', 'o', 'r', 'e',
			'a', 'c', 'h', 'b', 'o', 'd', 'y' };
	private final static char[] dgliteral = { '_', '_', 'd', 'g', 'l', 'i',
			't', 'e', 'r', 'a', 'l' };
	private final static char[] funcliteral = { '_', '_', 'f', 'u', 'n', 'c',
			'l', 'i', 't', 'e', 'r', 'a', 'l' };
	private final static IdentifierExp idfunc = new IdentifierExp(new char[] {
			'f', 'u', 'n', 'c', 't', 'i', 'o', 'n' });
	private final static IdentifierExp iddel = new IdentifierExp(new char[] {
			'd', 'e', 'l', 'e', 'g', 'a', 't', 'e' });

	public TOK tok; // TOKfunction or TOKdelegate

	public FuncLiteralDeclaration(Loc loc, Type type, TOK tok,
			ForeachStatement fes) {
		super(loc, null, STC.STCundefined, type);
		char[] id;

		if (fes != null) {
			id = foreachBody;
		} else if (tok == TOKdelegate) {
			id = dgliteral;
		} else {
			id = funcliteral;
		}
		this.ident = new IdentifierExp(IdentifierExp.generateId(id));
		this.tok = tok;
		this.fes = fes;
	}

	@Override
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
	public int getNodeType() {
		return FUNC_LITERAL_DECLARATION;
	}

	@Override
	public FuncLiteralDeclaration isFuncLiteralDeclaration() {
		return this;
	}

	@Override
	public boolean isNested() {
		return (tok == TOK.TOKdelegate);
	}

	@Override
	public String kind() {
		return (tok == TOKdelegate) ? "delegate" : "function";
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		FuncLiteralDeclaration f;

		if (s != null) {
			f = (FuncLiteralDeclaration) s;
		} else {
			f = new FuncLiteralDeclaration(loc, type.syntaxCopy(), tok, fes);
		}
		super.syntaxCopy(f);
		return f;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		type.toCBuffer(buf, ((tok == TOKdelegate) ? iddel : idfunc), hgs,
				context);
		bodyToCBuffer(buf, hgs, context);
	}

}
