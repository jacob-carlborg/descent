package dtool.dom;

import java.util.List;

import descent.internal.core.dom.Type;
import dtool.dom.ext.ASTNeoVisitor;

/**
 * A definition of a aggregate. TODO.
 */
public class DefinitionAggregate extends Definition {
	
	public List<ASTElement> members; 
	
//	public IBaseClass[] baseClasses; // TODO:
//	public ITemplateParameter[] templateParameters; // TODO:
	
	public ArcheType getArcheType() {
		return ArcheType.Aggregate;
	}
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}
}
