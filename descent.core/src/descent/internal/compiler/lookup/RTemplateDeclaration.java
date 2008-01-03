package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.ISourceReference;
import descent.core.IType;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.INode;
import descent.internal.compiler.parser.ITemplateDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.MATCH;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateParameter;
import descent.internal.compiler.parser.TemplateParameters;
import descent.internal.compiler.parser.TemplateTupleParameter;

/*
 * For now, a quick and dirty solution: parse the template and forward the
 * calls to it.
 * TODO: improve this if the template is simple, or is a class or template
 * function.
 */
public class RTemplateDeclaration extends RScopeDsymbol implements ITemplateDeclaration {
	
	private TemplateDeclaration temp;

	public RTemplateDeclaration(IType element, SemanticContext context) {
		super(element, context);
	}

	public void declareParameter(Scope sc, TemplateParameter tp, INode o, SemanticContext context) {
		materialize();
		temp.declareParameter(sc, tp, o, context);
	}

	public IFuncDeclaration deduce(Scope sc, Loc loc, Objects targsi, Expressions fargs, SemanticContext context) {
		materialize();
		return temp.deduce(sc, loc, targsi, fargs, context);
	}

	public List<TemplateInstance> instances() {
		materialize();
		return temp.instances();
	}

	public TemplateTupleParameter isVariadic() {
		materialize();
		return temp.isVariadic();
	}

	public int leastAsSpecialized(ITemplateDeclaration td2, SemanticContext context) {
		materialize();
		return temp.leastAsSpecialized(td2, context);
	}

	public MATCH matchWithInstance(TemplateInstance ti, Objects dedtypes, int flag, SemanticContext context) {
		materialize();
		return temp.matchWithInstance(ti, dedtypes, flag, context);
	}
	
	@Override
	public Dsymbols members() {
		materialize();
		return temp.members();
	}

	public IDsymbol onemember() {
		materialize();
		return temp.onemember();
	}

	public ITemplateDeclaration overnext() {
		// TODO this is wrong!
		materialize();
		return temp.overnext();
	}

	public ITemplateDeclaration overroot() {
		// TODO this is wrong!
		materialize();
		return temp.overroot();
	}

	public TemplateParameters parameters() {
		materialize();
		return temp.parameters();
	}

	public Scope scope() {
		materialize();
		return temp.scope();
	}
	
	@Override
	public ITemplateDeclaration isTemplateDeclaration() {
		return this;
	}
	
	private void materialize() {
		if (temp == null) {
			temp = (TemplateDeclaration) ((RModule) getModule()).materialize((ISourceReference) element);
		}
	}

}
