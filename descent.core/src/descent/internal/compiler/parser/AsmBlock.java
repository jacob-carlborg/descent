package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class AsmBlock extends CompoundStatement {

	public AsmBlock(Loc loc, Statements statements) {
		super(loc, statements);
	}
	
	@Override
	public int getNodeType() {
		return ASM_BLOCK;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceStatements);
		}
		visitor.endVisit(this);
	}

}
