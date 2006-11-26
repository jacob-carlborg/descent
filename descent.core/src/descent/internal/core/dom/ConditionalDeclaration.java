package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IDeclaration;
import descent.core.dom.IIftypeDeclaration;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.IVersionDeclaration;

public class ConditionalDeclaration extends Dsymbol implements IVersionDeclaration, IDebugDeclaration, IIftypeDeclaration {
	
	public Condition condition;
	public List<IDeclaration> a;
	public List<IDeclaration> aelse;

	public ConditionalDeclaration(Condition condition, List<IDeclaration> a, List<IDeclaration> aelse) {
		this.condition = condition;
		this.a = a;
		this.aelse = aelse;
	}
	
	public int getElementType() {
		switch(this.condition.getConditionType()) {
		case Condition.DEBUG: return DEBUG_DECLARATION;
		case Condition.VERSION: return VERSION_DECLARATION;
		case Condition.IFTYPE: return IFTYPE_DECLARATION;
		}
		return 0;
	}
	
	public IName getVersion() {
		return ((VersionCondition) condition).id;
	}
	
	public IName getDebug() {
		return ((DebugCondition) condition).id;
	}
	
	public IName getIdentifier() {
		return ((IftypeCondition) condition).ident;
	}
	
	public int getIftypeCondition() {
		return ((IftypeCondition) condition).getIftypeCondition();
	}
	
	public IType getTestType() {
		return ((IftypeCondition) condition).targ;
	}
	
	public IType getMatchingType() {
		return ((IftypeCondition) condition).tspec;
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
	
	public void accept0(ElementVisitor visitor) {
		boolean children;
		switch(this.condition.getConditionType()) {
		case Condition.DEBUG: 
			children = visitor.visit((IDebugDeclaration) this);
			if (children) {
				acceptChild(visitor, ((DebugCondition) condition).id); 
				acceptChildren(visitor, a);
				acceptChildren(visitor, aelse);
			}
			visitor.endVisit((IDebugDeclaration) this);
			break;
		case Condition.VERSION:
			children = visitor.visit((IVersionDeclaration) this);
			if (children) {
				acceptChild(visitor, ((VersionCondition) condition).id); 
				acceptChildren(visitor, a);
				acceptChildren(visitor, aelse);
			}
			visitor.endVisit((IVersionDeclaration) this);
			break;
		case Condition.IFTYPE:
			children = visitor.visit((IIftypeDeclaration) this);
			if (children) {
				acceptChild(visitor, ((IftypeCondition) condition).ident);
				acceptChild(visitor, ((IftypeCondition) condition).targ);
				acceptChild(visitor, ((IftypeCondition) condition).tspec); 
				acceptChildren(visitor, a);
				acceptChildren(visitor, aelse);
			}
			visitor.endVisit((IIftypeDeclaration) this);
			break;
		}
	}

}
