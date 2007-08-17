package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TemplateTupleParameter extends TemplateParameter {
	
	public TemplateTupleParameter(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		TypeIdentifier ti = new TypeIdentifier(loc, ident);
	    Declaration sparam = new AliasDeclaration(loc, ident, ti);
	    if (sc.insert(sparam) == null) {
	    	context.acceptProblem(Problem.newSemanticTypeError("Duplicate parameter " + ident, IProblem.DuplicatedParameter, 0, ident.start, ident.length));
	    }
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_TUPLE_PARAMETER;
	}

}
