package dtool.dom.definitions;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.FuncDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;
import dtool.dom.statements.Statement;
import dtool.refmodel.IScopeNode;

/**
 * A definition of a function.
 * TODO: special funcs
 */
public class DefinitionFunction extends Definition implements IScopeNode {

	//public Identifier outId;
	public descent.internal.core.dom.LINK linkage;
	public Entity rettype;
	public TemplateParameter[] templateParams;	
	public List<FunctionParameter> params;
	public int varargs;

	public Statement frequire;
	public Statement fbody;
	public Statement fensure;
	
	public descent.internal.core.dom.TypeFunction type;


	public DefinitionFunction(FuncDeclaration elem) {
		convertDsymbol(elem);
		this.frequire = Statement.convert(elem.frequire);
		this.fensure = Statement.convert(elem.fensure);
		this.fbody = Statement.convert(elem.fbody);

		if(elem.templateParameters != null)
			this.templateParams = TemplateParameter.convertMany(elem.templateParameters);
		this.params = DescentASTConverter.convertManyL(elem.getArguments(), this.params); 
		
		if(elem.type != null) {
			this.type = elem.type;
			this.rettype = Entity.convertType(elem.getReturnType());
		}
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Function;
	}
	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChild(visitor, frequire);
			TreeVisitor.acceptChild(visitor, fbody);
			TreeVisitor.acceptChild(visitor, fensure);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScopeNode getMembersScope() {
		return this;
	}

	@SuppressWarnings("unchecked")
	public List<DefUnit> getDefUnits() {
		return (List<DefUnit>) (List) params;
	}
	
	@Override
	public String toString() {
		String str = null;
		for(FunctionParameter param : params) {
			str = str + param.type + ",";
		}
		return super.toString() +"("+ str +")";
	}
	
	public List<IScopeNode> getSuperScopes() {
		// TODO: function super
		return null;
	}
}
