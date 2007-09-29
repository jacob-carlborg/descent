package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.STC.STCfield;

import static descent.internal.compiler.parser.TOK.TOKdotvar;
import static descent.internal.compiler.parser.TOK.TOKvar;

import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tbool;
import static descent.internal.compiler.parser.TY.Tchar;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tcomplex32;
import static descent.internal.compiler.parser.TY.Tcomplex64;
import static descent.internal.compiler.parser.TY.Tcomplex80;
import static descent.internal.compiler.parser.TY.Tdchar;
import static descent.internal.compiler.parser.TY.Terror;
import static descent.internal.compiler.parser.TY.Tfloat32;
import static descent.internal.compiler.parser.TY.Tfloat64;
import static descent.internal.compiler.parser.TY.Tfloat80;
import static descent.internal.compiler.parser.TY.Tident;
import static descent.internal.compiler.parser.TY.Timaginary32;
import static descent.internal.compiler.parser.TY.Timaginary64;
import static descent.internal.compiler.parser.TY.Timaginary80;
import static descent.internal.compiler.parser.TY.Tint16;
import static descent.internal.compiler.parser.TY.Tint32;
import static descent.internal.compiler.parser.TY.Tint64;
import static descent.internal.compiler.parser.TY.Tint8;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tuns16;
import static descent.internal.compiler.parser.TY.Tuns32;
import static descent.internal.compiler.parser.TY.Tuns64;
import static descent.internal.compiler.parser.TY.Tuns8;
import static descent.internal.compiler.parser.TY.Tvoid;
import static descent.internal.compiler.parser.TY.Twchar;

// DMD 1.020
public abstract class Type extends ASTDmdNode {

	public final static int PTRSIZE = 4;
	
	// TODO where to get this value from?
	public final static int REALSIZE = 8;

	public static class Modification {
		public int startPosition;
		public int length;
		public TOK tok;

		public Modification(TOK tok, int startPosition, int length) {
			this.startPosition = startPosition;
			this.length = length;
			this.tok = tok;
		}
	}

	public final static Type tvoid = new TypeBasic(TY.Tvoid);
	public final static Type tint8 = new TypeBasic(TY.Tint8);
	public final static Type tuns8 = new TypeBasic(TY.Tuns8);
	public final static Type tint16 = new TypeBasic(TY.Tint16);
	public final static Type tuns16 = new TypeBasic(TY.Tuns16);
	public final static Type tint32 = new TypeBasic(TY.Tint32);
	public final static Type tuns32 = new TypeBasic(TY.Tuns32);
	public final static Type tint64 = new TypeBasic(TY.Tint64);
	public final static Type tuns64 = new TypeBasic(TY.Tuns64);
	public final static Type tfloat32 = new TypeBasic(TY.Tfloat32);
	public final static Type tfloat64 = new TypeBasic(TY.Tfloat64);
	public final static Type tfloat80 = new TypeBasic(TY.Tfloat80);
	public final static Type timaginary32 = new TypeBasic(TY.Timaginary32);
	public final static Type timaginary64 = new TypeBasic(TY.Timaginary64);
	public final static Type timaginary80 = new TypeBasic(TY.Timaginary80);
	public final static Type tcomplex32 = new TypeBasic(TY.Tcomplex32);
	public final static Type tcomplex64 = new TypeBasic(TY.Tcomplex64);
	public final static Type tcomplex80 = new TypeBasic(TY.Tcomplex80);
	public final static Type tbit = new TypeBasic(TY.Tbit);
	public final static Type tbool = new TypeBasic(TY.Tbool);
	public final static Type tchar = new TypeBasic(TY.Tchar);
	public final static Type twchar = new TypeBasic(TY.Twchar);
	public final static Type tdchar = new TypeBasic(TY.Tdchar);
	public final static Type terror = new TypeBasic(TY.Terror); // for error recovery
	public final static Type tindex = tint32;
	public final static Type tboolean = tbool;
	public final static Type tsize_t = tuns32;
	public final static Type tptrdiff_t = tint32;
	public final static Type tshiftcnt = tint32;

	public static boolean impcnvWarn[][];
	public static TY impcnvResult[][];
	public static TY impcnvType1[][];
	public static TY impcnvType2[][];
	public static Type basic[];

