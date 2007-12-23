package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.IType;
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
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateParameter;
import descent.internal.compiler.parser.TemplateParameters;
import descent.internal.compiler.parser.TemplateTupleParameter;

public class RTemplateDeclaration extends RScopeDsymbol implements ITemplateDeclaration {

	public RTemplateDeclaration(IType element) {
		super(element);
	}

	public void declareParameter(Scope sc, TemplateParameter tp, INode o, SemanticContext context) {
		// TODO Auto-generated method stub
		
	}

	public IFuncDeclaration deduce(Scope sc, Loc loc, Objects targsi, Expressions fargs, SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TemplateInstance> instances() {
		// TODO Auto-generated method stub
		return null;
	}

	public TemplateTupleParameter isVariadic() {
		// TODO Auto-generated method stub
		return null;
	}

	public int leastAsSpecialized(ITemplateDeclaration td2, SemanticContext context) {
		// TODO Auto-generated method stub
		return 0;
	}

	public MATCH matchWithInstance(TemplateInstance ti, Objects dedtypes, int flag, SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDsymbol onemember() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITemplateDeclaration overnext() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITemplateDeclaration overroot() {
		// TODO Auto-generated method stub
		return null;
	}

	public TemplateParameters parameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public Scope scope() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ITemplateDeclaration isTemplateDeclaration() {
		return this;
	}

}
