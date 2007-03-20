package descent.internal.compiler.parser;

public class DebugSymbol extends Dsymbol {
	
	public Version version;

	public DebugSymbol(Version version) {
		this.version = version;		
	}
	
	@Override
	public int getNodeType() {
		return DEBUG_SYMBOL;
	}

}
