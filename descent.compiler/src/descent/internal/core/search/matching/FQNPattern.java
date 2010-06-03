package descent.internal.core.search.matching;

public abstract class FQNPattern extends JavaSearchPattern {
	
	public char[] simpleName;
	public char[] pkg;
	public char[][] enclosingTypeNames;
	public int modifiers;
	
	protected FQNPattern(int patternKind, int matchRule) {
		super(patternKind, matchRule);
	}

}
