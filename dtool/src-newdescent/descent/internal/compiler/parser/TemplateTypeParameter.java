package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.core.domX.IASTVisitor;

public class TemplateTypeParameter extends TemplateParameter {
	
	public Type specType;
	public Type defaultType;
	
	public TemplateTypeParameter(Loc loc, IdentifierExp ident, Type specType, Type defaultType) {
		super(loc, ident);
		this.specType = specType;
	    this.defaultType = defaultType;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, specType);
			TreeVisitor.acceptChildren(visitor, defaultType);
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

		if (specType != null) {
			specType = specType.semantic(loc, sc, context);
		}
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_TYPE_PARAMETER;
	}

}
