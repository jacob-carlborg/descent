package descent.internal.codeassist.complete;

import java.util.List;

public class CompletionOnJavadocImpl implements CompletionOnJavadoc {
	
	private final int start;
	private final char[] source;
	List<char[]> otherDdocs;

	public CompletionOnJavadocImpl(int start, char[] source) {
		this.start = start;
		this.source = source;
	}
	
	public char[] getSource() {
		return source;
	}
	
	public int getStart() {
		return start;
	}
	
	public List<char[]> getOtherDdocs() {
		return otherDdocs;
	}

	public void addCompletionFlags(int flags) {
	}

	public int getCompletionFlags() {
		return 0;
	}

}
