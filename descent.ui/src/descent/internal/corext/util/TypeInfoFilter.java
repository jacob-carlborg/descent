package descent.internal.corext.util;

import descent.core.Flags;
import descent.core.search.IJavaSearchConstants;
import descent.core.search.IJavaSearchScope;
import descent.core.search.SearchEngine;
import descent.core.search.SearchPattern;

import descent.internal.corext.util.TypeInfo.TypeInfoAdapter;

import descent.ui.dialogs.ITypeInfoFilterExtension;

import descent.internal.ui.util.StringMatcher;

public class TypeInfoFilter {
	
	private static class PatternMatcher {
		
		private String fPattern;
		private int fMatchKind;
		private StringMatcher fStringMatcher;

		private static final char END_SYMBOL= '<';
		private static final char ANY_STRING= '*';
		private static final char BLANK= ' ';
		
		public PatternMatcher(String pattern, boolean ignoreCase) {
			this(pattern, SearchPattern.R_EXACT_MATCH | SearchPattern.R_PREFIX_MATCH |
				SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CAMELCASE_MATCH);
		}
		
		public PatternMatcher(String pattern, int allowedModes) {
			initializePatternAndMatchKind(pattern);
			fMatchKind= fMatchKind & allowedModes;
			if (fMatchKind == SearchPattern.R_PATTERN_MATCH) {
				fStringMatcher= new StringMatcher(fPattern, true, false);
			}
		}
		
		public String getPattern() {
			return fPattern;
		}
		
		public int getMatchKind() {
			return fMatchKind;
		}
		
		public boolean matches(String text) {
			switch (fMatchKind) {
				case SearchPattern.R_PATTERN_MATCH:
					return fStringMatcher.match(text);
				case SearchPattern.R_EXACT_MATCH:
					return fPattern.equalsIgnoreCase(text);
				case SearchPattern.R_CAMELCASE_MATCH:
					if (SearchPattern.camelCaseMatch(fPattern, text)) {
						return true;
					}
					// fall through to prefix match if camel case failed (bug 137244)
				default:
					return Strings.startsWithIgnoreCase(text, fPattern);
			}
		}
		
		private void initializePatternAndMatchKind(String pattern) {
			int length= pattern.length();
			if (length == 0) {
				fMatchKind= SearchPattern.R_EXACT_MATCH;
				fPattern= pattern;
				return;
			}
			char last= pattern.charAt(length - 1);
			
			if (pattern.indexOf('*') != -1 || pattern.indexOf('?') != -1) {
				fMatchKind= SearchPattern.R_PATTERN_MATCH;
				switch (last) {
					case END_SYMBOL:
						fPattern= pattern.substring(0, length - 1);
						break;
					case BLANK:
						fPattern= pattern.trim();
						break;
					case ANY_STRING:
						fPattern= pattern;
						break;
					default:
						fPattern= pattern + ANY_STRING;
				}
				return;
			}
			
			if (last == END_SYMBOL) {
				fMatchKind= SearchPattern.R_EXACT_MATCH;
				fPattern= pattern.substring(0, length - 1);
				return;
			}
			
			if (last == BLANK) {
				fMatchKind= SearchPattern.R_EXACT_MATCH;
				fPattern= pattern.trim();
				return;
			}
			
			if (SearchUtils.isCamelCasePattern(pattern)) {
				fMatchKind= SearchPattern.R_CAMELCASE_MATCH;
				fPattern= pattern;
				return;
			}
			
			fMatchKind= SearchPattern.R_PREFIX_MATCH;
			fPattern= pattern;
		}		
	}
	
	private String fText;
	private IJavaSearchScope fSearchScope;
	private boolean fIsWorkspaceScope;
	private int fElementKind;
	private ITypeInfoFilterExtension fFilterExtension;
	private TypeInfoAdapter fAdapter= new TypeInfoAdapter();

	private PatternMatcher fPackageMatcher;
	private PatternMatcher fNameMatcher;
	
