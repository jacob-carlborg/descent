package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TOK.TOKeof;
import static descent.internal.compiler.parser.TOK.TOKstring;

public class CompileDeclaration extends AttribDeclaration {

	public Expression exp;
	public ScopeDsymbol sd;

	public CompileDeclaration(Expression exp) {
		super(null);
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

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		exp = exp.semantic(sc, context);
		exp = resolveProperties(sc, exp, context);
		exp = exp.optimize(WANTvalue | WANTinterpret);
		if (exp.op != TOKstring) {
			error("argument to mixin must be a string, not (%s)", exp.toChars());
			return;
		}
		StringExp se = (StringExp) exp;
		se = se.toUTF8(sc);
		Parser p = new Parser(context.ast, se.string);
		decl = p.parseDeclDefs(false);
		if (p.token.value != TOKeof) {
			error("incomplete mixin declaration (%s)", se.toChars());
		}

		super.addMember(sc, sd, 0, context);
		super.semantic(sc, context);
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		CompileDeclaration sc = new CompileDeclaration(exp.syntaxCopy());
		return sc;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs) {
		buf.writestring("mixin(");
		exp.toCBuffer(buf, hgs);
		buf.writestring(");");
		buf.writenl();
	}

}
