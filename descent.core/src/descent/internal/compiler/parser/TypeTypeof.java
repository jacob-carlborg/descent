package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TypeTypeof extends TypeQualified {

	public Expression exp, sourceExp;
	public int typeofStart;
	public int typeofLength;
	
	// Descent: to improve performance, must be set by Parser or ModuleBuilder
	public ASTNodeEncoder encoder;  

	public TypeTypeof(Loc loc, Expression exp, ASTNodeEncoder encoder) {
		super(loc, TY.Ttypeof);
		this.exp = this.sourceExp = exp;
		this.encoder = encoder;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceExp);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return TYPE_TYPEOF;
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		Type t;

		{
			sc.intypeof++;
			exp = exp.semantic(sc, context);
			sc.intypeof--;
			t = exp.type;
			if (null == t) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.ExpressionHasNoType, exp, new String[] { exp.toChars(context) }));
				}
				return tvoid;
			}
		}

		if (idents != null && idents.size() != 0) {
			Dsymbol s = t.toDsymbol(sc, context);
			for (int i = 0; i < idents.size(); i++) {
				if (null == s) {
					break;
				}
				IdentifierExp id = idents.get(i);
				s = s.searchX(loc, sc, id, context);
			}
			if (s != null) {
				t = s.getType();
				if (null == t) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolNotAType, this, new String[] { s.toChars(context) }));
					}
					return tvoid;
				}
			} else {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotResolveDotProperty, this, new String[] { toChars(context) }));
				}
				return tvoid;
			}
		}
		return t;
	}

	public void setTypeofSourceRange(int start, int length) {
		this.typeofStart = start;
		this.typeofLength = length;
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		if (exp.type != null) {
			return exp.type.size(loc, context);
		} else {
			return super.size(loc, context);
		}
	}

	@Override
	public Type syntaxCopy(SemanticContext context) {
		TypeTypeof t = new TypeTypeof(loc, exp.syntaxCopy(context), encoder);
		t.syntaxCopyHelper(this, context);
		return t;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, HdrGenState hgs, int mod, SemanticContext context) {
	    if (mod != this.mod) {
			toCBuffer3(buf, hgs, mod, context);
			return;
		}
		buf.writestring("typeof(");
		exp.toCBuffer(buf, hgs, context);
		buf.writeByte(')');
		toCBuffer2Helper(buf, hgs, context);
	}

	@Override
	public Dsymbol toDsymbol(Scope sc, SemanticContext context) {
		Type t = semantic(Loc.ZERO, sc, context);
		if (same(t, this, context)) {
			return null;
		}
		return t.toDsymbol(sc, context);
	}
	
	@Override
	public String getSignature0() {
		StringBuilder sb = new StringBuilder();
		appendSignature0(sb);
		return sb.toString();
	}
	
	@Override
	protected void appendSignature0(StringBuilder sb) {
		sb.append(ISignatureConstants.TYPEOF);
		char[] expc = encoder.encodeExpression(exp);
		sb.append(expc.length);
		sb.append(ISignatureConstants.TYPEOF);
		sb.append(expc);
	}

}
