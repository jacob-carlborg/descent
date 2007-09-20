package dtool.ast.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.BinExp;
import descent.internal.compiler.parser.UnaExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter;

public class PrefixExpression extends Expression {
	
	public interface Type {
		
		int ADDRESS = 1;
		int PRE_INCREMENT = 2;
		int PRE_DECREMENT = 3;
		int POINTER = 4;
		int NEGATIVE = 5;
		int POSITIVE = 6;
		int NOT = 7;
		int INVERT = 8;
	}
	
	public Resolvable exp;

	public int kind;


	public PrefixExpression(UnaExp elem, int kind) {
		convertNode(elem);
		this.exp = (Resolvable) DescentASTConverter.convertElem(elem.e1);
		this.kind = kind;
	}

	public PrefixExpression(BinExp elem, int kind) {
		setSourceRange(elem);
		this.exp = (Resolvable) DescentASTConverter.convertElem(elem.e1);
		this.kind = kind;
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