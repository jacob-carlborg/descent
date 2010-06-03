package descent.internal.codeassist.complete;

public interface ICompletionOnKeyword {
	
	char[] getToken();
	char[][] getPossibleKeywords();
	boolean canCompleteEmptyToken();
}
