package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

// DMD 1.020
public class TemplateAliasParameter extends TemplateParameter {

	public static Dsymbol sdummy = null;

	public Type specAliasT;
	public Type defaultAlias;
	public Dsymbol specAlias;

	public TemplateAliasParameter(Loc loc, IdentifierExp ident,
			Type specAliasT, Type defaultAlias) {
		super(loc, ident);
		this.specAliasT = specAliasT;
		this.defaultAlias = defaultAlias;

		this.specAlias = null;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, defaultAlias);
		}
		visitor.endVisit(this);
	}

	@Override
	public void declareParameter(Scope sc, SemanticContext context) {
		TypeIdentifier ti = new TypeIdentifier(loc, ident);
		sparam = new AliasDeclaration(loc, ident, ti);
		if (sc.insert(sparam) == null) {
			error(loc, "parameter '%s' multiply defined", ident
					.toChars());
		}
	}

	@Override
	public ASTDmdNode defaultArg(Scope sc, SemanticContext context) {
		Dsymbol s = null;

		if (defaultAlias != null) {
			s = defaultAlias.toDsymbol(sc, context);
			if (null == s) {
				error("%s is not a symbol", defaultAlias.toChars(context));
			}
		}
		return s;
	}

	@Override
	public ASTDmdNode dummyArg(SemanticContext context) {
		Dsymbol s;

		s = specAlias;
		if (null == s) {
			if (null == sdummy) {
				sdummy = new Dsymbol();
			}
			s = sdummy;
		}
		return s;
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_ALIAS_PARAMETER;
	}

	@Override
	public TemplateAliasParameter isTemplateAliasParameter() {
		return this;
	}

	@Override
	public MATCH matchArg(Scope sc, List<ASTDmdNode> tiargs, int i,
			List<TemplateParameter> parameters, List<ASTDmdNode> dedtypes,
			Declaration[] psparam, SemanticContext context) {
		Dsymbol sa;
		ASTDmdNode oarg;

		if (i < tiargs.size()) {
			oarg = tiargs.get(i);
		} else { // Get default argument instead
			oarg = defaultArg(sc, context);
			if (oarg == null) {
				if (i >= dedtypes.size()) {
					throw new IllegalStateException("assert(i < dedtypes.dim);");
				}
				// It might have already been deduced
				oarg = dedtypes.get(i);
				if (null == oarg) {
					// goto Lnomatch;
					psparam[0] = null;
					return MATCHnomatch;
				}
			}
		}

		sa = getDsymbol(oarg, context);
		if (null == sa) {
			// goto Lnomatch;
			psparam = null;
			return MATCHnomatch;
		}

		if (specAlias != null) {
			if (null == sa || sa == sdummy) {
				// goto Lnomatch;
				psparam = null;
				return MATCHnomatch;
			}
			if (sa != specAlias) {
				// goto Lnomatch;
				psparam = null;
				return MATCHnomatch;
			}
		} else if (dedtypes.get(i) != null) { // Must match already deduced symbol
			Dsymbol s = (Dsymbol) dedtypes.get(i);

			if (null == sa || s != sa) {
				// goto Lnomatch;
				psparam = null;
				return MATCHnomatch;
			}
		}
		dedtypes.set(i, sa);

		psparam[0] = new AliasDeclaration(loc, ident, sa);
		return MATCHexact;
	}

	@Override
	public int overloadMatch(TemplateParameter tp) {
		TemplateAliasParameter tap = tp.isTemplateAliasParameter();

		if (tap != null) {
			if (specAlias != tap.specAlias) {
				return 0;
			}

			return 1; // match
		}
		return 0;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (specAliasT != null) {
			specAlias = specAliasT.toDsymbol(sc, context);
			if (specAlias == null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.SymbolNotFound, 0, specAliasT.start,
						specAliasT.length,
						new String[] { specAliasT.toString() }));
			}
		}
	}

	@Override
	public ASTDmdNode specialization() {
		return specAliasT;
	}

	@Override
	public TemplateParameter syntaxCopy() {
		TemplateAliasParameter tp = new TemplateAliasParameter(loc, ident,
				specAliasT, defaultAlias);
		if (tp.specAliasT != null) {
			tp.specAliasT = specAliasT.syntaxCopy();
		}
		if (defaultAlias != null) {
			tp.defaultAlias = defaultAlias.syntaxCopy();
		}
		return tp;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("alias ");
		buf.writestring(ident.toChars());
		if (specAliasT != null) {
			buf.writestring(" : ");
			specAliasT.toCBuffer(buf, null, hgs, context);
		}
		if (defaultAlias != null) {
			buf.writestring(" = ");
			defaultAlias.toCBuffer(buf, null, hgs, context);
		}
	}

}
