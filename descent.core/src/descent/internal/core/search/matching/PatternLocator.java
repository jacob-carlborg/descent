package descent.internal.core.search.matching;

import descent.core.compiler.CharOperation;
import descent.core.dom.AggregateDeclaration;
import descent.core.search.SearchMatch;
import descent.core.search.SearchPattern;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.search.indexing.IIndexConstants;

// TODO JDT search stub
public class PatternLocator implements IIndexConstants {
	
	// store pattern info
	protected int matchMode;
	protected boolean isCaseSensitive;
	protected boolean isCamelCase;
	protected boolean isEquivalentMatch;
	protected boolean isErasureMatch;
	protected boolean mustResolve;
	protected boolean mayBeGeneric;
	
	// match to report
	SearchMatch match = null;
	
	/* match levels */
	public static final int IMPOSSIBLE_MATCH = 0;
	public static final int INACCURATE_MATCH = 1;
	public static final int POSSIBLE_MATCH = 2;
	public static final int ACCURATE_MATCH = 3;
	public static final int ERASURE_MATCH = 4;
	
	//	 Possible rule match flavors
	//	 see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=79866
	protected static final int POSSIBLE_FULL_MATCH = POSSIBLE_MATCH | (SearchPattern.R_FULL_MATCH<<16);
	protected static final int POSSIBLE_PREFIX_MATCH = POSSIBLE_MATCH | (SearchPattern.R_PREFIX_MATCH<<16);
	protected static final int POSSIBLE_PATTERN_MATCH = POSSIBLE_MATCH | (SearchPattern.R_PATTERN_MATCH<<16);
	protected static final int POSSIBLE_REGEXP_MATCH = POSSIBLE_MATCH | (SearchPattern.R_REGEXP_MATCH<<16);
	protected static final int POSSIBLE_CAMELCASE_MATCH = POSSIBLE_MATCH | (SearchPattern.R_CAMELCASE_MATCH<<16);
	protected static final int NODE_SET_MASK = 0xFF;
	protected static final int POSSIBLE_MATCH_MASK = ~NODE_SET_MASK;

	/* match container */
	public static final int COMPILATION_UNIT_CONTAINER = 1;
	public static final int CLASS_CONTAINER = 2;
	public static final int METHOD_CONTAINER = 4;
	public static final int FIELD_CONTAINER = 8;
	public static final int ALL_CONTAINER =
		COMPILATION_UNIT_CONTAINER | CLASS_CONTAINER | METHOD_CONTAINER | FIELD_CONTAINER;

	/* match rule */
	public static final int RAW_MASK = SearchPattern.R_EQUIVALENT_MATCH | SearchPattern.R_ERASURE_MATCH;
	public static final int RULE_MASK = RAW_MASK; // no other values for the while...
	
	public static PatternLocator patternLocator(SearchPattern pattern) {
		switch (((InternalSearchPattern)pattern).kind) {
			case IIndexConstants.PKG_REF_PATTERN :
				return new PackageReferenceLocator((PackageReferencePattern) pattern);
			case IIndexConstants.PKG_DECL_PATTERN :
				return new PackageDeclarationLocator((PackageDeclarationPattern) pattern);
			case IIndexConstants.TYPE_REF_PATTERN :
				return new TypeReferenceLocator((TypeReferencePattern) pattern);
			case IIndexConstants.TYPE_DECL_PATTERN :
				return new TypeDeclarationLocator((TypeDeclarationPattern) pattern);
			case IIndexConstants.SUPER_REF_PATTERN :
				return new SuperTypeReferenceLocator((SuperTypeReferencePattern) pattern);
			case IIndexConstants.CONSTRUCTOR_PATTERN :
				return new ConstructorLocator((ConstructorPattern) pattern);
			case IIndexConstants.FIELD_PATTERN :
				return new FieldLocator((FieldPattern) pattern);
			case IIndexConstants.METHOD_PATTERN :
				return new MethodLocator((MethodPattern) pattern);
			case IIndexConstants.OR_PATTERN :
				return new OrLocator((OrPattern) pattern);
			case IIndexConstants.LOCAL_VAR_PATTERN :
				return new LocalVariableLocator((LocalVariablePattern) pattern);
			case IIndexConstants.TYPE_PARAM_PATTERN:
				return new TypeParameterLocator((TypeParameterPattern) pattern);
			case IIndexConstants.DECL_PATTERN :
				return new DeclarationLocator((DeclarationPattern) pattern);
		}
		return null;
	}
	// TODO missing methods
	public static char[] qualifiedPattern(char[] simpleNamePattern, char[] qualificationPattern) {
		// NOTE: if case insensitive search then simpleNamePattern & qualificationPattern are assumed to be lowercase
		if (simpleNamePattern == null) {
			if (qualificationPattern == null) return null;
			return CharOperation.concat(qualificationPattern, ONE_STAR, '.');
		} else {
			return qualificationPattern == null
				? CharOperation.concat(ONE_STAR, simpleNamePattern)
				: CharOperation.concat(qualificationPattern, simpleNamePattern, '.');
		}
	}
	
