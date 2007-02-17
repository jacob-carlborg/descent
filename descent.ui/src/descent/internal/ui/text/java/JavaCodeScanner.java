/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.internal.ui.text.java;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

import descent.core.JavaCore;

import descent.ui.PreferenceConstants;
import descent.ui.text.IColorManager;
import descent.ui.text.IJavaColorConstants;

import descent.internal.ui.javaeditor.SemanticHighlightings;
import descent.internal.ui.text.AbstractJavaScanner;
import descent.internal.ui.text.CombinedWordRule;
import descent.internal.ui.text.JavaWhitespaceDetector;
import descent.internal.ui.text.JavaWordDetector;
import descent.internal.ui.text.ISourceVersionDependent;


/**
 * A Java code scanner.
 */
public final class JavaCodeScanner extends AbstractJavaScanner {

	/**
	 * Rule to detect java operators.
	 *
	 * @since 3.0
	 */
	protected class OperatorRule implements IRule {

		/** Java operators */
		private final char[] JAVA_OPERATORS= { ';', '(', ')', '{', '}', '.', '=', '/', '\\', '+', '-', '*', '[', ']', '<', '>', ':', '?', '!', ',', '|', '&', '^', '%', '~'};
		/** Token to return for this rule */
		private final IToken fToken;

		/**
		 * Creates a new operator rule.
		 *
		 * @param token Token to use for this rule
		 */
		public OperatorRule(IToken token) {
			fToken= token;
		}

		/**
		 * Is this character an operator character?
		 *
		 * @param character Character to determine whether it is an operator character
		 * @return <code>true</code> iff the character is an operator, <code>false</code> otherwise.
		 */
		public boolean isOperator(char character) {
			for (int index= 0; index < JAVA_OPERATORS.length; index++) {
				if (JAVA_OPERATORS[index] == character)
					return true;
			}
			return false;
		}

		/*
		 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {

			int character= scanner.read();
			if (isOperator((char) character)) {
				do {
					character= scanner.read();
				} while (isOperator((char) character));
				scanner.unread();
				return fToken;
			} else {
				scanner.unread();
				return Token.UNDEFINED;
			}
		}
	}

	private static class VersionedWordMatcher extends CombinedWordRule.WordMatcher implements ISourceVersionDependent {

		private final IToken fDefaultToken;
		private final String fVersion;
		private boolean fIsVersionMatch;

		public VersionedWordMatcher(IToken defaultToken, String version, String currentVersion) {
			fDefaultToken= defaultToken;
			fVersion= version;
			setSourceVersion(currentVersion);
		}

		/*
		 * @see descent.internal.ui.text.ISourceVersionDependent#setSourceVersion(java.lang.String)
		 */
		public void setSourceVersion(String version) {
			fIsVersionMatch= fVersion.compareTo(version) <= 0;
		}

		/*
		 * @see descent.internal.ui.text.CombinedWordRule.WordMatcher#evaluate(org.eclipse.jface.text.rules.ICharacterScanner, descent.internal.ui.text.CombinedWordRule.CharacterBuffer)
		 */
		public IToken evaluate(ICharacterScanner scanner, CombinedWordRule.CharacterBuffer word) {
			IToken token= super.evaluate(scanner, word);

			if (fIsVersionMatch || token.isUndefined())
				return token;

			return fDefaultToken;
		}
	}

	/**
	 * An annotation rule matches the '@' symbol, any following whitespace and
	 * optionally a following <code>interface</code> keyword.
	 *
	 * It does not match if there is a comment between the '@' symbol and
	 * the identifier. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=82452
	 *
	 * @since 3.1
	 */
	private static class AnnotationRule implements IRule, ISourceVersionDependent {
		/**
		 * A resettable scanner supports marking a position in a scanner and
		 * unreading back to the marked position.
		 */
		private static final class ResettableScanner implements ICharacterScanner {
			private final ICharacterScanner fDelegate;
			private int fReadCount;

			/**
			 * Creates a new resettable scanner that will forward calls
			 * to <code>scanner</code>, but store a marked position.
			 *
			 * @param scanner the delegate scanner
			 */
			public ResettableScanner(final ICharacterScanner scanner) {
				Assert.isNotNull(scanner);
				fDelegate= scanner;
				mark();
			}