	private static final int TYPE_MODIFIERS= Flags.AccEnum | /* Flags.AccAnnotation | */ Flags.AccInterface;
	
	public TypeInfoFilter(String text, IJavaSearchScope scope, int elementKind, ITypeInfoFilterExtension extension) {
		fText= text;
		fSearchScope= scope;
		fIsWorkspaceScope= fSearchScope.equals(SearchEngine.createWorkspaceScope());
		fElementKind= elementKind;
		fFilterExtension= extension;
		
		int index= text.lastIndexOf("."); //$NON-NLS-1$
		if (index == -1) {
			fNameMatcher= new PatternMatcher(text, true);
		} else {
			fPackageMatcher= new PatternMatcher(text.substring(0, index),
				SearchPattern.R_EXACT_MATCH | SearchPattern.R_PATTERN_MATCH);
			String name= text.substring(index + 1);
			if (name.length() == 0)
				name= "*"; //$NON-NLS-1$
			fNameMatcher= new PatternMatcher(name, true);
		}
	}

	public String getText() {
		return fText;
	}

	public boolean isSubFilter(String text) {
		if (! fText.startsWith(text))
			return false;
		
		return fText.indexOf('.', text.length()) == -1;
	}

	public boolean isCamcelCasePattern() {
		return fNameMatcher.getMatchKind() == SearchPattern.R_CAMELCASE_MATCH;
	}

	public String getPackagePattern() {
		if (fPackageMatcher == null)
			return null;
		return fPackageMatcher.getPattern();
	}

	public String getNamePattern() {
		return fNameMatcher.getPattern();
	}

	public int getSearchFlags() {
		return fNameMatcher.getMatchKind();
	}

	public boolean matchesRawNamePattern(TypeInfo type) {
		return Strings.startsWithIgnoreCase(type.getTypeName(), fNameMatcher.getPattern());
	}

	public boolean matchesCachedResult(TypeInfo type) {
		if (!(matchesPackage(type) && matchesFilterExtension(type)))
			return false;
		return matchesName(type);
	}
	
	public boolean matchesHistoryElement(TypeInfo type) {
		if (!(matchesPackage(type) && matchesModifiers(type) && matchesScope(type) && matchesFilterExtension(type)))
			return false;
		return matchesName(type);
	}

	public boolean matchesFilterExtension(TypeInfo type) {
		if (fFilterExtension == null)
			return true;
		fAdapter.setInfo(type);
		return fFilterExtension.select(fAdapter);
	}
	
	private boolean matchesName(TypeInfo type) {
		return fNameMatcher.matches(type.getTypeName());
	}

	private boolean matchesPackage(TypeInfo type) {
		if (fPackageMatcher == null)
			return true;
		// We should use the type container name here. However this
		// require support from JDT/Core. See
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=120534
		return fPackageMatcher.matches(type.getPackageName());
	}

	private boolean matchesScope(TypeInfo type) {
		if (fIsWorkspaceScope)
			return true;
		return type.isEnclosed(fSearchScope);
	}
	
	private boolean matchesModifiers(TypeInfo type) {
		if (fElementKind == IJavaSearchConstants.TYPE)
			return true;
		int modifiers= type.getModifiers() & TYPE_MODIFIERS;
		switch (fElementKind) {
			case IJavaSearchConstants.CLASS:
				return modifiers == 0;
				/*
			case IJavaSearchConstants.ANNOTATION_TYPE:
				return Flags.isAnnotation(modifiers);
				*/
			case IJavaSearchConstants.INTERFACE:
				return Flags.isInterface(modifiers);
			case IJavaSearchConstants.ENUM:
				return Flags.isEnum(modifiers);
			case IJavaSearchConstants.CLASS_AND_INTERFACE:
				return modifiers == 0 || Flags.isInterface(modifiers);
			case IJavaSearchConstants.CLASS_AND_ENUM:
				return modifiers == 0 || Flags.isEnum(modifiers);
		}
		return false;
	}
}