	public PatternLocator(SearchPattern pattern) {
		int matchRule = pattern.getMatchRule();
		this.isCaseSensitive = (matchRule & SearchPattern.R_CASE_SENSITIVE) != 0;
		this.isCamelCase = (matchRule & SearchPattern.R_CAMELCASE_MATCH) != 0;
		this.isErasureMatch = (matchRule & SearchPattern.R_ERASURE_MATCH) != 0;
		this.isEquivalentMatch = (matchRule & SearchPattern.R_EQUIVALENT_MATCH) != 0;
		this.matchMode = matchRule & JavaSearchPattern.MATCH_MODE_MASK;
		this.mustResolve = ((InternalSearchPattern)pattern).mustResolve;
	}
	
	/*
	 * Clear caches
	 */
	protected void clear() {
		// nothing to clear by default
	}
	/* (non-Javadoc)
	 * Modify PatternLocator.qualifiedPattern behavior:
	 * do not add star before simple name pattern when qualification pattern is null.
	 * This avoid to match p.X when pattern is only X...
	 */
	protected char[] getQualifiedPattern(char[] simpleNamePattern, char[] qualificationPattern) {
		// NOTE: if case insensitive search then simpleNamePattern & qualificationPattern are assumed to be lowercase
		if (simpleNamePattern == null) {
			if (qualificationPattern == null) return null;
			return CharOperation.concat(qualificationPattern, ONE_STAR, '.');
		} else if (qualificationPattern == null) {
			return simpleNamePattern;
		} else {
			return CharOperation.concat(qualificationPattern, simpleNamePattern, '.');
		}
	}
	
	/**
	 * Initializes this search pattern so that polymorphic search can be performed.
	 */ 
	public void initializePolymorphicSearch(MatchLocator locator) {
		// default is to do nothing
	}
	