			/*
			 * @see org.eclipse.jface.text.rules.ICharacterScanner#getColumn()
			 */
			public int getColumn() {
				return fDelegate.getColumn();
			}

			/*
			 * @see org.eclipse.jface.text.rules.ICharacterScanner#getLegalLineDelimiters()
			 */
			public char[][] getLegalLineDelimiters() {
				return fDelegate.getLegalLineDelimiters();
			}

			/*
			 * @see org.eclipse.jface.text.rules.ICharacterScanner#read()
			 */
			public int read() {
				int ch= fDelegate.read();
				if (ch != ICharacterScanner.EOF)
					fReadCount++;
				return ch;
			}

			/*
			 * @see org.eclipse.jface.text.rules.ICharacterScanner#unread()
			 */
			public void unread() {
				if (fReadCount > 0)
					fReadCount--;
				fDelegate.unread();
			}

			/**
			 * Marks an offset in the scanned content.
			 */
			public void mark() {
				fReadCount= 0;
			}

			/**
			 * Resets the scanner to the marked position.
			 */
			public void reset() {
				while (fReadCount > 0)
					unread();

				while (fReadCount < 0)
					read();
			}
		}

		private final IWhitespaceDetector fWhitespaceDetector= new JavaWhitespaceDetector();
		private final IWordDetector fWordDetector= new JavaWordDetector();
		private final IToken fInterfaceToken;
		private final IToken fAtToken;
		private final String fVersion;
		private boolean fIsVersionMatch;

		/**
		 * Creates a new rule.
		 *
		 * @param interfaceToken the token to return if
		 *        <code>'@\s*interface'</code> is matched
		 * @param atToken the token to return if <code>'@'</code>
		 *        is matched, but not <code>'@\s*interface'</code>
		 * @param version the lowest <code>JavaCore.COMPILER_SOURCE</code>
		 *        version that this rule is enabled
		 * @param currentVersion the current
		 *        <code>JavaCore.COMPILER_SOURCE</code> version
		 */
		public AnnotationRule(IToken interfaceToken, Token atToken, String version, String currentVersion) {
			fInterfaceToken= interfaceToken;
			fAtToken= atToken;
			fVersion= version;
			setSourceVersion(currentVersion);
		}

		/*
		 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {
			if (!fIsVersionMatch)
				return Token.UNDEFINED;

			ResettableScanner resettable= new ResettableScanner(scanner);
			if (resettable.read() == '@')
				return readAnnotation(resettable);

			resettable.reset();
			return Token.UNDEFINED;
		}

		private IToken readAnnotation(ResettableScanner scanner) {
			scanner.mark();
			skipWhitespace(scanner);
			if (readInterface(scanner)) {
				return fInterfaceToken;
			} else {
				scanner.reset();
				return fAtToken;
			}
		}

		private boolean readInterface(ICharacterScanner scanner) {
			int ch= scanner.read();
			int i= 0;
			while (i < INTERFACE.length() && INTERFACE.charAt(i) == ch) {
				i++;
				ch= scanner.read();
			}
			if (i < INTERFACE.length())
				return false;
			
			if (fWordDetector.isWordPart((char) ch))
				return false;
			
			if (ch != ICharacterScanner.EOF)
				scanner.unread();
			
			return true;
		}

		private boolean skipWhitespace(ICharacterScanner scanner) {
			while (fWhitespaceDetector.isWhitespace((char) scanner.read())) {
				// do nothing
			}

			scanner.unread();
			return true;
		}

		/*
		 * @see descent.internal.ui.text.ISourceVersionDependent#setSourceVersion(java.lang.String)
		 */
		public void setSourceVersion(String version) {
			fIsVersionMatch= fVersion.compareTo(version) <= 0; 
		}

	}

	private static final String SOURCE_VERSION= JavaCore.COMPILER_SOURCE;