	static {
		int TMAX = TY.values().length;

		basic = new Type[TMAX];
		basic[Tvoid.ordinal()] = new TypeBasic(Tvoid);
		basic[Tint8.ordinal()] = new TypeBasic(Tint8);
		basic[Tuns8.ordinal()] = new TypeBasic(Tuns8);
		basic[Tint16.ordinal()] = new TypeBasic(Tint16);
		basic[Tuns16.ordinal()] = new TypeBasic(Tuns16);
		basic[Tint32.ordinal()] = new TypeBasic(Tint32);
		basic[Tuns32.ordinal()] = new TypeBasic(Tuns32);
		basic[Tint64.ordinal()] = new TypeBasic(Tint64);
		basic[Tuns64.ordinal()] = new TypeBasic(Tuns64);
		basic[Tfloat32.ordinal()] = new TypeBasic(Tfloat32);
		basic[Tfloat64.ordinal()] = new TypeBasic(Tfloat64);
		basic[Tfloat80.ordinal()] = new TypeBasic(Tfloat80);
		basic[Timaginary32.ordinal()] = new TypeBasic(Timaginary32);
		basic[Timaginary64.ordinal()] = new TypeBasic(Timaginary64);
		basic[Timaginary80.ordinal()] = new TypeBasic(Timaginary80);
		basic[Tcomplex32.ordinal()] = new TypeBasic(Tcomplex32);
		basic[Tcomplex64.ordinal()] = new TypeBasic(Tcomplex64);
		basic[Tcomplex80.ordinal()] = new TypeBasic(Tcomplex80);
		basic[Tbit.ordinal()] = new TypeBasic(Tbit);
		basic[Tbool.ordinal()] = new TypeBasic(Tbool);
		basic[Tchar.ordinal()] = new TypeBasic(Tchar);
		basic[Twchar.ordinal()] = new TypeBasic(Twchar);
		basic[Tdchar.ordinal()] = new TypeBasic(Tdchar);

		impcnvResult = new TY[TMAX][];
		impcnvType1 = new TY[TMAX][];
		impcnvType2 = new TY[TMAX][];
		impcnvWarn = new boolean[TMAX][];

		for (int i = 0; i < TMAX; i++) {
			impcnvResult[i] = new TY[TMAX];
			impcnvType1[i] = new TY[TMAX];
			impcnvType2[i] = new TY[TMAX];
			impcnvWarn[i] = new boolean[TMAX];
			for (int j = 0; j < TMAX; j++) {
				impcnvResult[i][j] = Terror;
				impcnvType1[i][j] = Terror;
				impcnvType2[i][j] = Terror;
				impcnvWarn[i][j] = false;
			}
		}

		X(Tbit, Tbit, Tint32, Tint32, Tint32);
		X(Tbit, Tint8, Tint32, Tint32, Tint32);
		X(Tbit, Tuns8, Tint32, Tint32, Tint32);
		X(Tbit, Tint16, Tint32, Tint32, Tint32);
		X(Tbit, Tuns16, Tint32, Tint32, Tint32);
		X(Tbit, Tint32, Tint32, Tint32, Tint32);
		X(Tbit, Tuns32, Tuns32, Tuns32, Tuns32);
		X(Tbit, Tint64, Tint64, Tint64, Tint64);
		X(Tbit, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tbit, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tbit, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tbit, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tbit, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tbit, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tbit, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tbit, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tbit, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tbit, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tbool, Tbool, Tbool, Tbool, Tbool);
		X(Tbool, Tint8, Tint32, Tint32, Tint32);
		X(Tbool, Tuns8, Tint32, Tint32, Tint32);
		X(Tbool, Tint16, Tint32, Tint32, Tint32);
		X(Tbool, Tuns16, Tint32, Tint32, Tint32);
		X(Tbool, Tint32, Tint32, Tint32, Tint32);
		X(Tbool, Tuns32, Tuns32, Tuns32, Tuns32);
		X(Tbool, Tint64, Tint64, Tint64, Tint64);
		X(Tbool, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tbool, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tbool, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tbool, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tbool, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tbool, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tbool, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tbool, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tbool, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tbool, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tint8, Tint8, Tint32, Tint32, Tint32);
		X(Tint8, Tuns8, Tint32, Tint32, Tint32);
		X(Tint8, Tint16, Tint32, Tint32, Tint32);
		X(Tint8, Tuns16, Tint32, Tint32, Tint32);
		X(Tint8, Tint32, Tint32, Tint32, Tint32);
		X(Tint8, Tuns32, Tuns32, Tuns32, Tuns32);
		X(Tint8, Tint64, Tint64, Tint64, Tint64);
		X(Tint8, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tint8, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tint8, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tint8, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tint8, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tint8, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tint8, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tint8, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tint8, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tint8, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tuns8, Tuns8, Tint32, Tint32, Tint32);
		X(Tuns8, Tint16, Tint32, Tint32, Tint32);
		X(Tuns8, Tuns16, Tint32, Tint32, Tint32);
		X(Tuns8, Tint32, Tint32, Tint32, Tint32);
		X(Tuns8, Tuns32, Tuns32, Tuns32, Tuns32);
		X(Tuns8, Tint64, Tint64, Tint64, Tint64);
		X(Tuns8, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tuns8, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tuns8, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tuns8, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tuns8, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tuns8, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tuns8, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tuns8, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tuns8, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tuns8, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tint16, Tint16, Tint32, Tint32, Tint32);
		X(Tint16, Tuns16, Tint32, Tint32, Tint32);
		X(Tint16, Tint32, Tint32, Tint32, Tint32);
		X(Tint16, Tuns32, Tuns32, Tuns32, Tuns32);
		X(Tint16, Tint64, Tint64, Tint64, Tint64);
		X(Tint16, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tint16, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tint16, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tint16, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tint16, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tint16, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tint16, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tint16, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tint16, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tint16, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tuns16, Tuns16, Tint32, Tint32, Tint32);
		X(Tuns16, Tint32, Tint32, Tint32, Tint32);
		X(Tuns16, Tuns32, Tuns32, Tuns32, Tuns32);
		X(Tuns16, Tint64, Tint64, Tint64, Tint64);
		X(Tuns16, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tuns16, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tuns16, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tuns16, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tuns16, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tuns16, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tuns16, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tuns16, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tuns16, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tuns16, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tint32, Tint32, Tint32, Tint32, Tint32);
		X(Tint32, Tuns32, Tuns32, Tuns32, Tuns32);
		X(Tint32, Tint64, Tint64, Tint64, Tint64);
		X(Tint32, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tint32, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tint32, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tint32, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tint32, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tint32, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tint32, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tint32, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tint32, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tint32, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tuns32, Tuns32, Tuns32, Tuns32, Tuns32);
		X(Tuns32, Tint64, Tint64, Tint64, Tint64);
		X(Tuns32, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tuns32, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tuns32, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tuns32, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tuns32, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tuns32, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tuns32, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tuns32, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tuns32, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tuns32, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tint64, Tint64, Tint64, Tint64, Tint64);
		X(Tint64, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tint64, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tint64, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tint64, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tint64, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tint64, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tint64, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tint64, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tint64, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tint64, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tuns64, Tuns64, Tuns64, Tuns64, Tuns64);

		X(Tuns64, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tuns64, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tuns64, Tfloat80, Tfloat80, Tfloat80, Tfloat80);
		X(Tuns64, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tuns64, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tuns64, Timaginary80, Tfloat80, Timaginary80, Tfloat80);
		X(Tuns64, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tuns64, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tuns64, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tfloat32, Tfloat32, Tfloat32, Tfloat32, Tfloat32);
		X(Tfloat32, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tfloat32, Tfloat80, Tfloat80, Tfloat80, Tfloat80);

		X(Tfloat32, Timaginary32, Tfloat32, Timaginary32, Tfloat32);
		X(Tfloat32, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tfloat32, Timaginary80, Tfloat80, Timaginary80, Tfloat80);

		X(Tfloat32, Tcomplex32, Tfloat32, Tcomplex32, Tcomplex32);
		X(Tfloat32, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tfloat32, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tfloat64, Tfloat64, Tfloat64, Tfloat64, Tfloat64);
		X(Tfloat64, Tfloat80, Tfloat80, Tfloat80, Tfloat80);

		X(Tfloat64, Timaginary32, Tfloat64, Timaginary64, Tfloat64);
		X(Tfloat64, Timaginary64, Tfloat64, Timaginary64, Tfloat64);
		X(Tfloat64, Timaginary80, Tfloat80, Timaginary80, Tfloat80);

		X(Tfloat64, Tcomplex32, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tfloat64, Tcomplex64, Tfloat64, Tcomplex64, Tcomplex64);
		X(Tfloat64, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tfloat80, Tfloat80, Tfloat80, Tfloat80, Tfloat80);

		X(Tfloat80, Timaginary32, Tfloat80, Timaginary80, Tfloat80);
		X(Tfloat80, Timaginary64, Tfloat80, Timaginary80, Tfloat80);
		X(Tfloat80, Timaginary80, Tfloat80, Timaginary80, Tfloat80);

		X(Tfloat80, Tcomplex32, Tfloat80, Tcomplex80, Tcomplex80);
		X(Tfloat80, Tcomplex64, Tfloat80, Tcomplex80, Tcomplex80);
		X(Tfloat80, Tcomplex80, Tfloat80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Timaginary32, Timaginary32, Timaginary32, Timaginary32, Timaginary32);
		X(Timaginary32, Timaginary64, Timaginary64, Timaginary64, Timaginary64);
		X(Timaginary32, Timaginary80, Timaginary80, Timaginary80, Timaginary80);

		X(Timaginary32, Tcomplex32, Timaginary32, Tcomplex32, Tcomplex32);
		X(Timaginary32, Tcomplex64, Timaginary64, Tcomplex64, Tcomplex64);
		X(Timaginary32, Tcomplex80, Timaginary80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Timaginary64, Timaginary64, Timaginary64, Timaginary64, Timaginary64);
		X(Timaginary64, Timaginary80, Timaginary80, Timaginary80, Timaginary80);

		X(Timaginary64, Tcomplex32, Timaginary64, Tcomplex64, Tcomplex64);
		X(Timaginary64, Tcomplex64, Timaginary64, Tcomplex64, Tcomplex64);
		X(Timaginary64, Tcomplex80, Timaginary80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Timaginary80, Timaginary80, Timaginary80, Timaginary80, Timaginary80);

		X(Timaginary80, Tcomplex32, Timaginary80, Tcomplex80, Tcomplex80);
		X(Timaginary80, Tcomplex64, Timaginary80, Tcomplex80, Tcomplex80);
		X(Timaginary80, Tcomplex80, Timaginary80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tcomplex32, Tcomplex32, Tcomplex32, Tcomplex32, Tcomplex32);
		X(Tcomplex32, Tcomplex64, Tcomplex64, Tcomplex64, Tcomplex64);
		X(Tcomplex32, Tcomplex80, Tcomplex80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tcomplex64, Tcomplex64, Tcomplex64, Tcomplex64, Tcomplex64);
		X(Tcomplex64, Tcomplex80, Tcomplex80, Tcomplex80, Tcomplex80);

		/* ======================= */

		X(Tcomplex80, Tcomplex80, Tcomplex80, Tcomplex80, Tcomplex80);

		/* ======================= */
		/* ======================= */

		Y(Tint8, Tbit);
		Y(Tuns8, Tbit);
		Y(Tint16, Tbit);
		Y(Tuns16, Tbit);
		Y(Tint32, Tbit);
		Y(Tuns32, Tbit);
		Y(Tint64, Tbit);
		Y(Tuns64, Tbit);

		Y(Tuns8, Tint8);
		Y(Tint16, Tint8);
		Y(Tuns16, Tint8);
		Y(Tint32, Tint8);
		Y(Tuns32, Tint8);
		Y(Tint64, Tint8);
		Y(Tuns64, Tint8);

		Y(Tint8, Tuns8);
		Y(Tint16, Tuns8);
		Y(Tuns16, Tuns8);
		Y(Tint32, Tuns8);
		Y(Tuns32, Tuns8);
		Y(Tint64, Tuns8);
		Y(Tuns64, Tuns8);

		Y(Tuns16, Tint16);
		Y(Tint32, Tint16);
		Y(Tuns32, Tint16);
		Y(Tint64, Tint16);
		Y(Tuns64, Tint16);

		Y(Tint16, Tuns16);
		Y(Tint32, Tuns16);
		Y(Tuns32, Tuns16);
		Y(Tint64, Tuns16);
		Y(Tuns64, Tuns16);

		// Y(Tuns32, Tint32);
		Y(Tint64, Tint32);
		Y(Tuns64, Tint32);

		// Y(Tint32, Tuns32);
		Y(Tint64, Tuns32);
		Y(Tuns64, Tuns32);

		Y(Tint64, Tuns64);
		Y(Tuns64, Tint64);

		/* ======================= */
		/* ======================= */

		for (int i = 0; i < TMAX; i++) {
			for (int j = 0; j < TMAX; j++) {
				if (impcnvResult[i][j] == Terror) {
					impcnvResult[i][j] = impcnvResult[j][i];
					impcnvType1[i][j] = impcnvType2[j][i];
					impcnvType2[i][j] = impcnvType1[j][i];
				}
			}
		}
	}

