package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDebugStatement;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.IStaticIfStatement;
import descent.core.dom.IVersionStatement;

public class ConditionalStatement extends Statement implements IStaticIfStatement, IDebugStatement, IVersionStatement {

	private final Condition condition;
	private final Statement ifbody;
	private final Statement elsebody;

	public ConditionalStatement(Condition condition, Statement ifbody, Statement elsebody) {
		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;
	}
	
	public IExpression getCondition() {
		return ((StaticIfCondition) condition).exp;
	}
	
	public IStatement getBody() {
		return ifbody;
	}
	
	public IStatement getElseBody() {
		return elsebody;
	}
	
	public int getNodeType0() {
		switch(this.condition.getConditionType()) {
		case Condition.DEBUG: return DEBUG_STATEMENT;
		case Condition.VERSION: return VERSION_STATEMENT;
		case Condition.STATIC_IF: return STATIC_IF_STATEMENT;
		}
		return 0;
	}
	
	public String getName() {
		return null;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children;
		switch(this.condition.getConditionType()) {
		case Condition.DEBUG: 
			children = visitor.visit((IDebugStatement) this);
			if (children) {
				acceptChild(visitor, ((DebugCondition) condition).id); 
				acceptChild(visitor, ifbody);
				acceptChild(visitor, elsebody);
			}
			visitor.endVisit((IDebugStatement) this);
			break;
		case Condition.VERSION:
			children = visitor.visit((IVersionStatement) this);
			if (children) {
				acceptChild(visitor, ((VersionCondition) condition).id);
				acceptChild(visitor, ifbody);
				acceptChild(visitor, elsebody);
			}
			visitor.endVisit((IVersionStatement) this);
			break;
		case Condition.STATIC_IF:
			children = visitor.visit((IStaticIfStatement) this);
			if (children) {
				acceptChild(visitor, ((StaticIfCondition) condition).exp);
				acceptChild(visitor, ifbody);
				acceptChild(visitor, elsebody);
			}
			visitor.endVisit((IStaticIfStatement) this);
			break;
		}
		
		
	}
	

}
