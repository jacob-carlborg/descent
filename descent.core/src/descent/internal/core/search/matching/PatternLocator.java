package descent.internal.core.search.matching;

import descent.core.search.SearchMatch;
import descent.core.search.SearchPattern;

// TODO JDT stub
public class PatternLocator {
	
//	 store pattern info
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
	
	/*
	 * Clear caches
	 */
	protected void clear() {
		// nothing to clear by default
	}
	
	/**
	 * Initializes this search pattern so that polymorphic search can be performed.
	 */ 
	public void initializePolymorphicSearch(MatchLocator locator) {
		// default is to do nothing
	}

}
