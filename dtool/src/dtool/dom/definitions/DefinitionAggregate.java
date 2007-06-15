package dtool.dom.definitions;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.AggregateDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.BaseEntityRef;
import dtool.dom.base.Entity;
import dtool.dom.statements.IStatement;
import dtool.model.EntityResolver;
import dtool.model.IScope;

/**
 * A definition of a aggregate. TODO.
 */
public class DefinitionAggregate extends Definition implements IScope, IStatement {

	public static class BaseClass extends ASTNeoNode{
		
		public int prot;
		public BaseEntityRef.TypeConstraint type;
		
		public BaseClass(descent.internal.core.dom.BaseClass elem) {
			convertNode(elem);
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
		convertDsymbol(elem);
		this.members = DescentASTConverter.convertManyL(elem.members, this.members);
		this.baseClasses = DescentASTConverter.convertManyL(elem.baseClasses, this.baseClasses);
		if(elem.templateParameters != null)
		this.templateParameters = TemplateParameter.convertMany(elem.templateParameters);
	}
	
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}
	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParameters);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getMembersScope() {
		return this;
	}
	
	public List<DefUnit> getDefUnits() {
		return EntityResolver.getDefUnitsFromMembers(members);
	}

	public IScope getSuperScope() {
		// TODO: allow multiple scopes
		if(baseClasses.size() > 0)
			return baseClasses.get(0).type.entity.getTargetScope();
		else 
			return null;
	}
}