	private static void X(TY t1, TY t2, TY nt1, TY nt2, TY rt) {
		X(t1.ordinal(), t2.ordinal(), nt1, nt2, rt);
	}

	private static void X(int t1, int t2, TY nt1, TY nt2, TY rt) {
		impcnvResult[t1][t2] = rt;
		impcnvType1[t1][t2] = nt1;
		impcnvType2[t1][t2] = nt2;
	}

	private static void Y(TY t1, TY t2) {
		Y(t1.ordinal(), t2.ordinal());
	}

	private static void Y(int t1, int t2) {
		impcnvWarn[t1][t2] = true;
	}

	public TY ty;
	public Type next, sourceNext;
	public String deco;
	public Type pto; // merged pointer to this type
	public Type rto; // reference to this type
	public Type arrayof; // array of this type
	public TypeInfoDeclaration vtinfo; // TypeInfo object for this Type

	public List<Modification> modifications;

	public Type(TY ty, Type next) {
		this.ty = ty;
		this.next = next;
		this.sourceNext = next;
	}

	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_TYPE;
	}

	public TypeBasic isTypeBasic() {
		return null;
	}

	public boolean isbit() {
		return false;
	}

	public boolean iscomplex() {
		return false;
	}

	public boolean isreal() {
		return false;
	}

	public boolean isimaginary() {
		return false;
	}

	public ClassDeclaration isClassHandle() {
		return null;
	}

	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		if (next != null) {
			next = next.semantic(loc, sc, context);
		}
		return merge(context);
	}

	public Type merge(SemanticContext context) {
		Type t;

		t = this;
		if (deco == null) {
			OutBuffer buf = new OutBuffer();
			StringValue sv;

			if (next != null) {
				next = next.merge(context);
			}
			toDecoBuffer(buf, context);
			sv = context.typeStringTable.update(buf.toString());
			if (sv.ptrvalue != null) {
				t = (Type) sv.ptrvalue;
				assert t.deco != null;
			} else {
				sv.ptrvalue = this;
				deco = sv.lstring;
			}
		}
		return t;
	}

	public void toDecoBuffer(OutBuffer buf, SemanticContext context) {
		buf.writeByte(ty.mangleChar);
		if (next != null) {
			Assert.isTrue(next != this);
			next.toDecoBuffer(buf, context);
		}
	}

	public void resolve(Loc loc, Scope sc, Expression[] pe, Type[] pt,
			Dsymbol[] ps, SemanticContext context) {
		Type t;

		t = semantic(loc, sc, context);
		pt[0] = t;
		pe[0] = null;
		ps[0] = null;
	}

	public Expression toExpression() {
		return null;
	}

	public Type toBasetype(SemanticContext context) {
		return this;
	}

	public Dsymbol toDsymbol(Scope sc, SemanticContext context) {
		return null;
	}

	public boolean isintegral() {
		return false;
	}

	public boolean isunsigned() {
		return false;
	}

	public boolean isauto() {
		return false;
	}

	public Type pointerTo(SemanticContext context) {
		if (pto == null) {
			Type t;

			t = new TypePointer(this);
			pto = t.merge(context);
		}
		return pto;
	}

	public Type referenceTo() {
		return null;
	}

	public Type arrayOf(SemanticContext context) {
		if (arrayof == null) {
			Type t;

			t = new TypeDArray(this);
			arrayof = t.merge(context);
		}
		return arrayof;
	}

	public Expression defaultInit(SemanticContext context) {
		return null;
	}

	public final Expression getProperty(Loc loc, IdentifierExp ident,
			SemanticContext context) {
		return getProperty(loc, ident.ident, ident.start, ident.length, context);
	}

	public Expression getProperty(Loc loc, char[] ident, int start, int length,
			SemanticContext context) {
		Expression e = null;

		if (CharOperation.equals(ident, Id.__sizeof)) {
			e = new IntegerExp(loc, size(loc, context), Type.tsize_t);
		} else if (CharOperation.equals(ident, Id.size)) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.DeprecatedProperty, 0, start, length,
					new String[] { ".size", ".sizeof" }));
			e = new IntegerExp(loc, size(loc, context), Type.tsize_t);
		} else if (CharOperation.equals(ident, Id.alignof)) {
			e = new IntegerExp(loc, alignsize(context), Type.tsize_t);
		} else if (CharOperation.equals(ident, Id.typeinfo)) {
			if (!context.global.params.useDeprecated) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.DeprecatedProperty, 0, start, length,
						new String[] { "typeinfo", ".typeid(type)" }));
			}
			e = getTypeInfo(null, context);
		} else if (CharOperation.equals(ident, Id.init)) {
			e = defaultInit(context);
		} else if (CharOperation.equals(ident, Id.mangleof)) {
			Assert.isNotNull(deco);
			e = new StringExp(loc, deco.toCharArray(), deco.length(), 'c');
			Scope sc = new Scope(context);
			e = e.semantic(sc, context);
		} else if (CharOperation.equals(ident, Id.stringof)) {
			char[] s = toChars(context).toCharArray();
			e = new StringExp(loc, s, s.length, 'c');
			Scope sc = new Scope(context);
			e = e.semantic(sc, context);
		} else {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.UndefinedProperty, 0, start, length, new String[] {
							new String(ident), toChars(context) }));
			e = new IntegerExp(loc, Id.ONE, 1, Type.tint32);
		}
		return e;
	}

	public Type reliesOnTident() {
		if (next == null) {
			return null;
		} else {
			return next.reliesOnTident();
		}
	}

	public void checkDeprecated(Loc loc, Scope sc, SemanticContext context) {
		Type t;
		Dsymbol s;

		for (t = this; t != null; t = t.next) {
			s = t.toDsymbol(sc, context);
			if (s != null)
				s.checkDeprecated(sc, context);
		}
	}

	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		VarDeclaration v = null;

		if (e.op == TOKdotvar) {
			DotVarExp dv = (DotVarExp) e;
			v = dv.var.isVarDeclaration();
		} else if (e.op == TOKvar) {
			VarExp ve = (VarExp) e;
			v = ve.var.isVarDeclaration();
		}
		if (null != v) {
			if (CharOperation.equals(ident.ident, Id.offset)) {
				if (!context.global.params.useDeprecated) {
					error(".offset deprecated, use .offsetof");
				}
				//goto Loffset;
				if (0 != (v.storage_class & STCfield)) {
					e = new IntegerExp(e.loc, v.offset, Type.tsize_t);
					return e;
				}
			} else if (CharOperation.equals(ident.ident, Id.offsetof)) {
				//Loffset:
				if (0 != (v.storage_class & STCfield)) {
					e = new IntegerExp(e.loc, v.offset, Type.tsize_t);
					return e;
				}
			} else if (CharOperation.equals(ident.ident, Id.init)) {
				return defaultInit(context);
			}
		}

		if (CharOperation.equals(ident.ident, Id.typeinfo)) {
			if (!context.global.params.useDeprecated) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.DeprecatedProperty, 0, ident.start, ident.length,
						new String[] { ".typeinfo", "typeid(type)" }));
			}
			e = getTypeInfo(sc, context);
			return e;
		}

		if (CharOperation.equals(ident.ident, Id.stringof)) {
			char[] s = e.toChars(context).toCharArray();
			e = new StringExp(e.loc, s, 'c');
			Scope _sc = new Scope(context);
			e = e.semantic(_sc, context);
			return e;
		}

		return getProperty(e.loc, ident, context);
	}

	public int size(Loc loc, SemanticContext context) {
		error("no size for type %s", toChars(context));
		return 1;
	}

	public final int size(SemanticContext context) {
		return size(Loc.ZERO, context);
	}

	public int alignsize(SemanticContext context) {
		return size(null, context);
	}

	public int memalign(int salign, SemanticContext context) {
		return salign;
	}

	public boolean isBaseOf(Type type, int[] poffset, SemanticContext context) {
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Type))
			return false;

		Type t = (Type) o;

		// deco strings are unique and semantic() has been run
		if (this == o || (t != null && deco != null && deco.equals(t.deco))) {
			return true;
		}
		return false;
	}

	public MATCH implicitConvTo(Type to, SemanticContext context) {
		if (this == to) {
			return MATCH.MATCHexact;
		}
		return MATCH.MATCHnomatch;
	}

	private final static int COVARIANT = 1;
	private final static int DISTINCT = 0;
	private final static int NOT_COVARIANT = 2;
	private final static int OTHER = 3;

	public int covariant(Type t, SemanticContext context) {
		boolean inoutmismatch = false;

		if (equals(t)) {
			return COVARIANT;
		}
		if (ty != TY.Tfunction || t.ty != TY.Tfunction) {
			return DISTINCT;
		}

		TypeFunction t1 = (TypeFunction) this;
		TypeFunction t2 = (TypeFunction) t;

		if (t1.varargs != t2.varargs) {
			return DISTINCT;
		}

		if (t1.parameters != null && t2.parameters != null) {
			int dim = Argument.dim(t1.parameters, context);
			if (dim != Argument.dim(t2.parameters, context)) {
				return DISTINCT;
			}

			for (int i = 0; i < dim; i++) {
				Argument arg1 = Argument.getNth(t1.parameters, i, context);
				Argument arg2 = Argument.getNth(t2.parameters, i, context);

				if (!arg1.type.equals(arg2.type)) {
					return DISTINCT;
				}
				if (arg1.storageClass != arg2.storageClass) {
					inoutmismatch = true;
				}
			}
		} else if (t1.parameters != t2.parameters) {
			return DISTINCT;
		}

		// The argument lists match
		if (inoutmismatch) {
			return NOT_COVARIANT;
		}
		if (t1.linkage != t2.linkage) {
			return NOT_COVARIANT;
		}

		Type t1n = t1.next;
		Type t2n = t2.next;

		if (t1n.equals(t2n)) {
			return COVARIANT;
		}
		if (t1n.ty != TY.Tclass || t2n.ty != TY.Tclass) {
			return NOT_COVARIANT;
		}

		// If t1n is forward referenced:
		ClassDeclaration cd = ((TypeClass) t1n).sym;
		if (cd.baseClass == null && cd.baseclasses != null
				&& cd.baseclasses.size() > 0
				&& cd.isInterfaceDeclaration() == null) {
			return OTHER;
		}

		if (t1n.implicitConvTo(t2n, context) != MATCH.MATCHnomatch) {
			return COVARIANT;
		}
		return NOT_COVARIANT;
	}

	public boolean isfloating() {
		return false;
	}

	public boolean isString(SemanticContext context) {
		return false;
	}

	public boolean isZeroInit(SemanticContext context) {
		return false;
	}

	public boolean checkBoolean(SemanticContext context) {
		return isscalar(context);
	}

	public boolean isscalar(SemanticContext context) {
		return false;
	}

	public Type syntaxCopy() {
		// TODO semantic
		return this;
	}

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();
		toCBuffer2(buf, null, hgs, context);
		return buf.toChars();
	}

	public void toCBuffer(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer tbuf = new OutBuffer();
		toCBuffer2(tbuf, ident, hgs, context);
		buf.write(tbuf);
	}

	public boolean hasPointers(SemanticContext context) {
		return false;
	}

	public char[] getTypeInfoIdent(int internal) {
		// TODO semantic
		return null;
	}

	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		buf.prependstring(toChars(context));
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.ident);
		}
	}

	public Type nextOf() {
		return next;
	}

	public Type makeConst(int startPosition, int length) {
		if (this.modifications == null) {
			this.modifications = new ArrayList<Modification>();
		}
		this.modifications.add(new Modification(TOK.TOKconst, startPosition,
				length));
		return this;
	}

	public Type makeInvariant(int startPosition, int length) {
		if (this.modifications == null) {
			this.modifications = new ArrayList<Modification>();
		}
		this.modifications.add(new Modification(TOK.TOKinvariant,
				startPosition, length));
		return this;
	}

	public Expression getInternalTypeInfo(Scope sc) {
		// TODO semantic
		return null;
	}

	public int templateParameterLookup(Type tparam,
			TemplateParameters parameters) {
		if (tparam.ty != Tident) {
			throw new IllegalStateException("assert(tparam.ty == Tident);");
		}
		TypeIdentifier tident = (TypeIdentifier) tparam;
		if (tident.idents.size() == 0) {
			IdentifierExp id = tident.ident;

			for (int i = 0; i < parameters.size(); i++) {
				TemplateParameter tp = (TemplateParameter) parameters.get(i);

				if (tp.ident.equals(id))
					return i;
			}
		}
		return -1;
	}

	public MATCH deduceType(Scope sc, Type tparam,
			TemplateParameters parameters, Objects dedtypes,
			SemanticContext context) {
		if (tparam == null) {
			return MATCHnomatch;
		}

		if (this == tparam) {
			return MATCHexact;
		}

		if (tparam.ty == Tident) {
			// Determine which parameter tparam is
			int i = templateParameterLookup(tparam, parameters);
			if (i == -1) {
				if (sc == null) {
					return MATCHnomatch;
				}
				/* BUG: what if tparam is a template instance, that
				 * has as an argument another Tident?
				 */
				tparam = tparam.semantic(Loc.ZERO, sc, context);
				if (tparam.ty == Tident) {
					throw new IllegalStateException(
							"assert(tparam.ty != Tident);");
				}
				return deduceType(sc, tparam, parameters, dedtypes, context);
			}

			TemplateParameter tp = parameters.get(i);

			// Found the corresponding parameter tp
			if (null == tp.isTemplateTypeParameter()) {
				return MATCHnomatch;
			}
			Type at = (Type) dedtypes.get(i);
			if (null == at) {
				dedtypes.set(i, this);
				return MATCHexact;
			}
			if (equals(at)) {
				return MATCHexact;
			} else if (ty == Tclass && at.ty == Tclass) {
				return implicitConvTo(at, context);
			} else if (ty == Tsarray && at.ty == Tarray
					&& next.equals(at.nextOf())) {
				return MATCHexact;
			} else
				return MATCHnomatch;
		}

		if (ty != tparam.ty) {
			return MATCHnomatch;
		}

		if (nextOf() != null)
			return nextOf().deduceType(sc, tparam.nextOf(), parameters,
					dedtypes, context);

		return MATCHexact;
	}

	public TypeInfoDeclaration getTypeInfoDeclaration(SemanticContext context) {
		return new TypeInfoDeclaration(this, 0, context);
	}

	public void toTypeInfoBuffer(OutBuffer buf, SemanticContext context) {
		throw new IllegalStateException("assert(0);");
	}

	public boolean builtinTypeInfo() {
		return false;
	}

	public static char needThisPrefix() {
		return 'M'; // name mangling prefix for functions needing 'this'
	}

	public Expression getTypeInfo(Scope sc, SemanticContext context) {
		Expression e;
		Type t;

		t = merge(context); // do this since not all Type's are merge'd
		if (null == t.vtinfo) {
			t.vtinfo = t.getTypeInfoDeclaration(context);

			if (t.vtinfo == null) {
				throw new IllegalStateException("assert(t.vtinfo);");
			}

			/* If this has a custom implementation in std/typeinfo, then
			 * do not generate a COMDAT for it.
			 */
			if (!t.builtinTypeInfo()) { // Generate COMDAT
				if (sc != null) // if in semantic() pass
				{ // Find module that will go all the way to an object file
					Module m = sc.module.importedFrom;
					m.members.add(t.vtinfo);
				} else // if in obj generation pass
				{
					// TODO semantic
					// t.vtinfo.toObjFile();
				}
			}
		}
		e = new VarExp(Loc.ZERO, t.vtinfo);
		e = e.addressOf(sc, context);
		e.type = t.vtinfo.type; // do this so we don't get redundant dereference
		return e;
	}

}
