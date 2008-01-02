package descent.internal.compiler.parser;

public class Global {
	
	public int structalign = 8;
	public String version;
	public int gag;
	public int errors;
	public Param params = new Param();
	public long debugLevel;
	public Array<String> path = new Array<String>();
	
	public Global() {
		path.add("C:\\d\\dmd_1.0.20\\dmd\\src\\phobos");
	}

}
