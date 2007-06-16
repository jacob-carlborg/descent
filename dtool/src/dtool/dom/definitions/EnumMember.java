package dtool.dom.definitions;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.dom.references.Entity;
import dtool.refmodel.IScope;

public class EnumMember extends DefUnit {
	
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
