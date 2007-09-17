package dtool.ast.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.NewAnonClassExp;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.declarations.Declaration;
import dtool.ast.definitions.BaseClass;
import dtool.descentadapter.DescentASTConverter;

public class ExpLiteralNewAnonClass extends Expression {
	
	public Resolvable[] allocargs;
	public Resolvable[] args;
	public BaseClass[] baseClasses;
	public ASTNeoNode[] members; 


	@SuppressWarnings("unchecked")
	public ExpLiteralNewAnonClass(NewAnonClassExp elem) {
		convertNode(elem);
		this.allocargs = Expression.convertMany(elem.newargs); 
		this.args = Expression.convertMany(elem.arguments); 
		this.baseClasses = DescentASTConverter.convertMany(elem.cd.sourceBaseclasses.toArray(),
				new BaseClass[elem.cd.sourceBaseclasses.size()]);
		this.members = Declaration.convertMany(elem.cd.members);
		
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, allocargs);
			TreeVisitor.acceptChildren(visitor, args);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

}
