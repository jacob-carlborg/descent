package dtool.dom.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.StringUtil;
import melnorme.miscutil.tree.TreeVisitor;

import descent.internal.core.dom.FuncDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;
import dtool.dom.statements.Statement;
import dtool.refmodel.IScope;
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


	@Override
	public String toString() {
		String str = StringUtil.collToString(params, ",");
		/*for (int i = 0; i < params.length; i++) {
			str += params.get(i).type ;
			 str += ",";
		}*/
		return super.toString() +"("+ str +")";
	}
	
	@Override
	public String toStringAsDefUnit() {
		return toString() + "  " + rettype;
	}
	
	public List<IScope> getSuperScopes() {
		// TODO: function super
		return null;
	}
	

	public Iterator<FunctionParameter> getMembersIterator() {
		return params.iterator();
	}
}
