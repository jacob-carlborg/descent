package dtool.dom.definitions;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.EnumDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.expressions.Expression;
import dtool.model.IScope;

public class DefinitionEnum extends Definition implements IScope {

	public static class EnumMember extends DefUnit {
		
		public Expression value;

		public EnumMember(descent.internal.core.dom.EnumMember elem) {
			convertDsymbol(elem);
			this.value = Expression.convert(elem.value);
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChild(visitor, defname);
				TreeVisitor.acceptChild(visitor, value);
			}
			visitor.endVisit(this);	 			
		}

		@Override
		public EArcheType getArcheType() {
			return EArcheType.Enum;
		}

		@Override
		public IScope getMembersScope() {
			return getType().getTargetScope();
		}

		private Entity getType() {
			return ((DefinitionEnum) getParent()).type;
		}

	}

	private List<DefUnit> members;
	private Entity type;
	
	public DefinitionEnum(EnumDeclaration elem) {
		convertDsymbol(elem);
		this.members = DescentASTConverter.convertManyL(elem.members, this.members) ;
		this.type = Entity.convertType(elem.type);  
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);	

	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}

	@Override
	public IScope getMembersScope() {
		return this;
	}
	
	public List<DefUnit> getDefUnits() {
		return members;
	}
	
	
	public List<IScope> getSuperScopes() {
		// TODO Auto-generated method stub
		return null;
	}
}