	static String[] fgKeywords= {
		"abstract", "alias", "align", "asm", "assert", "auto",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"body", "break", "byte", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"case", "cast", "catch", "class", "const", "continue", //$NON-NLS-1$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$
		"debug", "default", "delegate", "delete", "deprecated", "do", //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-1$
		"else", "enum", "export", "extern", //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-1$
		"final", "finally", "for", "foreach", "foreach_reverse", "function", //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-2$ //$NON-NLS-1$
		"goto", //$NON-NLS-1$
		"if", "iftype", "import", "in", "inout", "interface", "invariant", "is", //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"lazy",  //$NON-NLS-1$
		"mixin", "module", //$NON-NLS-1$ //$NON-NLS-2$
		"new", //$NON-NLS-1$
		"out", "override",  //$NON-NLS-1$ //$NON-NLS-2$
		"package", "pragma", "private", "protected", "public", //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"scope", "static", "struct", "super", "switch", "synchronized", //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"template", "this", "throw", "try", "typedef", "typeid", "typeof", //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"union", "unittest",  //$NON-NLS-1$ //$NON-NLS-2$
		"version", "volatile", //$NON-NLS-1$ //$NON-NLS-2$
		"while", "with", //$NON-NLS-1$ //$NON-NLS-2$
	};

	private static final String INTERFACE= "interface";  //$NON-NLS-1$
	private static final String RETURN= "return"; //$NON-NLS-1$

	private static String[] fgTypes= { 
		"bool", "byte",  //$NON-NLS-1$ //$NON-NLS-2$
		"cdouble", "cent", "cfloat", "char", "creal",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"dchar", "double", //$NON-NLS-1$ //$NON-NLS-2$
		"float", //$NON-NLS-1$
		"idouble", "ifloat", "int", "ireal", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"long",  //$NON-NLS-1$
		"real",  //$NON-NLS-1$
		"short", //$NON-NLS-1$
		"ubyte", "ucent", "uint", "ulong", "ushort", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"void",  //$NON-NLS-1$
		"wchar", //$NON-NLS-1$
		}; 

	private static String[] fgConstants= { "false", "null", "true" }; //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$

	private static final String ANNOTATION_BASE_KEY= PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX + SemanticHighlightings.ANNOTATION;
	private static final String ANNOTATION_COLOR_KEY= ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_COLOR_SUFFIX;

	private static String[] fgTokenProperties= {
		IJavaColorConstants.JAVA_KEYWORD,
		IJavaColorConstants.JAVA_STRING,
		IJavaColorConstants.JAVA_DEFAULT,
		IJavaColorConstants.JAVA_KEYWORD_RETURN,
		IJavaColorConstants.JAVA_OPERATOR,
		ANNOTATION_COLOR_KEY,
	};

	private List fVersionDependentRules= new ArrayList(3);

