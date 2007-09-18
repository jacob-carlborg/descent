package descent.internal.compiler.parser;

// DMD 1.020
public class HdrGenState {
	
	public static class FLinitObject {
		public int init;
		public int decl;
	}

	public boolean hdrgen;
	public boolean tpltMember;
	public int console;
	
	public FLinitObject FLinit = new FLinitObject();

}
