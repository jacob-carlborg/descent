package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;

import static descent.internal.compiler.parser.TOK.TOKstring;

import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tchar;
import static descent.internal.compiler.parser.TY.Tdchar;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tvoid;
import static descent.internal.compiler.parser.TY.Twchar;

// DMD 1.020
public class TypeDArray extends TypeArray {

	public TypeDArray(Type next) {
		super(TY.Tarray, next);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, next);
		}
		visitor.endVisit(this);
	}

	@Override
	public int alignsize(SemanticContext context) {
		return PTRSIZE;
	}

	@Override
	public boolean builtinTypeInfo() {
		return next.isTypeBasic() != null;
	}

	@Override
	public boolean checkBoolean(SemanticContext context) {
		return true;
	}

	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
		e = new NullExp(Loc.ZERO);
		e.type = this;
		return e;
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		if (CharOperation.equals(ident.ident, Id.length)) {
			if (e.op == TOKstring) {
				StringExp se = (StringExp) e;

				return new IntegerExp(se.loc, se.len, Type.tindex);
			}
			e = new ArrayLengthExp(e.loc, e);
			e.type = Type.tsize_t;
			return e;
		} else if (CharOperation.equals(ident.ident, Id.ptr)) {
			e = e.castTo(sc, next.pointerTo(context), context);
			return e;
		} else {
			e = super.dotExp(sc, e, ident, context);
		}
		return e;
	}

	@Override
	public int getNodeType() {
		return TYPE_D_ARRAY;
	}

	@Override
	public TypeInfoDeclaration getTypeInfoDeclaration(SemanticContext context) {
		return new TypeInfoArrayDeclaration(this, context);
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		return true;
	}

	@Override
	public MATCH implicitConvTo(Type to, SemanticContext context) {
		// Allow implicit conversion of array to pointer
		if (context.global.params.useDeprecated
				&& to.ty == Tpointer
				&& (to.next.ty == Tvoid || next.equals(to.next) /*|| to.next.isBaseOf(next)*/)) {
			return MATCHconvert;
		}

		if (to.ty == Tarray) {
			int[] offset = { 0 };

			if ((to.next.isBaseOf(next, offset, context) && offset[0] == 0)
					|| to.next.ty == Tvoid) {
				return MATCHconvert;
			}
		}
		return super.implicitConvTo(to, context);
	}

	@Override
	public boolean isString(SemanticContext context) {
		TY nty = next.toBasetype(context).ty;
		return nty == Tchar || nty == Twchar || nty == Tdchar;
	}

	@Override
	public boolean isZeroInit(SemanticContext context) {
		return true;
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		Type tn = next;

		tn = next.semantic(loc, sc, context);
		Type tbn = tn.toBasetype(context);
		switch (tbn.ty) {
		case Tfunction:
		case Tnone:
		case Ttuple:
			error(loc, "can't have array of %s", tbn.toChars(context));
			tn = next = tint32;
			break;
		}
		if (tn.isauto()) {
			error(loc, "cannot have array of auto %s", tn.toChars(context));
		}
		if (next != tn) {
			//deco = NULL;			// redo
			return tn.arrayOf(context);
		}
		return merge(context);
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		return PTRSIZE * 2;
	}

	@Override
	public Type syntaxCopy() {
		Type t = next.syntaxCopy();
		if (t == next) {
			t = this;
		} else {
			t = new TypeDArray(t);
		}
		return t;
	}

	@Override
	public void toDecoBuffer(OutBuffer buf, SemanticContext context) {
		buf.writeByte(ty.mangleChar);
		if (next != null) {
			next.toDecoBuffer(buf, context);
		}
	}

	@Override
	public void toPrettyBracket(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("[]");
	}

	@Override
	public void toTypeInfoBuffer(OutBuffer buf, SemanticContext context) {
		buf.writeByte(ty.mangleChar);
		if (next != null) {
			next.toTypeInfoBuffer(buf, context);
		}
	}

}
