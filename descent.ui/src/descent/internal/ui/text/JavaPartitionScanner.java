/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.text;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import descent.ui.text.IJavaPartitions;
import descent.ui.text.rules.DEscapeRule;
import descent.ui.text.rules.DStringRule;
import descent.ui.text.rules.NestedCommentRule;


/**
 * This scanner recognizes the JavaDoc comments and Java multi line comments.
 */
public class JavaPartitionScanner extends RuleBasedPartitionScanner implements IJavaPartitions {

	/**
	 * Detector for empty comments.
	 */
	static class EmptyCommentDetector implements IWordDetector {

		/*
		 * @see IWordDetector#isWordStart
		 */
		public boolean isWordStart(char c) {
			return (c == '/');
		}

		/*
		 * @see IWordDetector#isWordPart
		 */
		public boolean isWordPart(char c) {
			return (c == '*' || c == '/');
		}
	}


	/**
	 * Word rule for empty comments.
	 */
	static class EmptyCommentRule extends WordRule implements IPredicateRule {

		private IToken fSuccessToken;
		/**
		 * Constructor for EmptyCommentRule.
		 * @param successToken
		 */
		public EmptyCommentRule(IToken successToken) {
			super(new EmptyCommentDetector());
			fSuccessToken= successToken;
			addWord("/**/", fSuccessToken); //$NON-NLS-1$
		}

		/*
		 * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
		 */
		public IToken evaluate(ICharacterScanner scanner, boolean resume) {
			return evaluate(scanner);
		}

		/*
		 * @see IPredicateRule#getSuccessToken()
		 */
		public IToken getSuccessToken() {
			return fSuccessToken;
		}
	}	

	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 */
	public JavaPartitionScanner() {
		super();

		// TODO JDT UI update according to D partitions
		IToken string= new Token(JAVA_STRING);
		IToken character= new Token(JAVA_CHARACTER);
		IToken javaDoc= new Token(JAVA_DOC);
		IToken multiLineComment= new Token(JAVA_MULTI_LINE_COMMENT);
		IToken singleLineComment= new Token(JAVA_SINGLE_LINE_COMMENT);
		
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		
		rules.add(new NestedCommentRule("/++", "/+", "+/", javaDoc, (char) 0, true));
		rules.add(new NestedCommentRule("/+", "/+", "+/", multiLineComment, (char) 0, true));
		rules.add(new MultiLineRule("/**", "*/", javaDoc, (char) 0, true));
		rules.add(new MultiLineRule("/*", "*/", multiLineComment, (char) 0, true));

		rules.add(new EndOfLineRule("///", javaDoc));
		rules.add(new EndOfLineRule("//", singleLineComment));
		
		rules.add(new DStringRule("x\"", "\"", string, (char) 0));
		
		rules.add(new DStringRule("r\"", "\"", string, (char) 0));
		rules.add(new DStringRule("`", "`", string, (char) 0));
		
		rules.add(new DStringRule("\"", "\"", string, '\\'));
		
		rules.add(new DEscapeRule(string));
		
		rules.add(new SingleLineRule("'", "'", character, '\\', true));
		
		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}
}
