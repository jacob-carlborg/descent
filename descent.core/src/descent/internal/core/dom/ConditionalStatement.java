package descent.internal.core.dom;

import descent.core.dom.IDebugStatement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.dom.IStaticIfStatement;
import descent.core.dom.IVersionStatement;

public class ConditionalStatement extends Statement implements IStaticIfStatement, IDebugStatement, IVersionStatement {

	private final Condition condition;
	private final Statement ifbody;
	private final Statement elsebody;

	public ConditionalStatement(Loc loc, Condition condition, Statement ifbody, Statement elsebody) {
		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;
	}
	
	public IExpression getCondition() {
		return ((StaticIfCondition) condition).exp;
	}
	
	public IStatement getThen() {
		return ifbody;
	}
	
	public IStatement getElse() {
		return elsebody;
	}
	
	public int getElementType() {
		switch(this.condition.getConditionType()) {
		case Condition.DEBUG: return DEBUG_STATEMENT;
		case Condition.VERSION: return VERSION_STATEMENT;
		case Condition.STATIC_IF: return STATIC_IF_STATEMENT;
		}
		return 0;
	}
	
	public IName getDebug() {
		return ((DebugCondition) condition).id;
	}
	
	public IName getVersion() {
		return ((VersionCondition) condition).id;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			switch(this.condition.getConditionType()) {
			case Condition.DEBUG: 
				acceptChild(visitor, ((DebugCondition) condition).id); 
				break;
			case Condition.VERSION:
				acceptChild(visitor, ((VersionCondition) condition).id); 
				break;
			case Condition.STATIC_IF:
				acceptChild(visitor, ((StaticIfCondition) condition).exp); 
				break;
			}
			acceptChild(visitor, ifbody);
			acceptChild(visitor, elsebody);
		}
		visitor.endVisit(this);
	}
	

}
