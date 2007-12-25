package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.STC.STCstatic;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ASTDmdNode.Match;
import static descent.internal.compiler.parser.PROT.PROTpackage;
import static descent.internal.compiler.parser.PROT.PROTpublic;

/**
 * Groups a bunch of methods that are shared amongst the source hierarchy
 * and resolved hierarchy.
 */
public class SemanticMixin {
	
	public static boolean equals(IAggregateDeclaration c1, IAggregateDeclaration c2) {
		if (c1 == null && c2 == null) {
			return true;
		}
		if ((c1 == null) != (c2 == null)) {
			return false;
		}
		
		return c1.type().getSignature().equals(c2.type().getSignature());
	}
	
	public static void accessCheck(IAggregateDeclaration aThis, Scope sc, IDsymbol smember, SemanticContext context, INode reference) {
		boolean result;

		FuncDeclaration f = sc.func;
		IAggregateDeclaration cdscope = sc.getStructClassScope();
		PROT access;

		IDsymbol smemberparent = smember.toParent();
		if (smemberparent == null
				|| smemberparent.isAggregateDeclaration() == null) {
			return; // then it is accessible
		}

		// BUG: should enable this check
		// assert(smember.parent.isBaseOf(this, NULL));

		// TODO don't do reference comparison
		if (smemberparent instanceof IAggregateDeclaration && equals((IAggregateDeclaration) smemberparent, aThis)) {
			PROT access2 = smember.prot();

			result = access2.level >= PROTpublic.level || aThis.hasPrivateAccess(f)
					|| aThis.isFriendOf(cdscope)
					|| (access2 == PROTpackage && ASTDmdNode.hasPackageAccess(sc, aThis));
		} else if ((access = aThis.getAccess(smember)).level >= PROTpublic.level) {
			result = true;
		} else if (access == PROTpackage && ASTDmdNode.hasPackageAccess(sc, aThis)) {
			result = true;
		} else {
			result = ASTDmdNode.accessCheckX(smember, f, aThis, cdscope);
		}
		if (!result) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.MemberIsNotAccessible, reference, new String[] { smember.toChars(context) }));
		}
	}
	
	public static void checkDeprecated(IDsymbol aThis, Scope sc, SemanticContext context) {
		if (!context.global.params.useDeprecated && aThis.isDeprecated()) {
			// Don't complain if we're inside a deprecated symbol's scope
			for (IDsymbol sp = sc.parent; sp != null; sp = sp.parent()) {
				if (sp.isDeprecated()) {
					return;
				}
			}

			for (; sc != null; sc = sc.enclosing) {
				if (sc.scopesym != null && sc.scopesym.isDeprecated()) {
					return;
				}
			}

			context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolIsDeprecated, aThis, new String[] { aThis.toChars(context) }));
		}
	}
	
	public static IFuncDeclaration overloadResolve(IFuncDeclaration aThis, Expressions arguments, SemanticContext context, ASTDmdNode caller) {
		TypeFunction tf;
		Match m = new Match();
		m.last = MATCHnomatch;
		ASTDmdNode.overloadResolveX(m, aThis, arguments, context);

		if (m.count == 1) // exactly one match
		{
			return m.lastf;
		} else {
			OutBuffer buf = new OutBuffer();

			if (arguments != null) {
				HdrGenState hgs = new HdrGenState();

				ASTDmdNode.argExpTypesToCBuffer(buf, arguments, hgs, context);
			}

			if (m.last == MATCHnomatch) {
				tf = (TypeFunction) aThis.type();

				context.acceptProblem(Problem.newSemanticTypeError(IProblem.ParametersDoesNotMatchParameterTypes, caller, new String[] { aThis.kindForError(context) + Argument.argsTypesToChars(tf.parameters, tf.varargs, context), buf.toChars() }));
				return m.anyf; // as long as it's not a FuncAliasDeclaration
			} else {
				TypeFunction t1 = (TypeFunction) m.lastf.type();
				TypeFunction t2 = (TypeFunction) m.nextf.type();

				context.acceptProblem(Problem.newSemanticTypeError(IProblem.CalledWithArgumentTypesMatchesBoth, caller, new String[] { buf.toChars(), m.lastf.toPrettyChars(context), Argument
						.argsTypesToChars(t1.parameters, t1.varargs,
								context), m.nextf
						.toPrettyChars(context), Argument
						.argsTypesToChars(t2.parameters, t2.varargs,
								context) }));
				return m.lastf;
			}
		}
	}
	
	public static boolean isBaseOf(IInterfaceDeclaration aThis, IClassDeclaration cd, int[] poffset, SemanticContext context) {
		int j;
		
		for (j = 0; j < cd.interfaces().size(); j++) {
			BaseClass b = cd.interfaces().get(j);

			if (SemanticMixin.equals(aThis, b.base)) {
				if (poffset != null) {
					poffset[0] = b.offset;
					if (j != 0 && cd.isInterfaceDeclaration() != null) {
						poffset[0] = ClassDeclaration.OFFSET_RUNTIME;
					}
				}
				return true;
			}
			if (aThis.isBaseOf(b, poffset)) {
				if (j != 0 && poffset != null
						&& cd.isInterfaceDeclaration() != null) {
					poffset[0] = ClassDeclaration.OFFSET_RUNTIME;
				}
				return true;
			}
		}

		if (cd.baseClass() != null && aThis.isBaseOf(cd.baseClass(), poffset, context)) {
			return true;
		}

		if (poffset != null) {
			poffset[0] = 0;
		}
		return false;
	}
	
	public static boolean isBaseOf(IInterfaceDeclaration aThis, BaseClass bc, int[] poffset) {
		for (int j = 0; j < bc.baseInterfaces.size(); j++) {
			BaseClass b = bc.baseInterfaces.get(j);

			if (equals(aThis, b.base)) {
				if (poffset != null) {
					poffset[0] = b.offset;
				}
				return true;
			}
			if (aThis.isBaseOf(b, poffset)) {
				return true;
			}
		}
		if (poffset != null) {
			poffset[0] = 0;
		}
		return false;
	}
	
	public static IModule getModule(IDsymbol aThis) {
		IModule m;
		IDsymbol s;

		s = aThis;
		while (s != null) {
			m = s.isModule();
			if (m != null) {
				return m;
			}
			s = s.parent();
		}
		return null;
	}
	
	public static IAggregateDeclaration isThis(IFuncDeclaration aThis) {
		IAggregateDeclaration ad;

		ad = null;
		if ((aThis.storage_class() & STCstatic) == 0) {
			ad = aThis.isMember2();
		}
		return ad;
	}
	
	public static String toChars(IModuleDeclaration aThis, SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		if (aThis.packages() != null && aThis.packages().size() > 0) {
			for (int i = 0; i < aThis.packages().size(); i++) {
				IdentifierExp pid = aThis.packages().get(i);
				buf.writestring(pid.toChars());
				buf.writeByte('.');
			}
		}
		buf.writestring(aThis.id().toChars());
		return buf.extractData();
	}
	
	public static String toChars(IDsymbol aThis, SemanticContext context) {
		return (aThis.ident() != null && aThis.ident().ident != null) ? aThis.ident().toChars() : "__anonymous";
	}

}
