package descent.internal.core.dom;

import descent.core.dom.IDebugStatement;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.dom.IStaticIfStatement;
import descent.core.dom.IVersionStatement;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

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
	
	public IStatement getThen() {
		return ifbody;
	}
	
	public IStatement getElse() {
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
