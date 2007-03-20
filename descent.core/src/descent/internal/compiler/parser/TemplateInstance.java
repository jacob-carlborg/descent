package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;


public class TemplateInstance extends ScopeDsymbol {
	
	public List<IdentifierExp> idents;
	public List<ASTNode> tiargs;
	public TemplateDeclaration tempdecl;	// referenced by foo.bar.abc
	public TemplateInstance inst;			// refer to existing instance
	public AliasDeclaration aliasdecl;		// != null if instance is an alias for its
	public boolean semanticdone; 			// has semantic() been done?

	public TemplateInstance(IdentifierExp id) {
		super(null);
		this.idents = new ArrayList<IdentifierExp>(3);
		this.idents.add(id);
	}
	
	@Override
	public Dsymbol toAlias(SemanticContext context) {
		if (inst == null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					"Cannot resolve forward reference",
					IProblem.ForwardReference, 0, start, length));
			return this;
		}

		if (inst != this)
			return inst.toAlias(context);

		if (aliasdecl != null)
			return aliasdecl.toAlias(context);

		return inst;
	}
	
	@Override
	public TemplateInstance isTemplateInstance() {
		return this;
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_INSTANCE;
	}
	
}