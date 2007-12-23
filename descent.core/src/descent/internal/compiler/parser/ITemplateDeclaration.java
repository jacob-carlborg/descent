package descent.internal.compiler.parser;

import java.util.List;

public interface ITemplateDeclaration extends IScopeDsymbol {
	
	IFuncDeclaration deduce(Scope sc, Loc loc, Objects targsi, Expressions fargs, SemanticContext context);
	
	ITemplateDeclaration overroot();
	
	ITemplateDeclaration overnext();
	
	IDsymbol onemember();
	
	int leastAsSpecialized(ITemplateDeclaration td2, SemanticContext context);
	
	MATCH matchWithInstance(TemplateInstance ti, Objects dedtypes,
			int flag, SemanticContext context);
	
	TemplateParameters parameters();
	
	TemplateTupleParameter isVariadic();
	
	void declareParameter(Scope sc, TemplateParameter tp, INode o,
			SemanticContext context);
	
	Scope scope();
	
	List<TemplateInstance> instances();

}
