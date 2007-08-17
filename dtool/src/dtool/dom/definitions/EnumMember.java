package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.dom.references.Reference;
import dtool.refmodel.IScopeNode;

public class EnumMember extends DefUnit {
	
	public Expression value;

	public EnumMember(descent.internal.compiler.parser.EnumMember elem) {
		super(elem);
		convertNode(elem.ident);
		this.value = Expression.convert(elem.value);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, value);
		}
		visitor.endVisit(this);	 			
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.EnumMember;
	}

	@Override
	public IScopeNode getMembersScope() {
		return getType().getTargetScope();
	}

	private Reference getType() {
		return ((DefinitionEnum) getParent()).type;
	}

}
