package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.BE.BEthrow;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class SwitchErrorStatement extends Statement {

	public SwitchErrorStatement(Loc loc) {
		super(loc);
		synthetic = true;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
		melnorme.miscutil.Assert.fail("accept0 on a fake Node");
	}
	
	@Override
	public int blockExit(SemanticContext context) {
		return BEthrow;
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return false;
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
