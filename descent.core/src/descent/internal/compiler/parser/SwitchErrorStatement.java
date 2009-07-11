package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.BE.BEthrow;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class SwitchErrorStatement extends Statement {

	public SwitchErrorStatement(Loc loc) {
		super(loc);
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
		
	}
	
	@Override
	public int blockExit(SemanticContext context) {
		return BEthrow;
	}

	@Override
	public int getNodeType() {
		return SWITCH_ERROR_STATEMENT;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("SwitchErrorStatement::toCBuffer()");
		buf.writenl();
	}

}
