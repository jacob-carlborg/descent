package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeCallExp extends CallExp {
	
	private Scope fScope;
	
	public CompileTimeCallExp(Loc loc, Expression e, Expression earg1,
			Expression earg2) {
		super(loc, e, earg1, earg2);
	}

	public CompileTimeCallExp(Loc loc, Expression e, Expression earg1) {
		super(loc, e, earg1);
	}

	public CompileTimeCallExp(Loc loc, Expression e, Expressions exps) {
		super(loc, e, exps);
	}

	public CompileTimeCallExp(Loc loc, Expression e) {
		super(loc, e);	
	}
	
	@Override
	public Expression optimize(int result, SemanticContext context) {
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, fScope);
			
			return super.optimize(result, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, fScope);
		}
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		this.fScope = sc;
		
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, sc);
			
			return super.semantic(sc, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, sc);
		}
	}

}
