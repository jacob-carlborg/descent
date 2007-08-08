package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.NewAnonClassExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.Declaration;
import dtool.dom.definitions.DefinitionAggregate;

public class ExpLiteralNewAnonClass extends Expression {
	
	public Expression[] allocargs;
	public Expression[] args;
	public DefinitionAggregate.BaseClass[] baseClasses;
	public ASTNode[] members; 


	@SuppressWarnings("unchecked")
	public ExpLiteralNewAnonClass(NewAnonClassExp elem) {
		convertNode(elem);
		this.allocargs = Expression.convertMany(elem.newargs); 
		this.args = Expression.convertMany(elem.arguments); 
		this.baseClasses = DescentASTConverter.convertMany(elem.cd.baseClasses,
				new DefinitionAggregate.BaseClass[elem.cd.baseClasses.length]);
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
