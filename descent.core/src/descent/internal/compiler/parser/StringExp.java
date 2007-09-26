package descent.internal.compiler.parser;

import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tchar;
import static descent.internal.compiler.parser.TY.Tdchar;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tvoid;
import static descent.internal.compiler.parser.TY.Twchar;

// DMD 1.020
public class StringExp extends Expression {

	public char[] sourceString;

	public char[] string;
	public int len;
	public char postfix;
	public char sz; // 1: char, 2: wchar, 4: dchar
	public boolean committed; // !=0 if type is committed

	public List<StringExp> allStringExps;

	public StringExp(Loc loc, char[] string) {
		this(loc, string, string.length);
	}

	public StringExp(Loc loc, char[] string, int len) {
		this(loc, string, len, (char) 0);
	}

	public StringExp(Loc loc, char[] string, int len, char postfix) {
		super(loc, TOK.TOKstring);
		this.string = string;
		this.len = len;
		this.sz = 1;
		this.committed = false;
		this.postfix = postfix;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		// TODO semantic
		return this;
	}

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer out = new OutBuffer();
		HdrGenState hdr = new HdrGenState();
		toCBuffer(out, hdr, context);
		return out.toChars();
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		// TODO semantic
		super.toCBuffer(buf, hgs, context);
	}

	public StringExp toUTF8(Scope sc, SemanticContext context) {
		if (sz != 1) {
			// Convert to UTF-8 string
			committed = false;
			Expression e = castTo(sc, Type.tchar.arrayOf(context), context);
			e = e.optimize(WANTvalue, context);
			Assert.isTrue(e.op == TOK.TOKstring);
			StringExp se = (StringExp) e;
			Assert.isTrue(se.sz == 1);
			return se;
		}
		return this;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			OutBuffer buffer = new OutBuffer();
			int newlen = 0;
			String p;
			int[] u = { 0 };
			int[] c = { 0 };

			switch (postfix) {
			case 'd':
				for (u[0] = 0; u[0] < len;) {
					p = Utf.decodeChar(string, 0, len, u, c);
					// utf_decodeChar((unsigned char )string, len, &u, &c);
					if (p != null) {
						error("%s", p);
						break;
					} else {
						buffer.write4(c[0]);
						newlen++;
					}
				}
				buffer.write4(0);
				string = buffer.extractData().toCharArray();
				len = newlen;
				sz = 4;
				type = new TypeSArray(Type.tdchar, new IntegerExp(loc, len,
						Type.tindex));
				committed = true;
				break;

			case 'w':
				for (u[0] = 0; u[0] < len;) {
					p = Utf.decodeChar(string, 0, len, u, c);
					if (p != null) {
						error("%s", p);
						break;
					} else {
						buffer.writeUTF16(c[0]);
						newlen++;
						if (c[0] >= 0x10000)
							newlen++;
					}
				}
				buffer.writeUTF16(0);
				string = buffer.extractData().toCharArray();
				len = newlen;
				sz = 2;
				type = new TypeSArray(Type.twchar, new IntegerExp(loc, len,
						Type.tindex));
				committed = true;
				break;

			case 'c':
				committed = true;
			default:
				type = new TypeSArray(Type.tchar, new IntegerExp(loc, len,
						Type.tindex));
				break;
			}
			type = type.semantic(loc, sc, context);
		}
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
		return result ? true : false;
	}

}
