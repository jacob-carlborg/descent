package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.InOut.InOut;
import static descent.internal.compiler.parser.InOut.Lazy;
import static descent.internal.compiler.parser.InOut.Out;
import static descent.internal.compiler.parser.LINK.LINKd;
import static descent.internal.compiler.parser.PROT.PROTpackage;
import static descent.internal.compiler.parser.PROT.PROTprivate;
import static descent.internal.compiler.parser.PROT.PROTprotected;
import static descent.internal.compiler.parser.TOK.TOKarray;
import static descent.internal.compiler.parser.TOK.TOKdelegate;
import static descent.internal.compiler.parser.TOK.TOKforeach_reverse;
import static descent.internal.compiler.parser.TOK.TOKsuper;
import static descent.internal.compiler.parser.TOK.TOKtuple;
import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tstruct;
import static descent.internal.compiler.parser.TY.Ttuple;
import static descent.internal.compiler.parser.TY.Tvoid;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.ASTNode;

// class Object in DMD compiler
public abstract class ASTDmdNode extends ASTNode {

	public final static int WANTflags = 1;
	public final static int WANTvalue = 2;
	public final static int WANTinterpret = 4;

	public final static int ARGUMENT = 1;
	public final static int CATCH = 2;
	public final static int ALIGN_DECLARATION = 3;
	public final static int ANON_DECLARATION = 4;
	public final static int COMPILE_DECLARATION = 5;
	public final static int CONDITIONAL_DECLARATION = 6;
	public final static int LINK_DECLARATION = 7;
	public final static int PRAGMA_DECLARATION = 8;
	public final static int DEBUG_SYMBOL = 9;
	public final static int ALIAS_DECLARATION = 10;
	public final static int FUNC_DECLARATION = 11;
	public final static int CTOR_DECLARATION = 12;
	public final static int DELETE_DECLARATION = 13;
	public final static int DTOR_DECLARATION = 14;
	public final static int FUNC_LITERAL_DECLARATION = 15;
	public final static int INVARIANT_DECLARATION = 16;
	public final static int NEW_DECLARATION = 17;
	public final static int STATIC_CTOR_DECLARATION = 18;
	public final static int STATIC_DTOR_DECLARATION = 19;
	public final static int UNIT_TEST_DECLARATION = 20;
	public final static int PROT_DECLARATION = 21;
	public final static int STORAGE_CLASS_DECLARATION = 22;
	public final static int BASE_CLASS = 23;
	public final static int TYPEDEF_DECLARATION = 24;
	public final static int VAR_DECLARATION = 25;
	public final static int ENUM_MEMBER = 26;
	public final static int IMPORT = 27;
	public final static int MODIFIER_DECLARATION = 28;
	public final static int MULTI_IMPORT = 29;
	public final static int CLASS_DECLARATION = 30;
	public final static int INTERFACE_DECLARATION = 31;
	public final static int STRUCT_DECLARATION = 32;
	public final static int UNION_DECLARATION = 33;
	public final static int ENUM_DECLARATION = 34;
	public final static int MODULE = 35;
	public final static int TEMPLATE_DECLARATION = 36;
	public final static int TEMPLATE_INSTANCE = 37;
	public final static int TEMPLATE_MIXIN = 38;
	public final static int STATIC_ASSERT = 39;
	public final static int VERSION = 40;
	public final static int VERSION_SYMBOL = 41;
	public final static int ARRAY_LITERAL_EXP = 42;
	public final static int ADD_ASSIGN_EXP = 43;
	public final static int INCREMENT_EXP = 44;
	public final static int ADD_EXP = 45;
	public final static int AND_AND_EXP = 46;
	public final static int AND_ASSIGN_EXP = 47;
	public final static int AND_EXP = 48;
	public final static int ASSIGN_EXP = 49;
	public final static int CAT_ASSIGN_EXP = 50;
	public final static int CAT_EXP = 51;
	public final static int CMP_EXP = 52;
	public final static int COMMA_EXP = 53;
	public final static int COND_EXP = 54;
	public final static int DIV_ASSIGN_EXP = 55;
	public final static int DIV_EXP = 56;
	public final static int EQUAL_EXP = 57;
	public final static int IDENTITY_EXP = 58;
	public final static int IN_EXP = 59;
	public final static int MIN_ASSIGN_EXP = 60;
	public final static int DECREMENT_EXP = 61;
	public final static int MIN_EXP = 62;
	public final static int MOD_ASSIGN_EXP = 63;
	public final static int MOD_EXP = 64;
	public final static int MUL_ASSIGN_EXP = 65;
	public final static int MUL_EXP = 66;
	public final static int OR_ASSIGN_EXP = 67;
	public final static int OR_EXP = 68;
	public final static int OR_OR_EXP = 69;
	public final static int POST_EXP = 70;
	public final static int SHL_ASSIGN_EXP = 71;
	public final static int SHL_EXP = 72;
	public final static int SHR_ASSIGN_EXP = 73;
	public final static int SHR_EXP = 74;
	public final static int USHR_ASSIGN_EXP = 75;
	public final static int USHR_EXP = 76;
	public final static int XOR_ASSIGN_EXP = 77;
	public final static int XOR_EXP = 78;
	public final static int DECLARATION_EXP = 79;
	public final static int DOLLAR_EXP = 80;
	public final static int FUNC_EXP = 81;
	public final static int IDENTIFIER_EXP = 82;
	public final static int TEMPLATE_INSTANCE_WRAPPER = 83;
	public final static int IFTYPE_EXP = 84;
	public final static int INTEGER_EXP = 85;
	public final static int MULTI_STRING_EXP = 86;
	public final static int NEW_ANON_CLASS_EXP = 87;
	public final static int NEW_EXP = 88;
	public final static int NULL_EXP = 89;
	public final static int REAL_EXP = 90;
	public final static int SCOPE_EXP = 91;
	public final static int STRING_EXP = 92;
	public final static int THIS_EXP = 93;
	public final static int SUPER_EXP = 94;
	public final static int TYPE_DOT_ID_EXP = 95;
	public final static int TYPE_EXP = 96;
	public final static int TYPEID_EXP = 97;
	public final static int ADDR_EXP = 98;
	public final static int ARRAY_EXP = 99;
	public final static int ASSERT_EXP = 100;
	public final static int CALL_EXP = 101;
	public final static int CAST_EXP = 102;
	public final static int COM_EXP = 103;
	public final static int COMPILE_EXP = 104;
	public final static int DELETE_EXP = 105;
	public final static int DOT_ID_EXP = 106;
	public final static int DOT_TEMPLATE_INSTANCE_EXP = 107;
	public final static int FILE_EXP = 108;
	public final static int NEG_EXP = 109;
	public final static int NOT_EXP = 110;
	public final static int PAREN_EXP = 111;
	public final static int PTR_EXP = 112;
	public final static int SLICE_EXP = 113;
	public final static int UADD_EXP = 114;
	public final static int ARRAY_INITIALIZER = 115;
	public final static int EXP_INITIALIZER = 116;
	public final static int STRUCT_INITIALIZER = 117;
	public final static int VOID_INITIALIZER = 118;
	public final static int MODIFIER = 119;
	public final static int MODULE_DECLARATION = 120;
	public final static int ASM_STATEMENT = 121;
	public final static int BREAK_STATEMENT = 122;
	public final static int CASE_STATEMENT = 123;
	public final static int COMPILE_STATEMENT = 124;
	public final static int COMPOUND_STATEMENT = 125;
	public final static int ASM_BLOCK = 126;
	public final static int CONDITIONAL_STATEMENT = 127;
	public final static int CONTINUE_STATEMENT = 128;
	public final static int DEFAULT_STATEMENT = 129;
	public final static int DO_STATEMENT = 130;
	public final static int EXP_STATEMENT = 131;
	public final static int DECLARATION_STATEMENT = 132;
	public final static int FOREACH_STATEMENT = 133;
	public final static int FOR_STATEMENT = 134;
	public final static int GOTO_CASE_STATEMENT = 135;
	public final static int GOTO_DEFAULT_STATEMENT = 136;
	public final static int GOTO_STATEMENT = 137;
	public final static int IF_STATEMENT = 138;
	public final static int LABEL_STATEMENT = 139;
	public final static int ON_SCOPE_STATEMENT = 140;
	public final static int PRAGMA_STATEMENT = 141;
	public final static int RETURN_STATEMENT = 142;
	public final static int STATIC_ASSERT_STATEMENT = 143;
	public final static int SWITCH_STATEMENT = 144;
	public final static int SYNCHRONIZED_STATEMENT = 145;
	public final static int THROW_STATEMENT = 146;
	public final static int VOLATILE_STATEMENT = 148;
	public final static int WHILE_STATEMENT = 149;
	public final static int WITH_STATEMENT = 150;
	public final static int TEMPLATE_ALIAS_PARAMETER = 151;
	public final static int TEMPLATE_TUPLE_PARAMETER = 152;
	public final static int TEMPLATE_TYPE_PARAMETER = 153;
	public final static int TEMPLATE_VALUE_PARAMETER = 154;
	public final static int TYPE_A_ARRAY = 155;
	public final static int TYPE_BASIC = 156;
	public final static int TYPE_D_ARRAY = 157;
	public final static int TYPE_DELEGATE = 158;
	public final static int TYPE_FUNCTION = 159;
	public final static int TYPE_POINTER = 160;
	public final static int TYPE_IDENTIFIER = 161;
	public final static int TYPE_INSTANCE = 162;
	public final static int TYPE_TYPEOF = 163;
	public final static int TYPE_S_ARRAY = 164;
	public final static int TYPE_SLICE = 165;
	public final static int TYPE_TYPEDEF = 166;
	public final static int TYPE_ENUM = 167;
	public final static int TUPLE_DECLARATION = 168;
	public final static int TYPE_TUPLE = 169;
	public final static int VAR_EXP = 170;
	public final static int DOT_VAR_EXP = 171;
	public final static int TYPE_STRUCT = 172;
	public final static int DSYMBOL_EXP = 173;
	public final static int TYPE_CLASS = 174;
	public final static int THIS_DECLARATION = 175;
	public final static int ARRAY_SCOPE_SYMBOL = 176;
	public final static int SCOPE_DSYMBOL = 177;
	public final static int TEMPLATE_EXP = 178;
	public final static int TRY_FINALLY_STATEMENT = 179;
	public final static int TRY_CATCH_STATEMENT = 180;
	public final static int LABEL_DSYMBOL = 181;
	public final static int HALT_EXP = 182;
	public final static int SYM_OFF_EXP = 183;
	public final static int SCOPE_STATEMENT = 184;
	public final static int DELEGATE_EXP = 185;
	public final static int TUPLE_EXP = 186;
	public final static int UNROLLED_LOOP_STATEMENT = 187;
	public final static int COMPLEX_EXP = 188;
	public final static int ASSOC_ARRAY_LITERAL_EXP = 189;
	public final static int FOREACH_RANGE_STATEMENT = 190;
	public final static int TRAITS_EXP = 191;
	public final static int COMMENT = 192;
	public final static int PRAGMA = 193;

