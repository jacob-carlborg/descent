package descent.internal.compiler.parser;

public class Version extends Dsymbol {
	
	public String value;
	
	public Version(String value) {
		this.value = value;
	}
	
	@Override
	public int kind() {
		return VERSION;
	}

}
