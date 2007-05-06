package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.MATCH.*;

import org.eclipse.core.runtime.Assert;

public class StringExp extends Expression {

	public String string;
	public char postfix;
	public char sz; // 1: char, 2: wchar, 4: dchar
	public boolean committed; // !=0 if type is committed
	public int len;

	public StringExp(Loc loc, String string) {
		super(loc, TOK.TOKstring);
		this.sz = 1;
		this.committed = false;
		this.postfix = 0;
	}

	public StringExp(Loc loc, String string, char postfix) {
		super(loc, TOK.TOKstring);
		this.string = string;
		this.sz = 1;
		this.committed = false;
		this.postfix = postfix;
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		// TODO semantic
		return this;
	}

	@Override
	public String toChars() {
		OutBuffer out = new OutBuffer();
		HdrGenState hdr = new HdrGenState();
		toCBuffer(out, hdr, null);
		return out.toChars();
	}

	public StringExp toUTF8(Scope sc, SemanticContext context) {
		if (sz != 1) { // Convert to UTF-8 string
			committed = false;
			Expression e = castTo(sc, Type.tchar.arrayOf(context), context);
			e = e.optimize(WANTvalue);
			Assert.isTrue(e.op == TOK.TOKstring);
			StringExp se = (StringExp) e;
			Assert.isTrue(se.sz == 1);
			return se;
		}
		return this;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		return super.semantic(sc, context);
	}

	@Override
	public int getNodeType() {
		return STRING_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		if (!committed) {
			if (!committed && t.ty == Tpointer && t.next.ty == Tvoid) {
				return MATCHnomatch;
			}
			if (type.ty == Tsarray || type.ty == Tarray || type.ty == Tpointer) {
				if (type.next.ty == Tchar) {
					switch (t.ty) {
					case Tsarray:
						if (type.ty == Tsarray
								&& ((TypeSArray) type).dim.toInteger(context) != ((TypeSArray) t).dim
										.toInteger(context)) {
							return MATCHnomatch;
						}
					case Tarray:
					case Tpointer:
						if (t.next.ty == Tchar) {
							return MATCHexact;
						} else if (t.next.ty == Twchar) {
							return MATCHexact;
						} else if (t.next.ty == Tdchar) {
							return MATCHexact;
						}
						break;
					}
				}
			}
		}
		return super.implicitConvTo(t, context);
	}

	@Override
	public boolean isBool(boolean result) {
		return result ? true : false;
	}

	public StringExp toUTF8(Scope sc) {
		// TODO semantic
		return this;
	}

}