	/**
	 * Creates a Java code scanner
	 *
	 * @param manager	the color manager
	 * @param store		the preference store
	 */
	public JavaCodeScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		initialize();
	}

	/*
	 * @see AbstractJavaScanner#getTokenProperties()
	 */
	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}

	/*
	 * @see AbstractJavaScanner#createRules()
	 */
	protected List createRules() {

		List rules= new ArrayList();

		// Add rule for character constants.
		Token token= getToken(IJavaColorConstants.JAVA_STRING);
		rules.add(new SingleLineRule("'", "'", token, '\\')); //$NON-NLS-2$ //$NON-NLS-1$


		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));

		String version= getPreferenceStore().getString(SOURCE_VERSION);

		// Add JLS3 rule for /@\s*interface/ and /@\s*\w+/
		token= getToken(ANNOTATION_COLOR_KEY);
		AnnotationRule atInterfaceRule= new AnnotationRule(getToken(IJavaColorConstants.JAVA_KEYWORD), token, JavaCore.VERSION_1_5, version);
		rules.add(atInterfaceRule);
		fVersionDependentRules.add(atInterfaceRule);

		// Add word rule for new keywords, 4077
		JavaWordDetector wordDetector= new JavaWordDetector();
		token= getToken(IJavaColorConstants.JAVA_DEFAULT);
		CombinedWordRule combinedWordRule= new CombinedWordRule(wordDetector, token);

		token= getToken(IJavaColorConstants.JAVA_DEFAULT);
		VersionedWordMatcher j14Matcher= new VersionedWordMatcher(token, JavaCore.VERSION_1_4, version);

		token= getToken(IJavaColorConstants.JAVA_KEYWORD);

		combinedWordRule.addWordMatcher(j14Matcher);
		fVersionDependentRules.add(j14Matcher);

		token= getToken(IJavaColorConstants.JAVA_DEFAULT);
		VersionedWordMatcher j15Matcher= new VersionedWordMatcher(token, JavaCore.VERSION_1_5, version);
		token= getToken(IJavaColorConstants.JAVA_KEYWORD);

		combinedWordRule.addWordMatcher(j15Matcher);
		fVersionDependentRules.add(j15Matcher);

		// Add rule for operators and brackets
		token= getToken(IJavaColorConstants.JAVA_OPERATOR);
		rules.add(new OperatorRule(token));

		// Add word rule for keyword 'return'.
		CombinedWordRule.WordMatcher returnWordRule= new CombinedWordRule.WordMatcher();
		token= getToken(IJavaColorConstants.JAVA_KEYWORD_RETURN);
		returnWordRule.addWord(RETURN, token);  
		combinedWordRule.addWordMatcher(returnWordRule);

		// Add word rule for keywords, types, and constants.
		CombinedWordRule.WordMatcher wordRule= new CombinedWordRule.WordMatcher();
		token= getToken(IJavaColorConstants.JAVA_KEYWORD);
		for (int i=0; i<fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], token);
		for (int i=0; i<fgTypes.length; i++)
			wordRule.addWord(fgTypes[i], token);
		for (int i=0; i<fgConstants.length; i++)
			wordRule.addWord(fgConstants[i], token);

		combinedWordRule.addWordMatcher(wordRule);

		rules.add(combinedWordRule);

		setDefaultReturnToken(getToken(IJavaColorConstants.JAVA_DEFAULT));
		return rules;
	}
	
	/*
	 * @see descent.internal.ui.text.AbstractJavaScanner#getBoldKey(java.lang.String)
	 */
	protected String getBoldKey(String colorKey) {
		if ((ANNOTATION_COLOR_KEY).equals(colorKey))
			return ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_BOLD_SUFFIX;
		return super.getBoldKey(colorKey);
	}
	
	/*
	 * @see descent.internal.ui.text.AbstractJavaScanner#getItalicKey(java.lang.String)
	 */
	protected String getItalicKey(String colorKey) {
		if ((ANNOTATION_COLOR_KEY).equals(colorKey))
			return ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ITALIC_SUFFIX;
		return super.getItalicKey(colorKey);
	}
	
	/*
	 * @see descent.internal.ui.text.AbstractJavaScanner#getStrikethroughKey(java.lang.String)
	 */
	protected String getStrikethroughKey(String colorKey) {
		if ((ANNOTATION_COLOR_KEY).equals(colorKey))
			return ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_STRIKETHROUGH_SUFFIX;
		return super.getStrikethroughKey(colorKey);
	}
	
	/*
	 * @see descent.internal.ui.text.AbstractJavaScanner#getUnderlineKey(java.lang.String)
	 */
	protected String getUnderlineKey(String colorKey) {
		if ((ANNOTATION_COLOR_KEY).equals(colorKey))
			return ANNOTATION_BASE_KEY + PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_UNDERLINE_SUFFIX;
		return super.getUnderlineKey(colorKey);
	}

	/*
	 * @see AbstractJavaScanner#affectsBehavior(PropertyChangeEvent)
	 */
	public boolean affectsBehavior(PropertyChangeEvent event) {
		return event.getProperty().equals(SOURCE_VERSION) || super.affectsBehavior(event);
	}

	/*
	 * @see AbstractJavaScanner#adaptToPreferenceChange(PropertyChangeEvent)
	 */
	public void adaptToPreferenceChange(PropertyChangeEvent event) {

		if (event.getProperty().equals(SOURCE_VERSION)) {
			Object value= event.getNewValue();

			if (value instanceof String) {
				String s= (String) value;

				for (Iterator it= fVersionDependentRules.iterator(); it.hasNext();) {
					ISourceVersionDependent dependent= (ISourceVersionDependent) it.next();
					dependent.setSourceVersion(s);
				}
			}

		} else if (super.affectsBehavior(event)) {
			super.adaptToPreferenceChange(event);
		}
	}
}
