package dtool.dom.expressions;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.BinExp;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.UnaExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;

public class PostfixExpression extends Expression {
	
	public interface Type {
		int POST_INCREMENT = 9;
		int POST_DECREMENT = 10;
	}
	
	public Resolvable exp;
	
	public int kind;

	
	public PostfixExpression(UnaExp elem, int kind) {
		convertNode(elem);
		this.exp = (Resolvable) DescentASTConverter.convertElem(elem.e1);
		this.kind = kind;
	}

	public PostfixExpression(BinExp elem) {
		setSourceRange(elem);
		this.exp = (Resolvable) DescentASTConverter.convertElem(elem.e1);
		if(elem.op == TOK.TOKplusplus) {
			this.kind = Type.POST_INCREMENT;
		} else if(elem.op == TOK.TOKminusminus) {
			this.kind = Type.POST_DECREMENT;
		} else Assert.fail();
	}

	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
