package dtool.ast.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;

public class EnumMember extends DefUnit {
	
	public Resolvable value;

	public EnumMember(descent.internal.compiler.parser.EnumMember elem) {
		super(elem);
		convertNode(elem);
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
