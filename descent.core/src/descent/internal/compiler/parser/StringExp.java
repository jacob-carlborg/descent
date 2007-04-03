package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.MATCH.*;

public class StringExp extends Expression {

	public String string;
	public char postfix;
	public char sz; // 1: char, 2: wchar, 4: dchar
	public boolean committed; // !=0 if type is committed
	public int len;

	public StringExp(String string) {
		this(string, (char) 0);
	}

	public StringExp(String string, char postfix) {
		super(TOK.TOKstring);
		this.string = string;
		this.postfix = postfix;
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		// TODO semantic
		return this;
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
		return result;
	}

	public StringExp toUTF8(Scope sc) {
		// TODO semantic
		return this;
	}

}
