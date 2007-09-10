package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHexact;

// DMD 1.020
public class TemplateTupleParameter extends TemplateParameter {

	public TemplateTupleParameter(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	@Override
	public void declareParameter(Scope sc, SemanticContext context) {
		TypeIdentifier ti = new TypeIdentifier(loc, ident);
		sparam = new AliasDeclaration(loc, ident, ti);
		if (null == sc.insert(sparam)) {
			error(loc, "parameter '%s' multiply defined", ident
					.toChars());
		}
	}

	@Override
	public ASTDmdNode defaultArg(Scope sc, SemanticContext context) {
		return null;
	}

	@Override
	public ASTDmdNode dummyArg(SemanticContext context) {
		return null;
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_TUPLE_PARAMETER;
	}

	@Override
	public TemplateTupleParameter isTemplateTupleParameter() {
		return this;
	}

	@Override
	public MATCH matchArg(Scope sc, Objects tiargs, int i,
			TemplateParameters parameters, Objects dedtypes,
			Declaration[] psparam, SemanticContext context) {
		/* The rest of the actual arguments (tiargs[]) form the match
		 * for the variadic parameter.
		 */
		if (!(i + 1 == dedtypes.size())) {
			throw new IllegalStateException("assert (i + 1 == dedtypes.size());"); // must be the last one
		}
		Tuple ovar;
		if (i + 1 == tiargs.size() && isTuple(tiargs.get(i)) != null) {
			ovar = isTuple(tiargs.get(i));
		} else {
			ovar = new Tuple();
			if (i < tiargs.size()) {
				for (int j = 0; j < ovar.objects.size(); j++) {
					ovar.objects.set(j, tiargs.get(i + j));
				}
			}
		}
		psparam[0] = new TupleDeclaration(loc, ident, ovar.objects);
		dedtypes.set(i, ovar);
		return MATCHexact;
	}

	@Override
	public int overloadMatch(TemplateParameter tp) {
		TemplateTupleParameter tvp = tp.isTemplateTupleParameter();

		if (tvp != null) {
			return 1; // match
		}

		return 0;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {

	}
	
	@Override
	public ASTDmdNode specialization() {
		return null;
	}
	
	@Override
	public TemplateParameter syntaxCopy() {
		TemplateTupleParameter tp = new TemplateTupleParameter(loc, ident);
		return tp;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(ident.toChars());
	    buf.writestring("...");
	}

}
