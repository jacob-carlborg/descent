package descent.internal.compiler.parser;

public class VersionSymbol extends Dsymbol {
	
	public Version version;

	public VersionSymbol(Version version) {
		this.version = version;		
	}
	
	@Override
	public int kind() {
		return VERSION_SYMBOL;
	}

}