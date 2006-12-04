package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IExpression;
import descent.core.dom.IStaticIfDeclaration;

public class StaticIfDeclaration extends Declaration implements IStaticIfDeclaration {
	
	public Condition condition;
	public List<Declaration> a;
	public List<Declaration> aelse;

	public StaticIfDeclaration(Condition condition, List<Declaration> a, List<Declaration> aelse) {
		this.condition = condition;
		this.a = a;
		this.aelse = aelse;
	}

	public IExpression getCondition() {
		return ((StaticIfCondition) condition).exp;
	}

	public int getNodeType0() {
		return STATIC_IF_DECLARATION;
	}

	@SuppressWarnings("unchecked")
	public IDeclaration[] getIfTrueDeclarationDefinitions() {
		if (a == null) return ASTNode.NO_DECLARATIONS;
		return a.toArray(new IDeclaration[a.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public IDeclaration[] getIfFalseDeclarationDefinitions() {
		if (aelse == null) return ASTNode.NO_DECLARATIONS;
		return aelse.toArray(new IDeclaration[aelse.size()]);
	}
	
	public void accept0(ASTVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}
