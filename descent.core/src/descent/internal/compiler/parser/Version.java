package descent.internal.compiler.parser;

public class Version extends Dsymbol {
	
	public String value;
	
	public Version(Loc loc, String value) {
		super(loc);
		this.value = value;
	}
	
	@Override
	public int getNodeType() {
		return VERSION;
	}

}
