package dtool.dom.definitions;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.EnumDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.model.IScope;

public class DefinitionEnum extends Definition {

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
		public IScope getScope() {
			// TODO Auto-generated method stub
			return null;
		}


	}

	private List<EnumMember> members;
	
	public DefinitionEnum(EnumDeclaration elem) {
		convertDsymbol(elem);
		this.members = DescentASTConverter.convertMany(elem.members, this.members) ;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);	

	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}

	@Override
	public IScope getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
