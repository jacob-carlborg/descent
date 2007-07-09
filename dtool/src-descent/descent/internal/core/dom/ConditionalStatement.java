package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IDebugStatement;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IDescentStatement;
import descent.core.dom.IStaticIfStatement;
import descent.core.dom.IVersionStatement;
import descent.core.domX.IASTVisitor;

public class ConditionalStatement extends Statement implements IStaticIfStatement, IDebugStatement, IVersionStatement {

	public final Condition condition;
	public final Statement ifbody;
	public final Statement elsebody;

	public ConditionalStatement(Condition condition, Statement ifbody, Statement elsebody) {
		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;
	}
	
	public IExpression getCondition() {
		return ((StaticIfCondition) condition).exp;
	}
	
	public IDescentStatement getThen() {
		return ifbody;
	}
	
	public IDescentStatement getElse() {
		return elsebody;
	}
	
	public int getElementType() {
		switch(this.condition.getConditionType()) {
		case Condition.DEBUG: return ElementTypes.DEBUG_STATEMENT;
		case Condition.VERSION: return ElementTypes.VERSION_STATEMENT;
		case Condition.STATIC_IF: return ElementTypes.STATIC_IF_STATEMENT;
		}
		return 0;
	}
	
	public IName getDebug() {
		return ((DebugCondition) condition).id;
	}
	
	public IName getVersion() {
		return ((VersionCondition) condition).id;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children;
		switch(this.condition.getConditionType()) {
		case Condition.DEBUG: 
			children = visitor.visit((IDebugStatement) this);
			if (children) {
				TreeVisitor.acceptChild(visitor, ((DebugCondition) condition).id); 
				TreeVisitor.acceptChild(visitor, ifbody);
				TreeVisitor.acceptChild(visitor, elsebody);
			}
			visitor.endVisit((IDebugStatement) this);
			break;
		case Condition.VERSION:
			children = visitor.visit((IVersionStatement) this);
			if (children) {
				TreeVisitor.acceptChild(visitor, ((VersionCondition) condition).id);
				TreeVisitor.acceptChild(visitor, ifbody);
				TreeVisitor.acceptChild(visitor, elsebody);
			}
			visitor.endVisit((IVersionStatement) this);
			break;
		case Condition.STATIC_IF:
			children = visitor.visit((IStaticIfStatement) this);
			if (children) {
				TreeVisitor.acceptChild(visitor, ((StaticIfCondition) condition).exp);
				TreeVisitor.acceptChild(visitor, ifbody);
				TreeVisitor.acceptChild(visitor, elsebody);
			}
			visitor.endVisit((IStaticIfStatement) this);
			break;
		}
		
		
	}
	

}
