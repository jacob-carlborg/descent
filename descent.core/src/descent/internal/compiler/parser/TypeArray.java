package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;

import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tchar;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Twchar;

// DMD 1.020
public abstract class TypeArray extends Type {

	private final static char[][] name1 = { "_adReverseChar".toCharArray(),
			"_adReverseWchar".toCharArray() };
	private final static char[][] name2 = { "_adSortChar".toCharArray(),
			"_adSortWchar".toCharArray() };
	private final static char[][] name3 = { "_adSortBit".toCharArray(),
			"_adSort".toCharArray() };

	public TypeArray(TY ty, Type next) {
		super(ty, next);
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		Type n = this.next.toBasetype(context); // uncover any typedef's

		if (CharOperation.equals(ident.ident, Id.reverse)
				&& (n.ty == Tchar || n.ty == Twchar)) {
			Expression ec;
			FuncDeclaration fd;
			Expressions arguments;
			char[] nm;

			nm = name1[n.ty == Twchar ? 1 : 0];
			fd = context.genCfunc(Type.tindex, nm);
			ec = new VarExp(Loc.ZERO, fd);
			e = e.castTo(sc, n.arrayOf(context), context); // convert to dynamic array
			arguments = new Expressions();
			arguments.add(e);
			e = new CallExp(e.loc, ec, arguments);
			e.type = next.arrayOf(context);
		} else if (CharOperation.equals(ident.ident, Id.sort)
				&& (n.ty == Tchar || n.ty == Twchar)) {
			Expression ec;
			FuncDeclaration fd;
			Expressions arguments;
			char[] nm;

			nm = name2[n.ty == Twchar ? 1 : 0];
			fd = context.genCfunc(Type.tindex, nm);
			ec = new VarExp(Loc.ZERO, fd);
			e = e.castTo(sc, n.arrayOf(context), context); // convert to dynamic array
			arguments = new Expressions();
			arguments.add(e);
			e = new CallExp(e.loc, ec, arguments);
			e.type = next.arrayOf(context);
		} else if (CharOperation.equals(ident.ident, Id.reverse)
				|| CharOperation.equals(ident.ident, Id.dup)) {
			Expression ec;
			FuncDeclaration fd;
			Expressions arguments;
			int size = next.size(e.loc, context);
			boolean dup;

			if (size == 0) {
				throw new IllegalStateException("assert(size);");
			}

			dup = CharOperation.equals(ident.ident, Id.dup);
			fd = context.genCfunc(Type.tindex, dup ? Id.adDup : Id.adReverse);
			ec = new VarExp(Loc.ZERO, fd);
			e = e.castTo(sc, n.arrayOf(context), context); // convert to dynamic array
			arguments = new Expressions();
			if (dup) {
				arguments.add(getTypeInfo(sc, context));
			}
			arguments.add(e);
			if (!dup) {
				arguments.add(new IntegerExp(Loc.ZERO, size, Type.tint32));
			}
			e = new CallExp(e.loc, ec, arguments);
			e.type = next.arrayOf(context);
		} else if (CharOperation.equals(ident.ident, Id.sort)) {
			Expression ec;
			FuncDeclaration fd;
			Expressions arguments;

			fd = context.genCfunc(tint32.arrayOf(context),
					(n.ty == Tbit ? name3[0] : name3[1]));
			ec = new VarExp(Loc.ZERO, fd);
			e = e.castTo(sc, n.arrayOf(context), context); // convert to dynamic array
			arguments = new Expressions();
			arguments.add(e);
			if (next.ty != Tbit) {
				arguments.add(n.ty == Tsarray ? n.getTypeInfo(sc, context) // don't convert to dynamic array
						: n.getInternalTypeInfo(sc, context));
			}
			e = new CallExp(e.loc, ec, arguments);
			e.type = next.arrayOf(context);
		} else {
			e = super.dotExp(sc, e, ident, context);
		}
		return e;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer buf2 = new OutBuffer();
		toPrettyBracket(buf2, hgs, context);
		buf.prependstring(buf2.toChars());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
		next.toCBuffer2(buf, null, hgs, context);
	}

	public abstract void toPrettyBracket(OutBuffer buf, HdrGenState hgs,
			SemanticContext context);
}
