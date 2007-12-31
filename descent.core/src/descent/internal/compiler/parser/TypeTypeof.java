package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TypeTypeof extends TypeQualified {

	public Expression exp, sourceExp;
	public int typeofStart;
	public int typeofLength;

	public TypeTypeof(Loc loc, Expression exp) {
		super(loc, TY.Ttypeof);
		this.exp = this.sourceExp = exp;
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
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.ExpressionHasNoType, exp, new String[] { exp.toChars(context) }));
				return tvoid;
			}
		}

		if (idents != null && idents.size() != 0) {
			IDsymbol s = t.toDsymbol(sc, context);
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
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolNotAType, this, new String[] { s.toChars(context) }));
					return tvoid;
				}
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CannotResolveDotProperty, this, new String[] { toChars(context) }));
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
		TypeTypeof t = new TypeTypeof(loc, exp.syntaxCopy(context));
		t.syntaxCopyHelper(this, context);
		return t;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer tmp = new OutBuffer();

		tmp.writestring("typeof(");
		exp.toCBuffer(tmp, hgs, context);
		tmp.writeByte(')');
		toCBuffer2Helper(tmp, null, hgs, context);
		buf.prependstring(tmp.toChars());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
	}

	@Override
	public IDsymbol toDsymbol(Scope sc, SemanticContext context) {
		Type t = semantic(Loc.ZERO, sc, context);
		if (same(t, this, context)) {
			return null;
		}
		return t.toDsymbol(sc, context);
	}
	
	@Override
	public String getSignature() {
		StringBuilder sb = new StringBuilder();
		sb.append('*');
		sb.append(ASTNodeEncoder.encodeExpression(exp));
		return sb.toString();
	}

}
