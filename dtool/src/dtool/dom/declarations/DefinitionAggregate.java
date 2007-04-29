package dtool.dom.declarations;

import java.util.ArrayList;
import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.AggregateDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.model.IScope;

/**
 * A definition of a aggregate. TODO.
 */
public class DefinitionAggregate extends Definition implements IScope {

	public static class BaseClass extends ASTNeoNode{
		
		public int prot;
		public EntityConstrainedRef.TypeConstraint type;
		
		public BaseClass(descent.internal.core.dom.BaseClass elem) {
			setSourceRange(elem);
			this.prot = elem.prot;
			this.type = Entity.convertType(elem.type);
		}
		
		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChild(visitor, type);
			}
			visitor.endVisit(this);	 			
		}
	}
	
	public List<ASTNode> members; 
	public List<BaseClass> baseClasses;
	public TemplateParameter[] templateParameters; 
	
	
	@SuppressWarnings("unchecked")
	public DefinitionAggregate(AggregateDeclaration elem) {
		super(elem);
		this.members = DescentASTConverter.convertMany(elem.members, this.members);
		this.baseClasses = DescentASTConverter.convertMany(elem.baseClasses, this.baseClasses);
		this.templateParameters = TemplateParameter.convert(elem.templateParameters);
	}
	
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}
	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, members);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, templateParameters);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getScope() {
		return this;
	}
	
	@Override
	public List<DefUnit> getDefUnits() {
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for(ASTNode elem: members) {
			if(elem instanceof DefUnit)
				defunits.add((DefUnit)elem);
		}
		return defunits;
	}
}
