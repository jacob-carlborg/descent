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
package descent.internal.compiler.problem;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import descent.core.compiler.IProblem;
import descent.internal.compiler.IProblemFactory;
import descent.internal.compiler.util.HashtableOfInt;

public class DefaultProblemFactory implements IProblemFactory {

	public HashtableOfInt messageTemplates;
	private Locale locale;
	private static HashtableOfInt DEFAULT_LOCALE_TEMPLATES;
	private final static char[] DOUBLE_QUOTES = "''".toCharArray(); //$NON-NLS-1$
	private final static char[] SINGLE_QUOTE = "'".toCharArray(); //$NON-NLS-1$

public DefaultProblemFactory() {
	this(Locale.getDefault());
}
/**
 * @param loc the locale used to get the right message
 */
public DefaultProblemFactory(Locale loc) {
	this.locale = loc;
	/* TODO JDT problems
	if (Locale.getDefault().equals(loc)){
		if (DEFAULT_LOCALE_TEMPLATES == null){
			DEFAULT_LOCALE_TEMPLATES = loadMessageTemplates(loc);
		}
		this.messageTemplates = DEFAULT_LOCALE_TEMPLATES;
	} else {
		this.messageTemplates = loadMessageTemplates(loc);
	}
	*/
}
/**
 * Answer a new IProblem created according to the parameters value
 * <ul>
 * <li>originatingFileName the name of the file name from which the problem is originated
 * <li>problemId the problem id
 * <li>problemArguments the fully qualified arguments recorded inside the problem
 * <li>messageArguments the arguments needed to set the error message (shorter names than problemArguments ones)
 * <li>severity the severity of the problem
 * <li>startPosition the starting position of the problem
 * <li>endPosition the end position of the problem
 * <li>lineNumber the line on which the problem occured
 * </ul>
 * @param originatingFileName char[]
 * @param problemId int
 * @param problemArguments String[]
 * @param messageArguments String[]
 * @param severity int
 * @param startPosition int
 * @param endPosition int
 * @param lineNumber int
 * @return CategorizedProblem
 */
public IProblem createProblem(
	char[] originatingFileName, 
	int problemId, 
	String[] problemArguments, 
	String[] messageArguments, 
	int severity, 
	int startPosition, 
	int endPosition, 
	int lineNumber) {

	/* TODO JDT problems
	return new DefaultProblem(
		originatingFileName, 
		this.getLocalizedMessage(problemId, messageArguments),
		problemId, 
		problemArguments, 
		severity, 
		startPosition, 
		endPosition, 
		lineNumber);
	*/
	return null;
}
private final static int keyFromID(int id) {
    return id + 1; // keys are offsetted by one in table, since it cannot handle 0 key
}
/**
 * Answer the locale used to retrieve the error messages
 * @return java.util.Locale
 */
public Locale getLocale() {
	return this.locale;
}
public final String getLocalizedMessage(int id, String[] problemArguments) {
	// TODO JDT problems
	return null;
}
/**
 * @param problem CategorizedProblem
 * @return String
 */
public final String localizedMessage(IProblem problem) {
	return getLocalizedMessage(problem.getID(), problem.getArguments());
}

/**
 * This method initializes the MessageTemplates class variable according
 * to the current Locale.
 * @param loc Locale
 * @return HashtableOfInt
 */
public static HashtableOfInt loadMessageTemplates(Locale loc) {
	ResourceBundle bundle = null;
	String bundleName = "descent.internal.compiler.problem.messages"; //$NON-NLS-1$
	try {
		bundle = ResourceBundle.getBundle(bundleName, loc); 
	} catch(MissingResourceException e) {
		System.out.println("Missing resource : " + bundleName.replace('.', '/') + ".properties for locale " + loc); //$NON-NLS-1$//$NON-NLS-2$
		throw e;
	}
	HashtableOfInt templates = new HashtableOfInt(700);
	Enumeration keys = bundle.getKeys();
	while (keys.hasMoreElements()) {
	    String key = (String)keys.nextElement();
	    try {
	        int messageID = Integer.parseInt(key);
			templates.put(keyFromID(messageID), bundle.getString(key));
	    } catch(NumberFormatException e) {
	        // key ill-formed
		} catch (MissingResourceException e) {
			// available ID
	    }
	}
	return templates;
}

}