	private static int idn;

	/***************************************************************************
	 * Helper function for ClassDeclaration::accessCheck() Returns: 0 no access
	 * 1 access
	 */
	public static boolean accessCheckX(Dsymbol smember, Dsymbol sfunc,
			AggregateDeclaration dthis, AggregateDeclaration cdscope) {
		Assert.isNotNull(dthis);

		if (dthis.hasPrivateAccess(sfunc) || dthis.isFriendOf(cdscope)) {
			if (smember.toParent() == dthis) {
				return true;
			} else {
				ClassDeclaration cdthis = dthis.isClassDeclaration();
				if (cdthis != null) {
					for (int i = 0; i < cdthis.baseclasses.size(); i++) {
						BaseClass b = cdthis.baseclasses.get(i);
						PROT access;

						access = b.base.getAccess(smember);
						if (access.level >= PROTprotected.level
								|| accessCheckX(smember, sfunc, b.base, cdscope)) {
							return true;
						}

					}
				}
			}
		} else {
			if (smember.toParent() != dthis) {
				ClassDeclaration cdthis = dthis.isClassDeclaration();
				if (cdthis != null) {
					for (int i = 0; i < cdthis.baseclasses.size(); i++) {
						BaseClass b = cdthis.baseclasses.get(i);

						if (accessCheckX(smember, sfunc, b.base, cdscope)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static void inferApplyArgTypes(TOK op, List<Argument> arguments,
			Expression aggr, SemanticContext context) {
		if (arguments == null || arguments.isEmpty()) {
			return;
		}

		/*
		 * Return if no arguments need types.
		 */
		for (int u = 0; true; u++) {
			if (u == arguments.size()) {
				return;
			}
			Argument arg = arguments.get(u);
			if (arg.type == null) {
				break;
			}
		}

		AggregateDeclaration ad;
		FuncDeclaration fd;

		Argument arg = arguments.get(0);
		Type taggr = aggr.type;
		if (taggr == null) {
			return;
		}
		Type tab = taggr.toBasetype(context);
		switch (tab.ty) {
		case Tarray:
		case Tsarray:
		case Ttuple:
			if (arguments.size() == 2) {
				if (arg.type == null) {
					arg.type = Type.tsize_t; // key type
				}
				arg = arguments.get(1);
			}
			if (arg.type == null && tab.ty != Ttuple) {
				arg.type = tab.next; // value type
			}
			break;

		case Taarray: {
			TypeAArray taa = (TypeAArray) tab;

			if (arguments.size() == 2) {
				if (arg.type == null) {
					arg.type = taa.index; // key type
				}
				arg = arguments.get(1);
			}
			if (arg.type == null) {
				arg.type = taa.next; // value type
			}
			break;
		}

		case Tclass: {
			ad = ((TypeClass) tab).sym;
			// goto Laggr;
			/*
			 * Look for an int opApply(int delegate(inout Type [, ...]) dg);
			 * overload
			 */
			Dsymbol s = search_function(ad,
					(op == TOKforeach_reverse) ? Id.applyReverse : Id.apply,
					context);
			if (s != null) {
				fd = s.isFuncDeclaration();
				if (fd != null) {
					inferApplyArgTypesX(fd, arguments, context);
				}
			}
			break;
		}

		case Tstruct: {
			ad = ((TypeStruct) tab).sym;
			// goto Laggr;
			/*
			 * Look for an int opApply(int delegate(inout Type [, ...]) dg);
			 * overload
			 */
			Dsymbol s = search_function(ad,
					(op == TOKforeach_reverse) ? Id.applyReverse : Id.apply,
					context);
			if (s != null) {
				fd = s.isFuncDeclaration();
				if (fd != null) {
					inferApplyArgTypesX(fd, arguments, context);
				}
			}
			break;
		}

		case Tdelegate: {
			if (false && aggr.op == TOKdelegate) {
				DelegateExp de = (DelegateExp) aggr;

				fd = de.func.isFuncDeclaration();
				if (fd != null) {
					inferApplyArgTypesX(fd, arguments, context);
				}
			} else {
				inferApplyArgTypesY((TypeFunction) tab.next, arguments, context);
			}
			break;
		}

		default:
			break; // ignore error, caught later
		}
	}

	public static void inferApplyArgTypesX(FuncDeclaration fstart,
			List<Argument> arguments, SemanticContext context) {
		Declaration d;
		Declaration next;

		for (d = fstart; d != null; d = next) {
			FuncDeclaration f;
			FuncAliasDeclaration fa;
			AliasDeclaration a;

			fa = d.isFuncAliasDeclaration();
			if (fa != null) {
				inferApplyArgTypesX(fa.funcalias, arguments, context);
				next = fa.overnext;
			} else if ((f = d.isFuncDeclaration()) != null) {
				next = f.overnext;

				TypeFunction tf = (TypeFunction) f.type;
				if (inferApplyArgTypesY(tf, arguments, context)) {
					continue;
				}
				if (arguments.size() == 0) {
					return;
				}
			} else if ((a = d.isAliasDeclaration()) != null) {
				Dsymbol s = a.toAlias(context);
				next = s.isDeclaration();
				if (next == a) {
					break;
				}
				if (next == fstart) {
					break;
				}
			} else {
				d.error("is aliased to a function");
				break;
			}
		}
	}

	public static boolean inferApplyArgTypesY(TypeFunction tf,
			List<Argument> arguments, SemanticContext context) {
		int nparams;
		Argument p;

		if (Argument.dim(tf.parameters, context) != 1) {
			return true;
		}
		p = Argument.getNth(tf.parameters, 0, context);
		if (p.type.ty != Tdelegate) {
			return true;
		}
		tf = (TypeFunction) p.type.next;
		Assert.isTrue(tf.ty == Tfunction);

		/*
		 * We now have tf, the type of the delegate. Match it against the
		 * arguments, filling in missing argument types.
		 */
		nparams = Argument.dim(tf.parameters, context);
		if (nparams == 0 || tf.varargs != 0) {
			return true; // not enough parameters
		}
		if (arguments.size() != nparams) {
			return true; // not enough parameters
		}

		for (int u = 0; u < nparams; u++) {
			Argument arg = arguments.get(u);
			Argument param = Argument.getNth(tf.parameters, u, context);
			if (arg.type != null) {
				if (!arg.type.equals(param.type)) {
					/*
					 * Cannot resolve argument types. Indicate an error by
					 * setting the number of arguments to 0.
					 */
					arguments.clear();
					return false;
				}
				continue;
			}
			arg.type = param.type;
		}
		return false;
	}

	public static Expression resolveProperties(Scope sc, Expression e,
			SemanticContext context) {
		if (e.type != null) {
			Type t = e.type.toBasetype(context);

			if (t.ty == TY.Tfunction) {
				e = new CallExp(e.loc, e);
				e = e.semantic(sc, context);
			}

			/*
			 * Look for e being a lazy parameter; rewrite as delegate call
			 */
			else if (e.op == TOK.TOKvar) {
				VarExp ve = (VarExp) e;

				if ((ve.var.storage_class & STC.STClazy) != 0) {
					e = new CallExp(e.loc, e);
					e = e.semantic(sc, context);
				}
			}

			else if (e.op == TOK.TOKdotexp) {
				e.error("expression has no value");
			}
		}
		return e;
	}

	public static Dsymbol search_function(AggregateDeclaration ad,
			char[] funcid, SemanticContext context) {
		Dsymbol s;
		FuncDeclaration fd;
		TemplateDeclaration td;

		s = ad.search(Loc.ZERO, funcid, 0, context);
		if (s != null) {
			Dsymbol s2;

			s2 = s.toAlias(context);
			fd = s2.isFuncDeclaration();
			if (fd != null && fd.type.ty == TY.Tfunction) {
				return fd;
			}

			td = s2.isTemplateDeclaration();
			if (td != null) {
				return td;
			}
		}
		return null;
	}
	public int astFlags;
	public List<Comment> preDdocs;
	public List<Modifier> modifiers;

	public Comment postDdoc;

	/**
	 * Denotes a node created during the semantic pass, must be ignored by
	 * ASTConverter.
	 */
	public boolean synthetic;

	/**
	 * Denotes a node descarded during the semantic pass, must not be ignored by
	 * ASTConverter.
	 */
	public boolean discarded;

	public void accessCheck(Scope sc, Expression e, Declaration d, SemanticContext context) {
		if (e == null) {
			if (d.prot() == PROTprivate && d.getModule() != sc.module
					|| d.prot() == PROTpackage && !hasPackageAccess(sc, d)) {
				error("%s %s.%s is not accessible from %s", d.kind(), d
						.getModule().toChars(), d.toChars(), sc.module
						.toChars());
			}
		} else if (e.type.ty == Tclass) { // Do access check
			ClassDeclaration cd;

			cd = (((TypeClass) e.type).sym);
			if (e.op == TOKsuper) {
				ClassDeclaration cd2;

				cd2 = sc.func.toParent().isClassDeclaration();
				if (cd2 != null) {
					cd = cd2;
				}
			}
			cd.accessCheck(sc, d);
		} else if (e.type.ty == Tstruct) { // Do access check
			StructDeclaration cd;

			cd = (((TypeStruct) e.type).sym);
			cd.accessCheck(sc, d);
		}
	}

	public void addModifier(Modifier modifier) {
		if (modifiers == null) {
			modifiers = new ArrayList<Modifier>();
		}
		modifiers.add(modifier);
	}

	public void addModifiers(List<Modifier> someModifiers) {
		if (modifiers == null) {
			modifiers = new ArrayList<Modifier>();
		}
		modifiers.addAll(someModifiers);
	}

	public void argExpTypesToCBuffer(OutBuffer buf, List<Expression> arguments,
			HdrGenState hgs) {
		if (arguments != null) {
			OutBuffer argbuf = new OutBuffer();

			for (int i = 0; i < arguments.size(); i++) {
				Expression arg = arguments.get(i);

				if (i != 0) {
					buf.writeByte(',');
				}
				argbuf.reset();
				arg.type.toCBuffer2(argbuf, null, hgs);
				buf.write(argbuf);
			}
		}
	}

	public void argsToCBuffer(OutBuffer buf, List<Expression> arguments,
			HdrGenState hgs, SemanticContext context) {
		if (arguments != null) {
			for (int i = 0; i < arguments.size(); i++) {
				Expression arg = arguments.get(i);

				if (i != 0) {
					buf.writeByte(',');
				}
				expToCBuffer(buf, hgs, arg, PREC.PREC_assign, context);
			}
		}
	}

	public void arrayExpressionSemantic(List<Expression> exps, Scope sc,
			SemanticContext context) {
		if (exps != null) {
			for (int i = 0; i < exps.size(); i++) {
				Expression e = exps.get(i);

				e = e.semantic(sc, context);
				exps.set(i, e);
			}
		}
	}

	private Expression createTypeInfoArray(Scope sc, List<Expression> exps,
			int dim) {
		// TODO semantic
		return null;
	}

	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_OBJECT;
	}

	protected void error(String s) {
		throw new IllegalStateException("Problem reporting not implemented");
	}

	protected void error(String s, int... s2) {
		throw new IllegalStateException("Problem reporting not implemented");
	}

	protected void error(String s, String... s2) {
		throw new IllegalStateException("Problem reporting not implemented");
	}
	
	protected void error(String s, char[]... s2) {
		throw new IllegalStateException("Problem reporting not implemented");
	}

	public void expandTuples(List<Expression> exps) {
		if (exps != null) {
			for (int i = 0; i < exps.size(); i++) {
				Expression arg = exps.get(i);

				// Inline expand all the tuples
				while (arg.op == TOKtuple) {
					TupleExp te = (TupleExp) arg;

					exps.remove(i); // remove arg
					exps.addAll(i, te.exps); // replace with tuple contents
					if (i == exps.size()) {
						return; // empty tuple, no more arguments
					}
					arg = exps.get(i);
				}
			}
		}
	}

	public void expToCBuffer(OutBuffer buf, HdrGenState hgs, Expression e,
			PREC pr, SemanticContext context) {
		if (e.op.precedence.ordinal() < pr.ordinal()) {
			buf.writeByte('(');
			e.toCBuffer(buf, hgs, context);
			buf.writeByte(')');
		} else {
			e.toCBuffer(buf, hgs, context);
		}
	}

	protected void fatal() {
		throw new IllegalStateException("Problem reporting not implemented");
	}

	public boolean findCondition(List<String> ids, Identifier ident) {
		if (ids != null) {
			for (String id : ids) {
				if (id.equals(ident.string)) {
					return true;
				}
			}
		}

		return false;
	}

	public void functionArguments(Loc loc, Scope sc, TypeFunction tf,
			List<Expression> arguments, SemanticContext context) {
		int n;
		int done;
		Type tb;

		Assert.isNotNull(arguments);
		int nargs = arguments != null ? arguments.size() : 0;
		int nparams = Argument.dim(tf.parameters, context);

		if (nargs > nparams && tf.varargs == 0) {
			error("expected %zu arguments, not %zu", nparams, nargs);
		}

		n = (nargs > nparams) ? nargs : nparams; // n = max(nargs, nparams)

		done = 0;
		for (int i = 0; i < n; i++) {
			Expression arg;

			if (i < nargs) {
				arg = arguments.get(i);
			} else {
				arg = null;
			}

			if (i < nparams) {
				Argument p = Argument.getNth(tf.parameters, i, context);

				if (arg == null) {
					if (p.defaultArg == null) {
						if (tf.varargs == 2 && i + 1 == nparams) {
							// goto L2;
						}
						error("expected %zu arguments, not %zu", nparams, nargs);
						break;
					}
					arg = p.defaultArg.copy();
					arguments.add(arg);
					nargs++;
				}

				if (tf.varargs == 2 && i + 1 == nparams) {
					if (arg.implicitConvTo(p.type, context) != MATCH.MATCHnomatch) {
						if (nargs != nparams) {
							error("expected %zu arguments, not %zu", nparams,
									nargs);
						}
						// goto L1;
					}
					// L2:
					Type tb2 = p.type.toBasetype(context);
					Type tret = p.isLazyArray();
					switch (tb2.ty) {
					case Tsarray:
					case Tarray: { // Create a static array variable v of type
						// arg.type

						Identifier id = new Identifier(("_arrayArg" + (++idn)).toCharArray());
						Type t = new TypeSArray(tb2.next, new IntegerExp(loc, nargs
								- i));
						t = t.semantic(loc, sc, context);
						VarDeclaration v = new VarDeclaration(loc, t, id,
								new VoidInitializer(loc));
						v.semantic(sc, context);
						v.parent = sc.parent;

						Expression c = new DeclarationExp(loc, v);
						c.type = v.type;

						for (int u = i; u < nargs; u++) {
							Expression a = arguments.get(u);
							if (tret != null && !tb2.next.equals(a.type)) {
								a = a.toDelegate(sc, tret);
							}

							Expression e = new VarExp(loc, v);
							e = new IndexExp(loc, e, new IntegerExp(loc, u + 1 - nparams));
							e = new AssignExp(loc, e, a);
							if (c != null) {
								c = new CommaExp(loc, c, e);
							} else {
								c = e;
							}
						}
						arg = new VarExp(loc, v);
						if (c != null) {
							arg = new CommaExp(loc, c, arg);
						}
						break;
					}
					case Tclass: { /*
									 * Set arg to be: new Tclass(arg0, arg1,
									 * ..., argn)
									 */
						List<Expression> args = new ArrayList<Expression>(
								nargs - 1);
						for (int u = i; u < nargs; u++) {
							args.set(u - i, arguments.get(u));
						}
						arg = new NewExp(loc, null, null, p.type, args);
						break;
					}
					default:
						if (arg == null) {
							error("not enough arguments");
							return;
						}
						break;
					}
					arg = arg.semantic(sc, context);
					done = 1;
				}

				// L1: 
				if (!(p.inout == Lazy && p.type.ty == Tvoid)) {
					arg = arg.implicitCastTo(sc, p.type, context);
				}
				if (p.inout == Out || p.inout == InOut) {
					// BUG: should check that argument to inout is type
					// 'invariant'
					// BUG: assignments to inout should also be type 'invariant'
					arg = arg.modifiableLvalue(sc, null, context);

					// if (arg.op == TOKslice)
					// arg.error("cannot modify slice %s", arg.toChars());

					// Don't have a way yet to do a pointer to a bit in array
					if (arg.op == TOKarray
							&& arg.type.toBasetype(context).ty == Tbit) {
						error("cannot have out or inout argument of bit in array");
					}
				}

				// Convert static arrays to pointers
				tb = arg.type.toBasetype(context);
				if (tb.ty == Tsarray) {
					arg = arg.checkToPointer(context);
				}

				// Convert lazy argument to a delegate
				if (p.inout == Lazy) {
					arg = arg.toDelegate(sc, p.type);
				}
			} else {

				// If not D linkage, do promotions
				if (tf.linkage != LINKd) {
					// Promote bytes, words, etc., to ints
					arg = arg.integralPromotions(sc, context);

					// Promote floats to doubles
					switch (arg.type.ty) {
					case Tfloat32:
						arg = arg.castTo(sc, Type.tfloat64, context);
						break;

					case Timaginary32:
						arg = arg.castTo(sc, Type.timaginary64, context);
						break;
					}
				}

				// Convert static arrays to dynamic arrays
				tb = arg.type.toBasetype(context);
				if (tb.ty == Tsarray) {
					TypeSArray ts = (TypeSArray) tb;
					Type ta = tb.next.arrayOf(context);
					if (ts.size(arg.loc) == 0) {
						arg = new NullExp(arg.loc);
						arg.type = ta;
					} else {
						arg = arg.castTo(sc, ta, context);
					}
				}

				arg.rvalue(context);
			}
			arg = arg.optimize(WANTvalue);
			arguments.set(i, arg);
			if (done != 0) {
				break;
			}
		}

		// If D linkage and variadic, add _arguments[] as first argument
		if (tf.linkage == LINKd && tf.varargs == 1) {
			Expression e;
			e = createTypeInfoArray(sc, arguments.subList(nparams, arguments
					.size()
					- nparams), arguments.size() - nparams);
			arguments.add(0, e);
		}
	}

	public abstract int getNodeType();

	/***************************************************************************
	 * Determine if scope sc has package level access to s.
	 */
	public boolean hasPackageAccess(Scope sc, Dsymbol s) {

		for (; s != null; s = s.parent) {
			if (s.isPackage() != null && s.isModule() == null) {
				break;
			}
		}

		if (s != null && s == sc.module.parent) {
			return true;
		}

		return false;
	}

	/**
	 * Determine if 'this' is available. If it is, return the FuncDeclaration
	 * that has it.
	 */
	public FuncDeclaration hasThis(Scope sc) {
		FuncDeclaration fd;
		FuncDeclaration fdthis;

		fdthis = sc.parent.isFuncDeclaration();

		// Go upwards until we find the enclosing member function
		fd = fdthis;
		while (true) {
			if (fd == null) {
				// goto Lno;
				return null; // don't have 'this' available
			}
			if (!fd.isNested()) {
				break;
			}

			Dsymbol parent = fd.parent;
			while (parent != null) {
				TemplateInstance ti = parent.isTemplateInstance();
				if (ti != null) {
					parent = ti.parent;
				} else {
					break;
				}
			}

			fd = fd.parent.isFuncDeclaration();
		}

		if (fd.isThis() == null) {
			// goto Lno;
			return null; // don't have 'this' available
		}

		Assert.isNotNull(fd.vthis);
		return fd;
	}

	public void preFunctionArguments(Loc loc, Scope sc, List<Expression> exps,
			SemanticContext context) {
		if (exps != null) {
			expandTuples(exps);

			for (int i = 0; i < exps.size(); i++) {
				Expression arg = exps.get(i);

				if (arg.type == null) {
					arg.error("%s is not an expression", arg.toChars());
					arg = new IntegerExp(arg.loc, 0, Type.tint32);
				}

				arg = resolveProperties(sc, arg, context);
				exps.set(i, arg);
			}
		}
	}
	
	public boolean RealEquals(Real r1, Real r2) {
		return r1.equals(r2);
	}


	public String toChars() {
		return toString();
	}

	protected String toPrettyChars() {
		throw new IllegalStateException("Problem reporting not implemented");
	}
	
	public Expression op_overload(Scope sc) {
		// TODO semantic
		return null;
	}
	
	
	public final int getElementType() {
		return getNodeType();
	}

}
