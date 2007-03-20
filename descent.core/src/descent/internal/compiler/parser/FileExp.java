package descent.internal.compiler.parser;

public class FileExp extends UnaExp {

	public FileExp(Expression e) {
		super(TOK.TOKmixin, e);
	}
	
	@Override
	public int getNodeType() {
		return FILE_EXP;
	}

}
