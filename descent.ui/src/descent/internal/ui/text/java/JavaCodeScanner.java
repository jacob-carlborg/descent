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
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.util.PropertyChangeEvent;

import descent.core.JavaCore;
import descent.internal.ui.text.AbstractJavaScanner;
import descent.internal.ui.text.CombinedWordRule;
import descent.internal.ui.text.ISourceVersionDependent;
import descent.internal.ui.text.JavaWhitespaceDetector;
import descent.internal.ui.text.JavaWordDetector;
import descent.ui.text.IColorManager;
import descent.ui.text.IJavaColorConstants;


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
		private final char[] JAVA_OPERATORS= { ';', '(', ')', '{', '}', '.', '=', 
				'/', '\\', '+', '-', '*', '[', ']', '<', '>', ':', '?', '!', ',', 
				'|', '&', '^', '%', '~'};
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

	private static final String SOURCE_VERSION= JavaCore.COMPILER_SOURCE;

	static String[] fgKeywords= {
		"abstract", "alias", "align", "asm", "assert", "auto",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"body", "break", //$NON-NLS-1$ //$NON-NLS-2$
		"case", "cast", "catch", "class", "const", "continue", //$NON-NLS-1$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$
		"debug", "default", "delegate", "delete", "deprecated", "do", //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-1$
		"else", "enum", "export", "extern", //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-1$
		"final", "finally", "for", "foreach", "foreach_reverse", "function", //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-2$ //$NON-NLS-1$
		"goto", //$NON-NLS-1$
		"if", "iftype", "import", "in", "inout", "interface", "invariant", "is", //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"lazy",  //$NON-NLS-1$
		"macro", "mixin", "module", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"new", //$NON-NLS-1$
		"out", "override",  //$NON-NLS-1$ //$NON-NLS-2$
		"package", "pragma", "private", "protected", "public", //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"ref",  //$NON-NLS-1$
		"scope", "static", "struct", "super", "switch", "synchronized", //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"template", "this", "throw", "try", "typedef", "typeid", "typeof", //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"union", "unittest",  //$NON-NLS-1$ //$NON-NLS-2$
		"version", "volatile", //$NON-NLS-1$ //$NON-NLS-2$
		"while", "with", //$NON-NLS-1$ //$NON-NLS-2$
	};
	
	static String[] fgKeywords2= {
		"__traits", //$NON-NLS-1$,
		"__overloadset", //$NON-NLS-1$
		"nothrow", //$NON-NLS-1$
		"pure", //$NON-NLS-1$
	};

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
	
	private static String[] fgSpecialTokens= { 
		"__FILE__", "__LINE__", "__DATE__", "__TIME__", "__TIMESTAMP__", "__VENDOR__", "__VERSION__"       //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$//$NON-NLS-7$
		}; 
	private static String[] fgSpecialTokens2= { 
		"__EOF__",
		};

	private static String[] fgConstants= { "false", "null", "true" }; //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$

	private static String[] fgTokenProperties= {
		IJavaColorConstants.JAVA_KEYWORD,
		IJavaColorConstants.JAVA_STRING,
		IJavaColorConstants.JAVA_DEFAULT,
		IJavaColorConstants.JAVA_KEYWORD_RETURN,
		IJavaColorConstants.JAVA_SPECIAL_TOKEN,
		IJavaColorConstants.JAVA_OPERATOR,
	};

	private List fVersionDependentRules= new ArrayList(1);

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

		// Add word rule for new keywords, 4077
		JavaWordDetector wordDetector= new JavaWordDetector();
		token= getToken(IJavaColorConstants.JAVA_DEFAULT);
		CombinedWordRule combinedWordRule= new CombinedWordRule(wordDetector, token);

		token= getToken(IJavaColorConstants.JAVA_DEFAULT);
		VersionedWordMatcher j2Matcher= new VersionedWordMatcher(token, JavaCore.VERSION_2_x, version);

		token= getToken(IJavaColorConstants.JAVA_KEYWORD);
		for (int i=0; i<fgKeywords2.length; i++)
			j2Matcher.addWord(fgKeywords2[i], token);
		
		token= getToken(IJavaColorConstants.JAVA_SPECIAL_TOKEN);
		for (int i=0; i<fgSpecialTokens2.length; i++)
			j2Matcher.addWord(fgSpecialTokens2[i], token);

		combinedWordRule.addWordMatcher(j2Matcher);
		fVersionDependentRules.add(j2Matcher);

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
		
		// Add word rule for special tokens.
		CombinedWordRule.WordMatcher stWordRule= new CombinedWordRule.WordMatcher();
		token= getToken(IJavaColorConstants.JAVA_SPECIAL_TOKEN);
		for (int i=0; i<fgSpecialTokens.length; i++)
			stWordRule.addWord(fgSpecialTokens[i], token);
		
		combinedWordRule.addWordMatcher(stWordRule);

		rules.add(combinedWordRule);

		setDefaultReturnToken(getToken(IJavaColorConstants.JAVA_DEFAULT));
		return rules;
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
