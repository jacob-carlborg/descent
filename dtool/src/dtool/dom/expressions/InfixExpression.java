package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.BinExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;

public class InfixExpression extends Expression {
	
	// XXX: link with token ?
	public interface Type {
		
		int MUL = 11;
		int DIV = 12;
		int MOD = 13;
		int ADD = 14;
		int MIN = 15;
		int CAT = 16;
		int SHIFT_LEFT = 17;
		int SHIFT_RIGHT = 18;
		int UNSIGNED_SHIFT_RIGHT = 19;
		int CMP = 20;
		int IN = 21;
		int EQUAL = 22;
		int IDENTITY = 23;
		int AND = 24;
		int XOR = 25;
		int OR = 26;
		int AND_AND = 27;
		int OR_OR = 28;
		int ASSIGN = 30;
		int ADD_ASSIGN = 31;
		int MIN_ASSIGN = 32;
		int MUL_ASSIGN = 33;
		int DIV_ASSIGN = 34;
		int MOD_ASSIGN = 35;
		int AND_ASSIGN = 36;
		int OR_ASSIGN = 37;
		int XOR_ASSIGN = 38;
		int SHIFT_LEFT_ASSIGN = 39;
		int SHIFT_RIGHT_ASSIGN = 40;
		int UNSIGNED_SHIFT_RIGHT_ASSIGN = 41;
		int CAT_ASSIGN = 42;
		int COMMA = 44;
		int NOT_IDENTITY = 45;
	}

	public Expression leftExp;
	public Expression rightExp;
	
	public int kind;

	
	public InfixExpression(BinExp elem, int kind) {
		convertNode(elem);
		this.leftExp = (Expression) DescentASTConverter.convertElem(elem.e1);
		this.rightExp = (Expression) DescentASTConverter.convertElem(elem.e2);
		this.kind = kind;
	}

	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, leftExp);
			TreeVisitor.acceptChildren(visitor, rightExp);
		}
		visitor.endVisit(this);
	}

}
