package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class ConditionalStatement extends Statement {
	
	public final Condition condition;
	public final Statement ifbody;
	public final Statement elsebody;

	public ConditionalStatement(Loc loc, Condition condition, Statement ifbody, Statement elsebody) {
		super(loc);
		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;		
	}
	
	@Override
	public int getNodeType() {
		return CONDITIONAL_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, ifbody);
			TreeVisitor.acceptChildren(visitor, elsebody);
		}
		visitor.endVisit(this);
	}


}
