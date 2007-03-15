package descent.internal.compiler.parser;

public abstract class Condition {
	
	public final static int DEBUG = 1;
	public final static int IFTYPE = 2;
	public final static int STATIC_IF = 3;
	public final static int VERSION = 4;
	
	public abstract int getConditionType();

}
