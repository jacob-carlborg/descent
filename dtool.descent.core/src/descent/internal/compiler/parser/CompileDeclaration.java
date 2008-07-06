package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TOK.TOKeof;
import static descent.internal.compiler.parser.TOK.TOKstring;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class CompileDeclaration extends AttribDeclaration {

	public Expression exp;
	public ScopeDsymbol sd;

	public CompileDeclaration(Loc loc, Expression exp) {
		super(loc, null);
		this.exp = exp;
	}

	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum,
			SemanticContext context) {
		this.sd = sd;
		return memnum;
	}

	@Override
	public int getNodeType() {
		return COMPILE_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, decl);
		}
		visitor.endVisit(this);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		exp = exp.semantic(sc, context);
		exp = resolveProperties(sc, exp, context);
		exp = exp.optimize(WANTvalue | WANTinterpret, context);
		if (exp.op != TOKstring) {
			error("argument to mixin must be a string, not (%s)", exp.toChars(context));
			return;
		}
		StringExp se = (StringExp) exp;
		se = se.toUTF8(sc);
		Parser p = new Parser(context.apiLevel, se.string);
		// p.nextToken(); // shouldn't be called, since it's called in Parser (in Descent, not in DMD)
		p.loc = loc;
		decl = p.parseDeclDefs(false);
		if (p.token.value != TOKeof) {
			error("incomplete mixin declaration (%s)", se.toChars(context));
		}

		super.addMember(sc, sd, 0, context);
		super.semantic(sc, context);
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		CompileDeclaration sc = new CompileDeclaration(loc, exp.syntaxCopy());
		return sc;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("mixin(");
		exp.toCBuffer(buf, hgs, context);
		buf.writestring(");");
		buf.writenl();
	}

}
