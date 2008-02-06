package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tvoid;

// DMD 1.020
public class TypePointer extends Type {
	
	private String signature; // Descent signature

	public TypePointer(Type next) {
		super(TY.Tpointer, next);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceNext);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
		e = new NullExp(Loc.ZERO);
		e.type = this;
		return e;
	}

	@Override
	public int getNodeType() {
		return TYPE_POINTER;
	}

	@Override
	public TypeInfoDeclaration getTypeInfoDeclaration(SemanticContext context) {
		return new TypeInfoPointerDeclaration(this, context);
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		return true;
	}

	@Override
	public MATCH implicitConvTo(Type to, SemanticContext context) {
		if (same(this, to, context)) {
			return MATCHexact;
		}
		if (to.ty == Tpointer && to.next != null) {
			if (to.next.ty == Tvoid) {
				return MATCHconvert;
			}

			if (next.ty == Tfunction && to.next.ty == Tfunction) {
				TypeFunction tf;
				TypeFunction tfto;

				tf = (TypeFunction) (next);
				tfto = (TypeFunction) (to.next);
				return tfto.equals(tf) ? MATCHexact : MATCHnomatch;
			}
		}
		return MATCHnomatch;
	}

	@Override
	public boolean isscalar(SemanticContext context) {
		return true;
	}

	@Override
	public boolean isZeroInit(SemanticContext context) {
		return true;
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		Type n = next.semantic(loc, sc, context);
		switch (n.toBasetype(context).ty) {
		case Ttuple:
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CannotHavePointerToSymbol, this, new String[] { n.toChars(context) }));
			n = tint32;
			break;
		}
		if (n != next) {
			deco = null;
		}
		next = n;
		return merge(context);
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		return PTRSIZE;
	}

	@Override
	public Type syntaxCopy(SemanticContext context) {
		Type t = next.syntaxCopy(context);
		if (same(t, next, context)) {
			t = this;
		} else {
			t = new TypePointer(t);
		}
		return t;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		buf.prependstring("*");
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
		next.toCBuffer2(buf, ident, hgs, context);
	}
	
	@Override
	public String getSignature0() {
		if (signature == null) {
			StringBuilder sb = new StringBuilder();
			appendSignature(sb);
			signature = sb.toString();
		}
		return signature;
	}
	
	@Override
	protected void appendSignature0(StringBuilder sb) {
		sb.append('P');
		next.appendSignature(sb);
	}

}
