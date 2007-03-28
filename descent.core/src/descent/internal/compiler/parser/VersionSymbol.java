package descent.internal.compiler.parser;

// TODO this class is not exactly like in DMD
public class VersionSymbol extends Dsymbol {
	
	public long level;
	public Version version;

	public VersionSymbol(IdentifierExp ident, Version version) {
		super(ident);
		this.version = version;		
	}
	
	public VersionSymbol(long level, Version version) {
		this.level = level;
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