	/**
	 * Check if the given ast node syntactically matches this pattern.
	 * If it does, add it to the match set.
	 * Returns the match level.
	 */
	public int match(ASTDmdNode node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(CtorDeclaration node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(Expression node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(VarDeclaration node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(AliasDeclaration node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(TypedefDeclaration node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(FuncDeclaration node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(CallExp node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(AggregateDeclaration node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(EnumDeclaration node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	public int match(TemplateDeclaration node, MatchingNodeSet nodeSet) { // needed for some generic nodes
		// each subtype should override if needed
		return IMPOSSIBLE_MATCH;
	}
	/**
	 * Returns the type(s) of container for this pattern.
	 * It is a bit combination of types, denoting compilation unit, class declarations, field declarations or method declarations.
	 */
	protected int matchContainer() {
		// override if the pattern can be more specific
		return ALL_CONTAINER;
	}
	/**
	 * Returns whether the given name matches the given pattern.
	 */
	protected boolean matchesName(char[] pattern, char[] name) {
		if (pattern == null) return true; // null is as if it was "*"
		if (name == null) return false; // cannot match null name
		return matchNameValue(pattern, name) != IMPOSSIBLE_MATCH;
	}
	/**
	 * Return how the given name matches the given pattern.
	 * @see "https://bugs.eclipse.org/bugs/show_bug.cgi?id=79866"
	 * 
	 * @param pattern
	 * @param name
	 * @return Possible values are:
	 * <ul>
	 * 	<li>{@link #POSSIBLE_FULL_MATCH}: Given name is equals to pattern</li>
	 * 	<li>{@link #POSSIBLE_PREFIX_MATCH}: Given name prefix equals to pattern</li>
	 * 	<li>{@link #POSSIBLE_CAMELCASE_MATCH}: Given name matches pattern as Camel Case</li>
	 * 	<li>{@link #POSSIBLE_PATTERN_MATCH}: Given name matches pattern as Pattern (ie. using '*' and '?' characters)</li>
	 * </ul>
	 */
	protected int matchNameValue(char[] pattern, char[] name) {
		if (pattern == null) return ACCURATE_MATCH; // null is as if it was "*"
		if (name == null) return IMPOSSIBLE_MATCH; // cannot match null name
		if (name.length == 0) { // empty name
			if (pattern.length == 0) { // can only matches empty pattern
				return ACCURATE_MATCH;
			}
			return IMPOSSIBLE_MATCH;
		} else if (pattern.length == 0) {
			return IMPOSSIBLE_MATCH; // need to have both name and pattern length==0 to be accurate
		}
		boolean matchFirstChar = !this.isCaseSensitive || pattern[0] == name[0];
		boolean sameLength = pattern.length == name.length;
		boolean canBePrefix = name.length >= pattern.length;
		if (this.isCamelCase && matchFirstChar && CharOperation.camelCaseMatch(pattern, name)) {
			return POSSIBLE_CAMELCASE_MATCH;
		}
		switch (this.matchMode) {
			case SearchPattern.R_EXACT_MATCH:
				if (!this.isCamelCase) {
					if (sameLength && matchFirstChar && CharOperation.equals(pattern, name, this.isCaseSensitive)) {
						return POSSIBLE_FULL_MATCH;
					}
					break;
				}
				// fall through next case to match as prefix if camel case failed
			case SearchPattern.R_PREFIX_MATCH:
				if (canBePrefix && matchFirstChar && CharOperation.prefixEquals(pattern, name, this.isCaseSensitive)) {
					return POSSIBLE_PREFIX_MATCH;
				}
				break;
			case SearchPattern.R_PATTERN_MATCH:
				if (!this.isCaseSensitive) {
					pattern = CharOperation.toLowerCase(pattern);
				}
				if (CharOperation.match(pattern, name, this.isCaseSensitive)) {
					return POSSIBLE_MATCH;
				}
				break;
			case SearchPattern.R_REGEXP_MATCH :
				// TODO (frederic) implement regular expression match
				break;
		}
		return IMPOSSIBLE_MATCH;
	}
	// TODO missing methods
	protected int referenceType() {
		return 0; // defaults to unknown (a generic JavaSearchMatch will be created)
	}
	/**
	 * Finds out whether the given ast node matches this search pattern.
	 * Returns IMPOSSIBLE_MATCH if it doesn't.
	 * Returns INACCURATE_MATCH if it potentially matches this search pattern (ie. 
	 * it has already been resolved but resolving failed.)
	 * Returns ACCURATE_MATCH if it matches exactly this search pattern (ie. 
	 * it doesn't need to be resolved or it has already been resolved.)
	 */
	public int resolveLevel(ASTDmdNode possibleMatchingNode) {
		// only called with nodes which were possible matches to the call to matchLevel
		// need to do instance of checks to find out exact type of ASTNode
		return IMPOSSIBLE_MATCH;
	}
	public String toString(){
		return "SearchPattern"; //$NON-NLS-1$
	}
}
