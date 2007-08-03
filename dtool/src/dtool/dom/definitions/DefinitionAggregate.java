package dtool.dom.definitions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.StringUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.AggregateDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A definition of a aggregate. TODO.
 */
public class DefinitionAggregate extends Definition implements IScopeNode, IStatement {

	public static class BaseClass extends ASTNeoNode {
		
		public int prot;
		public Entity type;
		
		public BaseClass(descent.internal.core.dom.BaseClass elem) {
			convertNode(elem);
			if(elem.hasNoSourceRangeInfo()) 
				convertNode(elem.type); // Try to have some range
				
			this.prot = elem.prot;
			this.type = Entity.convertType(elem.type);
		}
		
		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChild(visitor, type);
			}
			visitor.endVisit(this);	 			
		}
	}
	
	public List<ASTNode> members; 
	public List<BaseClass> baseClasses;
	public TemplateParameter[] templateParams; 
	
	
	@SuppressWarnings("unchecked")
	public DefinitionAggregate(AggregateDeclaration elem) {
		convertDsymbol(elem);
		this.members = DescentASTConverter.convertManyL(elem.members, this.members);
		this.baseClasses = DescentASTConverter.convertManyL(elem.baseClasses, this.baseClasses);
		if(elem.templateParameters != null)
		this.templateParams = TemplateParameter.convertMany(elem.templateParameters);
	}
	
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}
	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	public List<IScope> getSuperScopes() {
		if(baseClasses.size() < 0)
			return null;

		List<IScope> scopes = new ArrayList<IScope>();
		for(BaseClass baseclass: baseClasses) {
			DefUnit defunit = baseclass.type.findTargetDefUnit();
			if(defunit == null)
				continue;
			scopes.add(defunit.getMembersScope());
		}
		return scopes;
		// TODO add Object super scope.
	}

	public Iterator<ASTNode> getMembersIterator() {
		return members.iterator();
	}

	@Override
	public String toStringAsCodeCompletion() {
		return defname + " - " + getModule();
	}
	
	private String toStringTemplateParams() {
		return (templateParams == null ? "" : 
			"("+ StringUtil.collToString(templateParams, ",") +")");
	}

	@Override
	public String toStringFullSignature() {
		String str = getArcheType().toString() 
		+ "  " + getModule() +"."+ getName()
		+ toStringTemplateParams();
		return str;
	}

}
