package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TOK.TOKeof;
import static descent.internal.compiler.parser.TOK.TOKstring;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class CompileExp extends UnaExp {

	public CompileExp(Loc loc, Expression e) {
		super(loc, TOK.TOKmixin, e);
	}

	@Override
	public int getNodeType() {
		return COMPILE_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);
		e1 = e1.optimize(WANTvalue | WANTinterpret);
		if (e1.op != TOKstring) {
			error("argument to mixin must be a string, not (%s)", e1.toChars());
			return this;
		}
		StringExp se = (StringExp) e1;
		se = se.toUTF8(sc);
		Parser p = new Parser(context.apiLevel, se.string);
		p.loc = loc;
		Expression e = p.parseExpression();
		if (p.token.value != TOKeof) {
			error("incomplete mixin expression (%s)", se.toChars());
		}
		return e.semantic(sc, context);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("mixin(");
	    expToCBuffer(buf, hgs, e1, PREC.PREC_assign, context);
	    buf.writeByte(')');
	}

}
