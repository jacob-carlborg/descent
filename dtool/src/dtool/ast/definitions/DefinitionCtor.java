package dtool.ast.definitions;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.FuncDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.Statement;
import dtool.descentadapter.DescentASTConverter;

public class DefinitionCtor extends ASTNeoNode {

	public List<IFunctionParameter> params;
	public int varargs;

	public IStatement fbody;

	
	public DefinitionCtor(CtorDeclaration elem) {
		convertNode(elem);
		this.params = DescentASTConverter.convertManyL(elem.parameters, this.params);
		this.fbody = Statement.convert(elem.fbody);
		varargs = DefinitionFunction.convertVarArgs(elem.varargs);
	}
	
	public DefinitionCtor(FuncDeclaration elem) {
		convertNode(elem);
		this.params = DescentASTConverter.convertManyL(elem.parameters, this.params);
		this.fbody = Statement.convert(elem.fbody);
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, fbody);
		}
		visitor.endVisit(this);
	}

}
