package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;

public class Problem implements IProblem {
	
	public final static Object[] NO_OBJECTS = new Object[0];
	
	private boolean isError;
	private int categoryId;
	private int id;
	private int sourceStart;
	private int sourceEnd;
	private int sourceLineNumber;
	private String[] arguments;
	
	private Problem() { }
	
	public static Problem newSyntaxError(int id, int line, int start, int length, String ... arguments) {
		Problem p = new Problem();
		p.isError = true;
		p.id = id;
		p.categoryId = CAT_SYNTAX;
		p.sourceLineNumber = line;
		p.sourceStart = start;
		p.sourceEnd = start + length - 1;
		p.arguments = arguments;
		return p;
	}
	
	public static Problem newSyntaxError(int id, int line, int start, int length) {
		return newSyntaxError(id, line, start, length, (String[]) null);
	}
	
	public static Problem newSemanticMemberError(int id, int line, int start, int length, String ... arguments) {
		Problem p = new Problem();
		p.isError = true;
		p.id = id;
		p.categoryId = CAT_MEMBER;
		p.sourceLineNumber = line;
		p.sourceStart = start;
		p.sourceEnd = start + length - 1;
		p.arguments = arguments;
		return p;
	}
	
	public static Problem newSemanticMemberError(int id, int line, int start, int length) {
		return newSemanticMemberError(id, line, start, length, (String[]) null);
	}
	
	private static Problem newSemanticTypeProblem(int id, int line, int start, int length, String[] arguments, boolean isError) {
		Problem p = new Problem();
		p.isError = isError;
		p.id = id;
		p.categoryId = CAT_TYPE;
		p.sourceLineNumber = line;
		p.sourceStart = start;
		p.sourceEnd = start + length - 1;
		p.arguments = arguments;
		return p;
	}
	
	public static Problem newSemanticTypeError(int id, int line, int start, int length, String ... arguments) {
		return newSemanticTypeProblem(id, line, start, length, arguments, true);
	}
	
	public static Problem newSemanticTypeError(int id, ASTDmdNode node, String ... arguments) {
		return newSemanticTypeProblem(id, node.getLineNumber(), node.getStart(), node.getLength(), arguments, true);
	}
	
	public static Problem newSemanticTypeErrorLoc(int id, ASTDmdNode node, String ... arguments) {
		return newSemanticTypeProblem(id, node.getLineNumber(), node.getErrorStart(), node.getErrorLength(), arguments, true);
	}
	
	public static Problem newSemanticTypeError(int id, ASTDmdNode n1, ASTDmdNode n2, String ... arguments) {
		return newSemanticTypeProblem(id, n1.getLineNumber(), n1.getStart(), n2.getStart()+ n2.getLength() - n1.getStart(), arguments, true);
	}
	
	public static Problem newSemanticTypeError(int id, ASTDmdNode n1, ASTDmdNode n2) {
		return newSemanticTypeProblem(id, n1.getLineNumber(), n1.getStart(), n2.getStart() + n2.getLength() - n1.getStart(), null, true);
	}
	
	public static Problem newSemanticTypeError(int id, ASTDmdNode node) {
		return newSemanticTypeProblem(id, node.getLineNumber(), node.getStart(), node.getLength(), null, true);
	}
	
	public static Problem newSemanticTypeErrorLoc(int id, ASTDmdNode node) {
		return newSemanticTypeProblem(id, node.getLineNumber(), node.getErrorStart(), node.getErrorLength(), null, true);
	}
	
	public static Problem newSemanticTypeError(int id, int line, int start, int length) {
		return newSemanticTypeError(id, line, start, length, (String[]) null);
	}
	
	public static Problem newSemanticTypeWarning(int id, int line, int start, int length, String ... arguments) {
		return newSemanticTypeProblem(id, line, start, length, arguments, false);
	}
	
	public static Problem newSemanticTypeWarning(int id, int line, int start, int length) {
		return newSemanticTypeWarning(id, line, start, length, (String[]) null);
	}
	
	public static Problem newSemanticTypeWarning(int id, ASTDmdNode node) {
		return newSemanticTypeWarning(id, node.getLineNumber(), node.getStart(), node.getLength(), (String[]) null);
	}
	
	public static Problem newSemanticTypeWarningLoc(int id, ASTDmdNode node) {
		return newSemanticTypeWarning(id, node.getLineNumber(), node.getErrorStart(), node.getErrorLength(), (String[]) null);
	}
	
	public static Problem newTask(String message, int line, int start, int length) {
		Problem p = new Problem();
		p.arguments = new String[] { message };
		p.isError = false;
		p.id = IProblem.Task;
		p.categoryId = CAT_UNSPECIFIED;
		p.sourceLineNumber = line;
		p.sourceStart = start;
		p.sourceEnd = start + length - 1;
		return p;
	}
	
	public int getID() {
		return id;
	}

	public String getMessage() {
		switch(id) {
		/* EVAL-FOR-EACH
		* 
		* print DST "\t\tcase " . $$_{'optName'} . ":\n";
		* print DST "\t\t\treturn String.format(ProblemMessages." . $$_{'optName'};
		* if($$_{'numArgs'})
		* {
		*     for(my $i = 0; $i < $$_{'numArgs'}; $i++)
		*     {
		*         print DST ", arguments[" . $i . "]";
		*     }
		* }
		* print DST ");\n";
		*
		*/
		default:
			return "";
		}
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
		return getMessage();
	}

}
