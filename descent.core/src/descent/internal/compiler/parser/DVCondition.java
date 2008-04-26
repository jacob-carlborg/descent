package descent.internal.compiler.parser;

// DMD 1.020
public abstract class DVCondition extends Condition {
	
	public Module mod;
	public char[] ident;
	public long level;
	public int startPosition;
	public int length;
	
	public DVCondition(Module mod, Loc loc, long level, char[] id) {
		super(loc);
		this.mod = mod;
		this.level = level;
		this.ident = id;
		
		if (mod == null) {
			System.out.println(this);
		}
	}
	
	@Override
	public Condition syntaxCopy(SemanticContext context) {
		return this; // don't need to copy
	}

}
