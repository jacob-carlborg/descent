package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class CompileStatement extends Statement {
	
	public Expression exp;

	public CompileStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = exp;	
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}

	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		return super.semantic(sc, context);
		/* TODO semantic
		exp = exp.semantic(sc, context);
	    exp = Expression.resolveProperties(sc, exp, context);
	    exp = exp.optimize(Expression.WANTvalue | Expression.WANTinterpret);
	    if (exp.op != TOK.TOKstring)
	    {	error("argument to mixin must be a string, not (%s)", exp.toChars());
		return this;
	    }
	    StringExp se = (StringExp) exp;
	    se = se.toUTF8(sc);
	    Parser p(sc.module, (unsigned char *)se.string, se.len, 0);
	    p.loc = loc;

	    Statements *statements = new Statements();
	    while (p.token.value != TOKeof)
	    {
		Statement *s = p.parseStatement(PSsemi | PScurlyscope);
		statements.push(s);
	    }

	    Statement *s = new CompoundStatement(loc, statements);
	    return s.semantic(sc);
	    */
	}

	@Override
	public int getNodeType() {
		return COMPILE_STATEMENT;
	}

}
