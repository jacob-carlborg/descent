package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Treference;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeReference extends Type {
	
	private SemanticContext context;

	public TypeReference(Type t, SemanticContext context) {
		super(Treference, t);
		this.context = context;
		if (t.ty == Tbit) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CannotMakeReferenceToABit, this));
			}
		}
	}

	@Override
	public Type syntaxCopy(SemanticContext context) {
		Type t = next.syntaxCopy(context);
		if (same(t, next, context)) {
			t = this;
		} else {
			t = new TypeReference(t, context);
			t.copySourceRange(this);
		}
		return t;
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		return PTRSIZE;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, HdrGenState hgs, int mod, SemanticContext context) {
	    if (mod != this.mod) {
			toCBuffer3(buf, hgs, mod, context);
			return;
		}
		next.toCBuffer2(buf, hgs, this.mod, context);
		buf.writeByte('&');
	}
	
	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident, SemanticContext context) {
		// References just forward things along
	    return next.dotExp(sc, e, ident, context);
	}
	
	@Override
	public Expression defaultInit(Loc loc, SemanticContext context) {
		Expression e;
	    e = new NullExp(loc);
	    e.type = this;
	    return e;
	}
	
	@Override
	public boolean isZeroInit(SemanticContext context) {
		return true;
	}

	@Override
	public int getNodeType() {
		return TYPE_REFERENCE;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
	}
	
	@Override
	public String getSignature0() {
		// TODO Descent signature
		return null;
	}
	
	@Override
	protected void appendSignature0(StringBuilder sb) {
		// TODO Descent signature		
	}

}
