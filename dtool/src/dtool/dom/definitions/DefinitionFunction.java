package dtool.dom.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.StringUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.FuncDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;
import dtool.dom.statements.IStatement;
import dtool.dom.statements.Statement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

/**
 * A definition of a function.
 * TODO: special funcs
 */
public class DefinitionFunction extends Definition implements IScopeNode, IStatement {

	//public Identifier outId;
	public descent.internal.core.dom.LINK linkage;
	public Entity rettype;
	public TemplateParameter[] templateParams;	
	public List<IFunctionParameter> params;
	public int varargs;

	public Statement frequire;
	public Statement fbody;
	public Statement fensure;
	
	//public descent.internal.core.dom.TypeFunction type;


	public DefinitionFunction(FuncDeclaration elem) {
		convertDsymbol(elem);
		this.frequire = Statement.convert(elem.frequire);
		this.fensure = Statement.convert(elem.fensure);
		this.fbody = Statement.convert(elem.fbody);

		if(elem.templateParameters != null)
			this.templateParams = TemplateParameter.convertMany(elem.templateParameters);
		this.params = DescentASTConverter.convertManyL(elem.getArguments(), this.params); 
		
		if(elem.type != null) {
			//this.type = elem.type;
			this.rettype = Entity.convertType(elem.getReturnType());
		}
	}
	
	public static ASTNeoNode convertFunctionParameter(Argument elem) {
		if(elem.id != null)
			return new FunctionParameter(elem);
		else 
			return new NamelessParameter(elem);
	}
	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChild(visitor, frequire);
			TreeVisitor.acceptChild(visitor, fbody);
			TreeVisitor.acceptChild(visitor, fensure);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		String str = StringUtil.collToString(params, ",");
		return super.toString() +"("+ str +")";
	}
	
	private String toStringParameterSig() {
		String strParams = "(";
		for (int i = 0; i < params.size(); i++) {
			if(i != 0)
				strParams += ", ";
			strParams += params.get(i).toStringAsParameter();
		}
		return strParams + ")";
	}
	
	private String toStringTemplateParams() {
		return (templateParams == null ? "" : 
			"("+ StringUtil.collToString(templateParams, ",") +")");
	}
	
	
	@Override
	public String toStringFullSignature() {
		String str = getArcheType().toString() + "  "
			+ rettype.toString() + " " + getName() 
			+ toStringTemplateParams()
			+ toStringParameterSig();
		return str;
	}
	

	@Override
	public String toStringAsCodeCompletion() {
		return getName()
			+ toStringTemplateParams()
			+ toStringParameterSig() 
			+ "  " + rettype 
			+ " - " + NodeUtil.getOuterDefUnit(this);
	}


	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Function;
	}
	
	@Override
	public IScopeNode getMembersScope() {
		return this;
	}

	
	public List<IScope> getSuperScopes() {
		// TODO: function super
		return null;
	}
	

	public Iterator<IFunctionParameter> getMembersIterator() {
		return params.iterator();
	}
}
