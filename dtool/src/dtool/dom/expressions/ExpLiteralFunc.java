package dtool.dom.expressions;

import java.util.List;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.FuncExp;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.TypeFunction;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefinitionFunction;
import dtool.dom.definitions.IFunctionParameter;
import dtool.dom.references.Reference;
import dtool.dom.statements.IStatement;
import dtool.dom.statements.Statement;

public class ExpLiteralFunc extends Expression {
	
	public Reference rettype;
	public List<IFunctionParameter> params;
	public int varargs;

	public IStatement frequire;
	public IStatement fbody;
	public IStatement fensure;

	public ExpLiteralFunc(FuncExp elem) {
		convertNode(elem);
		FuncLiteralDeclaration fd = elem.fd;
		
		this.frequire = Statement.convert(fd.frequire);
		this.fensure = Statement.convert(fd.fensure);
		this.fbody = Statement.convert(fd.fbody);
		
		TypeFunction elemTypeFunc = ((TypeFunction) fd.type);

		Assert.isTrue(fd.parameters == null);
		this.params = DescentASTConverter.convertManyL(elemTypeFunc.parameters, this.params); 

		varargs = DefinitionFunction.convertVarArgs(elemTypeFunc.varargs);
		this.rettype = Reference.convertType(elemTypeFunc.next);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, frequire);
			TreeVisitor.acceptChildren(visitor, fbody);
			TreeVisitor.acceptChildren(visitor, fensure);
		}
		visitor.endVisit(this);	 
	}

}
