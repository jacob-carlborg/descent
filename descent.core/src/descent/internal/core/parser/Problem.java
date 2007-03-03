package descent.internal.core.parser;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;

public class Problem implements IProblem {
	
	public final static Object[] NO_OBJECTS = new Object[0];
	
	private String message;
	private boolean isError;
	private int categoryId;
	private int id;
	private int sourceStart;
	private int sourceEnd;
	private int sourceLineNumber;
	
	private Problem() { }
	
	public static Problem newSyntaxError(String message, int id, int line, int sourceStart, int length) {
		Problem p = new Problem();
		p.message = message;
		p.isError = true;
		p.id = id;
		p.categoryId = CAT_SYNTAX;
		p.sourceLineNumber = line;
		p.sourceStart = sourceStart;
		p.sourceEnd = sourceStart + length - 1;
		return p;
	}
	
	public int getID() {
		return id;
	}

	public String getMessage() {
		return message;
	}
	
	public int getLength() {
		return sourceEnd;
	}

	public int getSourceStart() {
		return sourceStart;
	}
	
	public int getSourceEnd() {
		return sourceEnd;
	}
	
	public boolean isError() {
		return isError;
	}
	
	public boolean isWarning() {
		return !isError;
	}
	
	public int getSourceLineNumber() {
		return sourceLineNumber;
	}
	
	public int getCategoryID() {
		return categoryId;
	}
	
	public String getMarkerType() {
		return "descent.core.problem";
	}
	
	public String[] getArguments() {
		return CharOperation.NO_STRINGS;
	}
	
	public char[] getOriginatingFileName() {
		return CharOperation.NO_CHAR;
	}

	public void setSourceEnd(int sourceEnd) {
		this.sourceEnd = sourceEnd;
	}

	public void setSourceLineNumber(int lineNumber) {
		this.sourceLineNumber = lineNumber;
	}

	public void setSourceStart(int sourceStart) {
		this.sourceStart = sourceStart;
	}
	
	/**
	 * Returns the names of the extra marker attributes associated to this problem when persisted into a marker 
	 * by the JavaBuilder. Extra attributes are only optional, and are allowing client customization of generated
	 * markers. By default, no EXTRA attributes is persisted, and a categorized problem only persists the following attributes:
	 * <ul>
	 * <li>	<code>IMarker#MESSAGE</code> -&gt; {@link IProblem#getMessage()}</li>
	 * <li>	<code>IMarker#SEVERITY</code> -&gt; <code> IMarker#SEVERITY_ERROR</code> or 
	 *         <code>IMarker#SEVERITY_WARNING</code> depending on {@link IProblem#isError()} or {@link IProblem#isWarning()}</li>
	 * <li>	<code>IJavaModelMarker#ID</code> -&gt; {@link IProblem#getID()}</li>
	 * <li>	<code>IMarker#CHAR_START</code>  -&gt; {@link IProblem#getSourceStart()}</li>
	 * <li>	<code>IMarker#CHAR_END</code>  -&gt; {@link IProblem#getSourceEnd()}</li>
	 * <li>	<code>IMarker#LINE_NUMBER</code>  -&gt; {@link IProblem#getSourceLineNumber()}</li>
	 * <li>	<code>IJavaModelMarker#ARGUMENTS</code>  -&gt; some <code>String[]</code> used to compute quickfixes </li>
	 * <li>	<code>IJavaModelMarker#CATEGORY_ID</code> -&gt; {@link CategorizedProblem#getCategoryID()}</li>
	 * </ul>
	 * The names must be eligible for marker creation, as defined by <code>IMarker#setAttributes(String[], Object[])</code>, 
	 * and there must be as many names as values according to {@link #getExtraMarkerAttributeValues()}.
	 * Note that extra marker attributes will be inserted after default ones (as described in {@link CategorizedProblem#getMarkerType()},
	 * and thus could be used to override defaults.
	 * @return the names of the corresponding marker attributes
	 */
	public String[] getExtraMarkerAttributeNames() {
		return CharOperation.NO_STRINGS;
	}

	/**
	 * Returns the respective values for the extra marker attributes associated to this problem when persisted into 
	 * a marker by the JavaBuilder. Each value must correspond to a matching attribute name, as defined by
	 * {@link #getExtraMarkerAttributeNames()}. 
	 * The values must be eligible for marker creation, as defined by <code>IMarker#setAttributes(String[], Object[])</code>.
	 * @return the values of the corresponding extra marker attributes
	 */
	public Object[] getExtraMarkerAttributeValues() {
		return NO_OBJECTS;
	}
	
	@Override
	public String toString() {
		return message;
	}

}
