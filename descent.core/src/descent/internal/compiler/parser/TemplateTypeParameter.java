package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

// DMD 1.020
public class TemplateTypeParameter extends TemplateParameter {

	public Type specType, sourceSpecType;
	public Type defaultType, sourceDefaultType;

	public TemplateTypeParameter(Loc loc, IdentifierExp ident, Type specType,
			Type defaultType) {
		super(loc, ident);
		this.specType = this.sourceSpecType = specType;
		this.defaultType = this.sourceDefaultType = defaultType;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, sourceSpecType);
			TreeVisitor.acceptChildren(visitor, sourceDefaultType);
		}
		visitor.endVisit(this);
	}

	@Override
	public void declareParameter(Scope sc, SemanticContext context) {
		TypeIdentifier ti = new TypeIdentifier(loc, ident);
		sparam = new AliasDeclaration(loc, ident, ti);
		
		// Descent
		((AliasDeclaration) sparam).isTemplateParameter = true;
		
		if (null == sc.insert(sparam)) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.ParameterMultiplyDefined, ident, new String[] { new String(ident.ident) }));
			}
		}
	}

	@Override
	public ASTDmdNode defaultArg(Scope sc, SemanticContext context) {
		Type t;

		t = defaultType;
		if (t != null) {
			t = t.syntaxCopy(context);
			t = t.semantic(loc, sc, context);
		}
		return t;
	}

	@Override
	public ASTDmdNode dummyArg(SemanticContext context) {
		Type t;

		if (specType != null) {
			t = specType;
		} else {
			// Use this for alias-parameter's too (?)
			t = new TypeIdentifier(loc, ident);
		}
		return t;
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_TYPE_PARAMETER;
	}

	@Override
	public TemplateTypeParameter isTemplateTypeParameter() {
		return this;
	}

	@Override
	public MATCH matchArg(Scope sc, Objects tiargs, int i,
			TemplateParameters parameters, Objects dedtypes,
			Declaration[] psparam, SemanticContext context) {
		Type t;
		ASTDmdNode oarg;
		MATCH m = MATCHexact;
		Type ta;

		if (i < size(tiargs)) {
			oarg = tiargs.get(i);
		} else { 
			// Get default argument instead
			oarg = defaultArg(sc, context);
			if (null == oarg) {
				assert (i < dedtypes.size());
				// It might have already been deduced
				oarg = dedtypes.get(i);
				if (null == oarg) {
					// goto Lnomatch;
					psparam = null;
					return MATCHnomatch;
				}
			}
		}

		ta = isType(oarg);
		if (null == ta) {
			// goto Lnomatch;
			psparam = null;
			return MATCHnomatch;
		}

		t = (Type) dedtypes.get(i);

		if (specType != null) {
			MATCH m2 = ta.deduceType(sc, specType, parameters, dedtypes,
					context);
			if (m2 == MATCHnomatch) {
				// goto Lnomatch;
				psparam = null;
				return MATCHnomatch;
			}

			if (m2.ordinal() < m.ordinal()) {
				m = m2;
			}
			t = (Type) dedtypes.get(i);
		} else {
			m = MATCHconvert;
			if (t != null) { // Must match already deduced type

				if (!t.equals(ta)) {
					// goto Lnomatch;
					psparam = null;
					return MATCHnomatch;
				}
			}
		}

		if (null == t) {
			dedtypes.set(i, ta);
			t = ta;
		}
		psparam[0] = new AliasDeclaration(loc, ident, t);
		
		// Descent
		((AliasDeclaration) psparam[0]).isTemplateParameter = true;
		
		return m;
	}

	@Override
	public int overloadMatch(TemplateParameter tp) {
		TemplateTypeParameter ttp = tp.isTemplateTypeParameter();

		if (ttp != null) {
			if (specType != ttp.specType) {
				return 0;
			}

			if (specType != null && !specType.equals(ttp.specType)) {
				return 0;
			}

			return 1; // match
		}

		return 0;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (specType != null) {
			specType = specType.semantic(loc, sc, context);
		}
	}

	@Override
	public ASTDmdNode specialization() {
		return specType;
	}

	@Override
	public TemplateParameter syntaxCopy(SemanticContext context) {
		TemplateTypeParameter tp = new TemplateTypeParameter(loc, ident,
				specType, defaultType);
		if (tp.specType != null) {
			tp.specType = specType.syntaxCopy(context);
		}
		if (defaultType != null) {
			tp.defaultType = defaultType.syntaxCopy(context);
		}
		return tp;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(ident.toChars());
		if (specType != null) {
			buf.writestring(" : ");
			specType.toCBuffer(buf, null, hgs, context);
		}
		if (defaultType != null) {
			buf.writestring(" = ");
			defaultType.toCBuffer(buf, null, hgs, context);
		}
	}
	@Override
	public void appendSignature(StringBuilder sb) {
		sb.append(ISignatureConstants.TEMPLATE_TYPE_PARAMETER);
		if (specType != null) {
			sb.append(ISignatureConstants.TEMPLATE_TYPE_PARAMETER2);
			specType.appendSignature(sb);
		}
	}
	
	@Override
	public char[] getDefaultValue() {
		if (defaultType == null) {
			return null;
		}
		return defaultType.getSignature().toCharArray();
	}

}
