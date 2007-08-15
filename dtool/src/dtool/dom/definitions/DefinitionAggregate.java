package dtool.dom.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.StringUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IScopeNode;

/**
 * A definition of a aggregate. 
 */
public abstract class DefinitionAggregate extends Definition implements IScopeNode, IStatement {

	public TemplateParameter[] templateParams; 
	public List<ASTNode> members; // can be null. (bodyless aggregates)
	
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}
	
	protected void acceptNodeChildren(IASTNeoVisitor visitor, boolean children) {
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, members);
		}
	}
	
	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	public Iterator<ASTNode> getMembersIterator() {
		return members.iterator();
	}

	@Override
	public String toStringAsCodeCompletion() {
		return defname + " - " + getModuleScope();
	}
	
	private String toStringTemplateParams() {
		return (templateParams == null ? "" : 
			"("+ StringUtil.collToString(templateParams, ",") +")");
	}

	@Override
	public String toStringFullSignature() {
		String str = getArcheType().toString() 
		+ "  " + getModuleScope() +"."+ getName()
		+ toStringTemplateParams();
		return str;
	}
	
}
