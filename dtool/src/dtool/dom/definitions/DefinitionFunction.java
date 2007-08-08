package dtool.dom.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.Assert;
import melnorme.miscutil.StringUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.FuncDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
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
	public Reference rettype;
	public TemplateParameter[] templateParams;	
	public List<IFunctionParameter> params;
	public boolean varargs;

	public IStatement frequire;
	public IStatement fbody;
	public IStatement fensure;
	
	//public descent.internal.core.dom.TypeFunction type;


	public DefinitionFunction(FuncDeclaration elem) {
		convertDsymbol(elem);
		this.frequire = Statement.convert(elem.frequire);
		this.fensure = Statement.convert(elem.fensure);
		this.fbody = Statement.convert(elem.fbody);

		if(elem.templateParameters != null)
			this.templateParams = TemplateParameter.convertMany(elem.templateParameters);
		this.params = DescentASTConverter.convertManyL(elem.getArguments(), this.params); 

		if(elem.ident.string.equals("this")) {
			//TODO
		} else if(elem.ident.string.equals("~this")) {
			//TODO
		} else {
			varargs = convertVarArgs(elem.type.varargs);
			this.rettype = Reference.convertType(elem.getReturnType());
		}
	}
	
	public static ASTNeoNode convertFunctionParameter(Argument elem) {
		if(elem.id != null)
			return new FunctionParameter(elem);
		else 
			return new NamelessParameter(elem);
	}
	
	public static boolean convertVarArgs(int varargs) {
		if(varargs == 0)
			return false;
		else if(varargs == 1) 
			return true;
		else Assert.fail("Unknown varargs");
		return false;
	}
	
	public static String toStringParameterSig(List<IFunctionParameter> params, boolean varargs) {
		String strParams = "(";
		for (int i = 0; i < params.size(); i++) {
			if(i != 0)
				strParams += ", ";
			strParams += params.get(i).toStringAsParameter();
		}
		if(varargs) strParams = strParams + (params.size()==0 ? "..." : ", ...");
		return strParams + ")";
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
		return super.toString() + toStringParameterSig(params, varargs);
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
			+ toStringParameterSig(params, varargs);
		return str;
	}
	

	@Override
	public String toStringAsCodeCompletion() {
		return getName()
			+ toStringTemplateParams()
			+ toStringParameterSig(params, varargs) 
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
