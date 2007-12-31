package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Xor;

// DMD 1.020 
public class XorAssignExp extends BinExp {

	public XorAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKxorass, e1, e2);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceE1);
			TreeVisitor.acceptChildren(visitor, sourceE2);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return XOR_ASSIGN_EXP;
	}

	@Override
	public char[] opId() {
		return Id.xorass;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		return commonSemanticAssignIntegral(sc, context);
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretAssignCommon(istate, Xor, context);
	}

}
