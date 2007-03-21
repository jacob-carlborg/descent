package descent.internal.compiler.parser;

public class VersionSymbol extends Dsymbol {
	
	public Version version;

	public VersionSymbol(Version version) {
		this.version = version;		
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		
	}
	
	@Override
	public int getNodeType() {
		return VERSION_SYMBOL;
	}

}
