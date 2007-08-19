package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TraitsExp;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpTraits extends Expression {

	public final ASTNode[] args;
	public final char[] traitsKeyword;
	
	public ExpTraits(TraitsExp node) {
		convertNode(node);
		this.traitsKeyword = node.ident.ident;
		this.args = new ASTNode[node.args.size()];
		DescentASTConverter.convertMany(node.args, this.args);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}

}
