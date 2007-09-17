package dtool.ast.definitions;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ClassDeclaration;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter;
import dtool.refmodel.IScope;

/**
 * A definition of a class aggregate.
 */
public class DefinitionClass extends DefinitionAggregate {

	public List<BaseClass> baseClasses;
	
	@SuppressWarnings("unchecked")
	public DefinitionClass(ClassDeclaration elem) {
		super(elem);
		if(elem.members != null)
			this.members = DescentASTConverter.convertManyL(elem.members, this.members);
		this.baseClasses = DescentASTConverter.convertManyL(elem.sourceBaseclasses, this.baseClasses);
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
	
	@Override
	protected void acceptNodeChildren(IASTNeoVisitor visitor, boolean children) {
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, members);
		}
	}

	public List<IScope> getSuperScopes() {
		if(baseClasses == null || baseClasses.size() < 0)
			return null;

		List<IScope> scopes = new ArrayList<IScope>();
		for(BaseClass baseclass: baseClasses) {
			DefUnit defunit = baseclass.type.findTargetDefUnit();
			if(defunit == null)
				continue;
			scopes.add(defunit.getMembersScope());
		}
		return scopes;
		// TODO add Object super scope.
	}

}
