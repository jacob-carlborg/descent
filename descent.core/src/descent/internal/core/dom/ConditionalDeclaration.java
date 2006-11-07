package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.IVersionDeclaration;

public class ConditionalDeclaration extends Dsymbol implements IVersionDeclaration, IDebugDeclaration {
	
	public Condition condition;
	public List<IDElement> a;
	public List<IDElement> aelse;

	public ConditionalDeclaration(Condition condition, List<IDElement> a, List<IDElement> aelse) {
		this.condition = condition;
		this.a = a;
		this.aelse = aelse;
	}
	
	public int getElementType() {
		return CONDITIONAL_DECLARATION;
	}
	
	public int getConditionalDeclarationType() {
		switch(this.condition.getConditionType()) {
		case Condition.DEBUG: return CONDITIONAL_DEBUG;
		case Condition.VERSION: return CONDITIONAL_VERSION;
		}
		return 0;
	}
	
	public IName getVersion() {
		return ((VersionCondition) condition).id;
	}
	
	public IName getDebug() {
		return ((DebugCondition) condition).id;
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
		boolean children = visitor.visit(this);
		if (children) {
			switch(this.condition.getConditionType()) {
			case Condition.DEBUG: 
				acceptChild(visitor, ((DebugCondition) condition).id); 
				break;
			case Condition.VERSION:
				acceptChild(visitor, ((VersionCondition) condition).id); 
				break;
			}
			acceptChildren(visitor, a);
			acceptChildren(visitor, aelse);
		}
		visitor.endVisit(this);
	}

}
