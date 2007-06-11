package dtool.dom.definitions;

import java.util.List;

import util.StringUtil;
import util.tree.TreeVisitor;
import descent.internal.core.dom.FuncDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.BaseEntityRef;
import dtool.dom.base.Entity;
import dtool.dom.statements.Statement;
import dtool.model.IScope;

/**
 * A definition of a function.
 * TODO: special funcs
 */
public class DefinitionFunction extends Definition implements IScope {

	//public Identifier outId;
	public descent.internal.core.dom.LINK linkage;
	public BaseEntityRef.TypeConstraint rettype;
	public TemplateParameter[] templateParams;	
	public List<Parameter> params;
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
		this.params = DescentASTConverter.convertMany(elem.getArguments(), this.params); 
		
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
	public IScope getBindingScope() {
		return this;
	}

	@SuppressWarnings("unchecked")
	public List<DefUnit> getDefUnits() {
		return (List<DefUnit>) (List) params;
	}
	
	@Override
	public String toString() {
		return super.toString() +"("+ StringUtil.collToString(params, ",") +")";
	}
	
	public IScope getSuperScope() {
		// TODO: function super
		return null;
	}
}
