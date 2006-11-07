package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStaticIfDeclaration;

public class StaticIfDeclaration extends Dsymbol implements IStaticIfDeclaration {
	
	public Condition condition;
	public List<IDElement> a;
	public List<IDElement> aelse;

	public StaticIfDeclaration(Condition condition, List<IDElement> a, List<IDElement> aelse) {
		this.condition = condition;
		this.a = a;
		this.aelse = aelse;
	}

	public IExpression getCondition() {
		return ((StaticIfCondition) condition).exp;
	}
	
	public int getElementType() {
		return CONDITIONAL_DECLARATION;
	}

	public int getConditionalDeclarationType() {
		return CONDITIONAL_STATIC_IF;
	}

	@SuppressWarnings("unchecked")
	public IDElement[] getIfTrueDeclarationDefinitions() {
		if (a == null) return AbstractElement.NO_ELEMENTS;
		return a.toArray(new IDElement[a.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public IDElement[] getIfFalseDeclarationDefinitions() {
		if (aelse == null) return AbstractElement.NO_ELEMENTS;
		return aelse.toArray(new IDElement[aelse.size()]);
	}
	
	public void accept(IDElementVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}
