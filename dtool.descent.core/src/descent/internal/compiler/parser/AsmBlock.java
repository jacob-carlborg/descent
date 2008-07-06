package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class AsmBlock extends CompoundStatement {

	public AsmBlock(Loc loc, List<Statement> statements) {
		super(loc, statements);
	}
	
	@Override
	public int getNodeType() {
		return ASM_BLOCK;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceStatements);
		}
		visitor.endVisit(this);
	}

}
