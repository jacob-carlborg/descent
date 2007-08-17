package dtool.dom.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.Assert;
import melnorme.miscutil.StringUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.TypeFunction;
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
	public descent.internal.compiler.parser.LINK linkage;
	public Reference rettype;
	public TemplateParameter[] templateParams;	
	public List<IFunctionParameter> params;
	public int varargs;

	public IStatement frequire;
	public IStatement fbody;
	public IStatement fensure;
	
	//public descent.internal.compiler.parser.TypeFunction type;


	public DefinitionFunction(FuncDeclaration elem) {
		super(elem);
		this.frequire = Statement.convert(elem.frequire);
		this.fensure = Statement.convert(elem.fensure);
		this.fbody = Statement.convert(elem.fbody);
		
		TypeFunction elemTypeFunc = ((TypeFunction) elem.type);

		/*if(elem.templateParameters != null)
			this.templateParams = TemplateParameter.convertMany(elem.templateParameters);*/
		Assert.isTrue(elem.parameters == null);
		this.params = DescentASTConverter.convertManyL(elemTypeFunc.parameters, this.params); 

		varargs = convertVarArgs(elemTypeFunc.varargs);
		Assert.isNotNull(elemTypeFunc.next);
		this.rettype = Reference.convertType(elemTypeFunc.next);
		Assert.isNotNull(this.rettype);
	}
	
	public static ASTNeoNode convertFunctionParameter(Argument elem) {
		if(elem.ident != null)
			return new FunctionParameter(elem);
		else 
			return new NamelessParameter(elem);
	}
	
	public static int convertVarArgs(int varargs) {
		Assert.isTrue(varargs >= 0 && varargs <= 2);
		return varargs;
	}
	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, frequire);
			TreeVisitor.acceptChildren(visitor, fbody);
			TreeVisitor.acceptChildren(visitor, fensure);
		}
		visitor.endVisit(this);
	}
	
	public static String toStringParameterSig(List<IFunctionParameter> params, int varargs) {
		String strParams = "(";
		for (int i = 0; i < params.size(); i++) {
			if(i != 0)
				strParams += ", ";
			strParams += params.get(i).toStringAsParameter();
		}
		if(varargs == 1) strParams += (params.size()==0 ? "..." : ", ...");
		if(varargs == 2) strParams += "...";
		return strParams + ")";
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
