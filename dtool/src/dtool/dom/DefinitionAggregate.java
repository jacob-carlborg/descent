package dtool.dom;

import java.util.List;

import descent.core.dom.ITemplateParameter;
import descent.internal.core.dom.Type;
import dtool.dom.ext.ASTNeoVisitor;


public class DefinitionAggregate extends Definition {
	
	public Type type;
	public List<ASTElement> members; 
	
//	public IBaseClass[] baseClasses; // FIXME:
//	public ITemplateParameter[] templateParameters; // FIXME:
	
	public ArcheType getArcheType() {
		return ArcheType.Aggregate;
	}
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
			acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}
}
