package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDeclaration;
import descent.core.dom.IExpression;
import descent.core.dom.IStaticIfDeclaration;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class StaticIfDeclaration extends Dsymbol implements IStaticIfDeclaration {
	
	public Condition condition;
	public List<IDeclaration> a;
	public List<IDeclaration> aelse;

	public StaticIfDeclaration(Condition condition, List<IDeclaration> a, List<IDeclaration> aelse) {
		this.condition = condition;
		this.a = a;
		this.aelse = aelse;
	}

	public IExpression getCondition() {
		return ((StaticIfCondition) condition).exp;
	}

	public int getElementType() {
		return ElementTypes.STATIC_IF_DECLARATION;
	}

	@SuppressWarnings("unchecked")
	public IDeclaration[] getIfTrueDeclarationDefinitions() {
		if (a == null) return AbstractElement.NO_DECLARATIONS;
		return a.toArray(new IDeclaration[a.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public IDeclaration[] getIfFalseDeclarationDefinitions() {
		if (aelse == null) return AbstractElement.NO_DECLARATIONS;
		return aelse.toArray(new IDeclaration[aelse.size()]);
	}
	
	public void accept0(ASTVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}
