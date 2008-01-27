package descent.internal.compiler.parser;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ASTDmdNode.Match;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.PROT.PROTnone;
import static descent.internal.compiler.parser.PROT.PROTpackage;
import static descent.internal.compiler.parser.PROT.PROTpublic;

import static descent.internal.compiler.parser.STC.STCconst;
import static descent.internal.compiler.parser.STC.STCstatic;

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
	
	public static void checkDeprecated(IDsymbol aThis, Scope sc, SemanticContext context, INode reference) {
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

			context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolIsDeprecated, reference, new String[] { aThis.toChars(context) }));
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
	
	public static IDsymbol pastMixin(IDsymbol aThis) {
		IDsymbol s = aThis;
		while (s != null && s.isTemplateMixin() != null) {
			s = s.parent();
		}
		return s;
	}
	
	public static IDsymbol searchX(IDsymbol aThis, Loc loc, Scope sc, IdentifierExp id, SemanticContext context) {
		IDsymbol s = aThis.toAlias(context);
		IDsymbol sm;

		switch (id.dyncast()) {
		case DYNCAST_IDENTIFIER:
			sm = s.search(loc, id, 0, context);
			break;

		case DYNCAST_DSYMBOL: { // It's a template instance
			Dsymbol st = ((TemplateInstanceWrapper) id).tempinst;
			TemplateInstance ti = st.isTemplateInstance();
			id = ti.name;
			sm = s.search(loc, id, 0, context);
			if (null == sm) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.TemplateIdentifierIsNotAMemberOf, aThis, new String[] { id.toChars(), s.kind(), s.toChars(context) }));
				return null;
			}
			sm = sm.toAlias(context);
			ITemplateDeclaration td = sm.isTemplateDeclaration();
			if (null == td) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.SymbolIsNotATemplate, aThis, new String[] { id.toChars(), sm.kind() }));
				return null;
			}
			ti.tempdecl = td;
			if (0 == ti.semanticdone) {
				ti.semantic(sc, context);
			}
			sm = ti.toAlias(context);
			break;
		}

		default:
			throw new IllegalStateException("assert(0);");
		}
		return sm;
	}
	
	public static void toCBuffer(IDsymbol aThis, OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(aThis.toChars(context));
	}
	
	public static IDsymbol toParent(IDsymbol aThis) {
		return aThis.parent() != null ? aThis.parent().pastMixin() : null;
	}
	
	public static IDsymbol toParent2(IDsymbol aThis) {
		IDsymbol s = aThis.parent();
		while (s != null && s.isTemplateInstance() != null) {
			s = s.parent();
		}
		return s;
	}
	
	public static boolean oneMember(IDsymbol aThis, IDsymbol[] ps, SemanticContext context) {
		ps[0] = aThis;
		return true;
	}
	
	public static PROT getAccess(IClassDeclaration aThis, IDsymbol smember) {
		PROT access_ret = PROTnone;

		IDsymbol p = smember.toParent();
		if (p != null && p.isAggregateDeclaration() != null && equals(p.isAggregateDeclaration(), aThis)) {
			access_ret = smember.prot();
		} else {
			PROT access;
			int i;

			if (smember.isDeclaration().isStatic()) {
				access_ret = smember.prot();
			}

			for (i = 0; i < aThis.baseclasses().size(); i++) {
				BaseClass b = aThis.baseclasses().get(i);

				access = b.base.getAccess(smember);
				switch (access) {
				case PROTnone:
					break;

				case PROTprivate:
					access = PROTnone; // private members of base class not
					// accessible
					break;

				case PROTpackage:
				case PROTprotected:
				case PROTpublic:
				case PROTexport:
					// If access is to be tightened
					if (b.protection.level < access.level) {
						access = b.protection;
					}

					// Pick path with loosest access
					if (access.level > access_ret.level) {
						access_ret = access;
					}
					break;

				default:
					Assert.isTrue(false);
				}
			}
		}
		return access_ret;
	}
	
	public static PROT getAccess(IStructDeclaration aThis, IDsymbol smember) {
		PROT access_ret = PROTnone;

		IDsymbol p = smember.toParent();
		if (p != null && p.isAggregateDeclaration() != null && equals(p.isAggregateDeclaration(), aThis)) {
			access_ret = smember.prot();
		} else if (smember.isDeclaration().isStatic()) {
			access_ret = smember.prot();
		}
		return access_ret;
	}
	
	public static IFuncDeclaration overloadExactMatch(IFuncDeclaration aThis, Type t, SemanticContext context) {
		IFuncDeclaration f;
		IDeclaration d;
		IDeclaration next;

		for (d = aThis; d != null; d = next) {
			FuncAliasDeclaration fa = d.isFuncAliasDeclaration();

			if (fa != null) {
				IFuncDeclaration f2 = fa.funcalias
						.overloadExactMatch(t, context);
				if (f2 != null) {
					return f2;
				}
				next = fa.overnext;
			} else {
				IAliasDeclaration a = d.isAliasDeclaration();

				if (a != null) {
					IDsymbol s = a.toAlias(context);
					next = s.isDeclaration();
					if (next == a) {
						break;
					}
				} else {
					f = d.isFuncDeclaration();
					if (f == null) {
						break; // BUG: should print error message?
					}
					if (t.equals(d.type())) {
						return f;
					}
					next = f.overnext();
				}
			}
		}
		return null;
	}
	
	public static boolean overrides(IFuncDeclaration aThis, IFuncDeclaration fd, SemanticContext context) {
		boolean result = false;

		if (ASTDmdNode.equals(fd.ident(), aThis.ident())) {
			int cov = aThis.type().covariant(fd.type(), context);
			if (cov != 0) {
				IClassDeclaration cd1 = aThis.toParent().isClassDeclaration();
				IClassDeclaration cd2 = fd.toParent().isClassDeclaration();

				if (cd1 != null && cd2 != null
						&& cd2.isBaseOf(cd1, null, context)) {
					result = true;
				}
			}
		}
		return result;
	}
	
	public static void alignmember(IAggregateDeclaration aThis, int salign, int size, int[] poffset) {
		if (salign > 1) {
			//int sa;

			switch (size) {
			case 1:
				break;
			case 2:
				//case_2:
				poffset[0] = (poffset[0] + 1) & ~1; // align to word
				break;
			case 3:
			case 4:
				if (salign == 2) {
					// goto case_2;
					poffset[0] = (poffset[0] + 1) & ~1; // align to word
				}
				poffset[0] = (poffset[0] + 3) & ~3; // align to dword
				break;
			default:
				poffset[0] = (poffset[0] + salign - 1) & ~(salign - 1);
				break;
			}
		}
	}
	
	public static boolean hasPrivateAccess(IAggregateDeclaration aThis, IDsymbol smember) {
		if (smember != null) {
			IAggregateDeclaration cd = null;
			IDsymbol smemberparent = smember.toParent();
			if (smemberparent != null) {
				cd = smemberparent.isAggregateDeclaration();
			}

			if (equals(aThis, cd)) { // smember is a member of this class
				return true; // so we get private access
			}

			// If both are members of the same module, grant access
			while (true) {
				IDsymbol sp = smember.toParent();
				if (sp.isFuncDeclaration() != null
						&& smember.isFuncDeclaration() != null) {
					smember = sp;
				} else {
					break;
				}
			}
			// TODO check reference comparison
			if (cd == null && aThis.toParent() == smember.toParent()) {
				return true;
			}
			if (cd == null && aThis.getModule() == smember.getModule()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isFriendOf(IAggregateDeclaration aThis, IAggregateDeclaration cd) {
		if (equals(aThis, cd)) {
			return true;
		}

		// Friends if both are in the same module
		// if (toParent() == cd->toParent())
		// TODO check reference comparison
		if (cd != null && aThis.getModule() == cd.getModule()) {
			return true;
		}

		return false;
	}
	
	public static IExpInitializer getExpInitializer(IVarDeclaration aThis, SemanticContext context) {
		IExpInitializer ei;

		if (aThis.init() != null) {
			ei = aThis.init().isExpInitializer();
		} else {
			Expression e = aThis.type().defaultInit(context);
			if (e != null) {
				ei = new ExpInitializer(aThis.loc(), e);
			} else {
				ei = null;
			}
		}
		return ei;
	}
	
	public static boolean isVirtual(IFuncDeclaration aThis, SemanticContext context) {
		return aThis.isMember() != null
				&& !(aThis.isStatic() || aThis.protection() == PROT.PROTprivate || aThis.protection() == PROT.PROTpackage)
				&& aThis.toParent().isClassDeclaration() != null;
	}
	
	public static boolean isAuto(IDeclaration aThis) {
		return (aThis.storage_class() & STC.STCauto) != 0;
	}
	
	public static boolean isConst(IDeclaration aThis) {
		return (aThis.storage_class() & STC.STCconst) != 0;
	}
	
	public static boolean isAbstract(IDeclaration aThis) {
		return (aThis.storage_class() & STC.STCabstract) != 0;
	}
	
	public static boolean isFinal(IDeclaration aThis) {
		return (aThis.storage_class() & STC.STCfinal) != 0;
	}
	
	public static boolean isCtorinit(IDeclaration aThis) {
		return (aThis.storage_class() & STC.STCctorinit) != 0;
	}
	
	public static boolean isScope(IDeclaration aThis) {
		return (aThis.storage_class() & (STC.STCscope | STC.STCauto)) != 0;
	}
	
	public static boolean isStatic(IDeclaration aThis) {
		return (aThis.storage_class() & (STC.STCstatic)) != 0;
	}
	
	public static boolean isParameter(IDeclaration aThis) {
		return (aThis.storage_class() & (STC.STCparameter)) != 0;
	}
	
	public static boolean isOut(IDeclaration aThis) {
		return (aThis.storage_class() & (STC.STCout)) != 0;
	}
	
	public static boolean isRef(IDeclaration aThis) {
		return (aThis.storage_class() & (STC.STCref)) != 0;
	}
	
	public static boolean isDataseg(IVarDeclaration aThis, SemanticContext context) {
		IDsymbol parent = aThis.toParent();
		if (parent == null && (aThis.storage_class() & (STCstatic | STCconst)) == 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CannotResolveForwardReference, aThis));
			aThis.type(Type.terror);
			return false;
		}
		return ((aThis.storage_class() & (STCstatic | STCconst)) != 0
				|| parent.isModule() != null || parent.isTemplateInstance() != null);
	}
	
	public static boolean isZeroInit(IStructDeclaration sd, SemanticContext context) {
		boolean zeroInit = true;
		for (int j = 0; j < sd.fields().size(); j++) {
			IDsymbol s = sd.fields().get(j);
			IVarDeclaration vd = s.isVarDeclaration();
			if (vd != null && !vd.isDataseg(context)) {
				if (vd.init() != null) {
					// Should examine init to see if it is really all 0's
					zeroInit = true;
					break;
				} else {
					if (!vd.type().isZeroInit(context)) {
						zeroInit = false;
						break;
					}
				}
			}
		}
		return zeroInit;
	}
	
	public static void checkNestedReference(IVarDeclaration aThis, Scope sc, Loc loc, SemanticContext context) {
		if (!aThis.isDataseg(context) && aThis.parent() != sc.parent && aThis.parent() != null) {
			IFuncDeclaration fdv = aThis.toParent().isFuncDeclaration();
			FuncDeclaration fdthis = (FuncDeclaration) sc.parent.isFuncDeclaration(); // SEMANTIC

			if (fdv != null && fdthis != null) {
				if (loc != null && loc.filename != null)
					fdthis.getLevel(loc, fdv, context);
				aThis.nestedref(1);
				fdv.nestedFrameRef(true);
			}
		}
	}
	
	public static String toString(Import node) {
		StringBuilder buffer = new StringBuilder();
		if (node.aliasId != null) {
			buffer.append(node.aliasId);
			buffer.append(" = ");
		}
		if (node.packages != null) {
			for(int i = 0; i < node.packages.size(); i++) {
				if (i > 0) {
					buffer.append('.');
				}
				buffer.append(node.packages.get(i));
			}
			buffer.append('.');
		}
		if (node.id != null) {
			buffer.append(node.id);
		}
		if (node.names != null) {
			buffer.append(" : ");
			for(int i = 0; i < node.names.size(); i++) {
				if (i > 0) {
					buffer.append(", ");
				}
				if (node.aliases.get(i) != null) {
					buffer.append(node.aliases.get(i));
					buffer.append(" = ");
				}
				buffer.append(node.names.get(i));
			}
		}
		return buffer.toString();
	}
	
	public static String getSignature(IDsymbol aThis) {
		if (aThis.effectiveParent() == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		appendSignature(aThis, sb);
		return sb.toString();
	}
	
	public static void appendSignature(IDsymbol aThis, StringBuilder sb) {
		IDsymbol parent = aThis.effectiveParent();
		if (parent == null) {
			return;
		}
		
		if (parent instanceof TemplateInstance) {
			TemplateInstance tempinst = (TemplateInstance) parent;
			ITemplateDeclaration tempdecl = tempinst.tempdecl;
			while(tempdecl.parent() instanceof TemplateInstance) {
				tempinst = (TemplateInstance) tempdecl.parent();
				tempdecl = tempinst.tempdecl;
			}
			tempdecl.parent().appendSignature(sb);
		} else if (aThis.templated()) {
			if (parent.effectiveParent() != null) {
				parent.effectiveParent().appendSignature(sb);	
			}
		} else {
			parent.appendSignature(sb);
		}
		appendNameSignature(aThis, sb);
	}
	
	public static void appendNameSignature(IDsymbol aThis, StringBuilder sb) {
		if (aThis.ident() == null || aThis.ident().ident == null || aThis.ident().ident.length == 0) {
			return;
		}
		
		if (isLocal(aThis)) {
			sb.append('$');
			sb.append(aThis.getStart());
		}
		
		if (aThis instanceof UnitTestDeclaration || 
			aThis instanceof InvariantDeclaration ||
			aThis instanceof IStaticCtorDeclaration ||
			aThis instanceof StaticDtorDeclaration) {
			
			// Simplified signature for this symbols
			sb.append('$');
			sb.append(aThis.getStart());
			
			sb.append(aThis.getSignaturePrefix());
			
		} else {
			
			if (!(aThis.parent() instanceof TemplateInstance)) {
				sb.append(aThis.getSignaturePrefix());
				sb.append(aThis.ident().ident.length);
				sb.append(aThis.ident().ident);
			}
			
			if (aThis instanceof IFuncDeclaration) {
				aThis.type().appendSignature(sb);
			}
			
			if (aThis instanceof TemplateDeclaration) {
				TemplateDeclaration temp = (TemplateDeclaration) aThis;
				if (temp.wrapper && temp.members.get(0) instanceof IFuncDeclaration) {
					temp.members.get(0).type().appendSignature(sb);
				}
			}
			
			if (aThis.parent() instanceof TemplateInstance) {
				appendTemplateInstanceSignature(aThis, sb);
			} else if (aThis.templated() && aThis.parent() instanceof ITemplateDeclaration) {
				ITemplateDeclaration tempdecl = (ITemplateDeclaration) aThis.parent();
				for(TemplateParameter param : tempdecl.parameters()) {
					param.appendSignature(sb);
				}
				sb.append(ISignatureConstants.TEMPLATE_PARAMETERS_BREAK);
			}
			
			if (aThis instanceof ITemplateDeclaration) {
				appendTemplateParameters((ITemplateDeclaration) aThis, sb);
			}
		}
	}
	
	private static void appendTemplateInstanceSignature(IDsymbol aThis, StringBuilder sb) {
		TemplateInstance tempinst = (TemplateInstance) aThis.parent();
		ITemplateDeclaration tempdecl = tempinst.tempdecl;
		if (tempdecl.parent() instanceof TemplateInstance) {
			appendTemplateInstanceSignature(tempdecl, sb);
		}
		
		sb.append(aThis.getSignaturePrefix());
		sb.append(tempinst.name.ident.length);
		sb.append(tempinst.name.ident);
		
		appendTemplateParameters(tempdecl, sb);
		
		tempinst.appendSignature(sb);
	}
	
	private static void appendTemplateParameters(ITemplateDeclaration tempdecl, StringBuilder sb) {
		for(TemplateParameter param : tempdecl.parameters()) {
			param.appendSignature(sb);
		}
		sb.append(ISignatureConstants.TEMPLATE_PARAMETERS_BREAK);
	}
	
	/*
	 * Determines if a symbol is local to a function.
	 */
	private static boolean isLocal(IDsymbol symbol) {
		return symbol.parent() instanceof IFuncDeclaration;
	}
	
	public static String mangle(IClassDeclaration aThis, SemanticContext context) {
		IDsymbol parentsave = aThis.parent();

		/* These are reserved to the compiler, so keep simple
		 * names for them.
		 */
		if (ASTDmdNode.equals(aThis.ident(), Id.Exception)) {
			if (aThis.parent().ident() != null
					&& ASTDmdNode.equals(aThis.parent().ident(), Id.object)) {
				aThis.parent(null);
			}
		} else if (ASTDmdNode.equals(aThis.ident(), Id.TypeInfo)
				||
				//		CharOperation.equals(ident.ident, Id.Exception) ||
				ASTDmdNode.equals(aThis.ident(), Id.TypeInfo_Struct)
				|| ASTDmdNode.equals(aThis.ident(), Id.TypeInfo_Class)
				|| ASTDmdNode.equals(aThis.ident(), Id.TypeInfo_Typedef)
				|| ASTDmdNode.equals(aThis.ident(), Id.TypeInfo_Tuple)
				|| aThis == context.ClassDeclaration_object 
				|| aThis == context.ClassDeclaration_classinfo
				|| aThis == context.Module_moduleinfo
				|| aThis.ident().toChars().startsWith("TypeInfo_")) {
			aThis.parent(null);
		}

		String id = Dsymbol_mangle(aThis, context);
		aThis.parent(parentsave);
		return id;
	}
	
	public static String Dsymbol_mangle(IDsymbol aThis, SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		String id;

		id = aThis.ident() != null ? aThis.ident().toChars() : aThis.toChars(context);
		if (aThis.parent() != null) {
			try {
				String p = aThis.parent().mangle(context);
				if (p.charAt(0) == '_' && p.charAt(1) == 'D') {
					p = p.substring(2);
				}
				buf.writestring(p);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		buf.data.append(id.length()).append(id);
		id = buf.toChars();
		buf.data = null;
		return id;
	}
	
	public static void importScope(IScopeDsymbol aThis, IScopeDsymbol s, PROT protection) {
		if (s != aThis) {
			if (aThis.imports() == null) {
				aThis.imports(new ArrayList<IScopeDsymbol>());
			} else {
				for (int i = 0; i < aThis.imports().size(); i++) {
					IScopeDsymbol ss;

					ss = aThis.imports().get(i);
					if (ss == s) {
						if (protection.ordinal() > aThis.prots().get(i).ordinal()) {
							aThis.prots().set(i, protection); // upgrade access
						}
						return;
					}
				}
			}
			aThis.imports().add(s);
			// TODO semantic check this translation
			// prots = (unsigned char *)mem.realloc(prots, imports.dim *
			// sizeof(prots[0]));
			// prots[imports.dim - 1] = protection;
			if (aThis.prots() == null) {
				aThis.prots(new Array<PROT>());
			}
			aThis.prots().set(ASTDmdNode.size(aThis.imports()) - 1, protection);
		}
	}
	
	public static IDsymbol search(IScopeDsymbol aThis, Loc loc, char[] ident, int flags, SemanticContext context) {
		IDsymbol s;
		int i;

		// Look in symbols declared in this module
		s = aThis.symtab() != null ? aThis.symtab().lookup(ident) : null;
		if (s != null) {
		} else if (aThis.imports() != null) {
			// Look in imported modules

			i = -1;
			for (IScopeDsymbol ss : aThis.imports()) {
				i++;
				IDsymbol s2;

				// If private import, don't search it
				if ((flags & 1) != 0 && aThis.prots().get(i) == PROT.PROTprivate) {
					continue;
				}

				s2 = ss.search(loc, ident, ss.isModule() != null ? 1 : 0, context);
				if (s == null) {
					s = s2;
				} else if (s2 != null && s != s2) {
					if (s.toAlias(context) == s2.toAlias(context)) {
						if (s.isDeprecated()) {
							s = s2;
						}
					} else {
						/*
						 * Two imports of the same module should be regarded as
						 * the same.
						 */
						Import i1 = s.isImport();
						Import i2 = s2.isImport();
						if (!(i1 != null && i2 != null && (i1.mod == i2.mod || (i1.parent()
								.isImport() == null
								&& i2.parent().isImport() == null && ASTDmdNode.equals(i1.ident(), i2.ident()))))) {
							ScopeDsymbol.multiplyDefined(loc, s, s2, context);
							break;
						}
					}
				}
			}
			if (s != null) {
				IDeclaration d = s.isDeclaration();
				if (d != null && d.protection() == PROT.PROTprivate
						&& d.parent().isTemplateMixin() == null) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.MemberIsPrivate, aThis, new String[] { new String(d.ident().ident) }));
				}
			}
		}
		return s;
	}

}
