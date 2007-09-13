package dtool.dom.definitions;

import java.util.List;

import descent.internal.compiler.parser.UnionDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.IScope;

/**
 * A definition of a struct aggregate.
 */
public class DefinitionUnion extends DefinitionAggregate {

	public TemplateParameter[] templateParams; 
	
	
	public DefinitionUnion(UnionDeclaration elem) {
		super(elem);
		if(elem.members != null)
			this.members = DescentASTConverter.convertManyL(elem.members, this.members);
		// TODO: where did template Parameters go
		//if(elem.templateParameters != null)
		//	this.templateParams = TemplateParameter.convertMany(elem.templateParameters);
	}
	
	@Override	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		acceptNodeChildren(visitor, children);
		visitor.endVisit(this);
	}

	public List<IScope> getSuperScopes() {
		return null;
	}
}
