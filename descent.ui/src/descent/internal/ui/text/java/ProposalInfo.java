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


import java.io.IOException;
import java.io.Reader;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.JavaModelException;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.text.javadoc.JavaDoc2HTMLTextReader;
import descent.ui.JavadocContentAccess;


public class ProposalInfo {

	private boolean fJavadocResolved= false;
	private String fJavadoc= null;

	protected IJavaElement fElement;

	public ProposalInfo(IMember member) {
		fElement= member;
	}
	
	protected ProposalInfo() {
		fElement= null;
	}

	public IJavaElement getJavaElement() throws JavaModelException {
		return fElement;
	}

	/**
	 * Gets the text for this proposal info formatted as HTML, or
	 * <code>null</code> if no text is available.
	 *
	 * @param monitor a progress monitor
	 * @return the additional info text
	 */
	public final String getInfo(IProgressMonitor monitor) {
		if (!fJavadocResolved) {
			fJavadocResolved= true;
			fJavadoc= computeInfo(monitor);
		}
		return fJavadoc;
	}

	/**
	 * Gets the text for this proposal info formatted as HTML, or
	 * <code>null</code> if no text is available.
	 *
	 * @param monitor a progress monitor
	 * @return the additional info text
	 */
	private String computeInfo(IProgressMonitor monitor) {
		try {
			final IJavaElement javaElement= getJavaElement();
			if (javaElement instanceof IMember) {
				IMember member= (IMember) javaElement;
				return extractJavadoc(member, monitor);
			}
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		} catch (IOException e) {
			JavaPlugin.log(e);
		}
		return null;
	}

	/**
	 * Extracts the javadoc for the given <code>IMember</code> and returns it
	 * as HTML.
	 *
	 * @param member the member to get the documentation for
	 * @param monitor a progress monitor
	 * @return the javadoc for <code>member</code> or <code>null</code> if
	 *         it is not available
	 * @throws JavaModelException if accessing the javadoc fails
	 * @throws IOException if reading the javadoc fails
	 */
	private String extractJavadoc(IMember member, IProgressMonitor monitor) throws JavaModelException, IOException {
		if (member != null) {
			Reader reader=  getHTMLContentReader(member, monitor);
			if (reader != null)
				return getString(reader);
		}
		return null;
	}

	private Reader getHTMLContentReader(IMember member, IProgressMonitor monitor) throws JavaModelException {
	    Reader contentReader= JavadocContentAccess.getContentReader(member, true);
        if (contentReader != null)
        	return new JavaDoc2HTMLTextReader(contentReader);
        
        if (true && member.getOpenable().getBuffer() == null) { // only if no source available
        	/* TODO JDT UI attached javadoc
        	String s= member.getAttachedJavadoc(monitor);
        	if (s != null)
        		return new StringReader(s);
        	*/
        }
        return null;
    }
	
	/**
	 * Gets the reader content as a String
	 */
	private static String getString(Reader reader) {
		StringBuffer buf= new StringBuffer();
		char[] buffer= new char[1024];
		int count;
		try {
			while ((count= reader.read(buffer)) != -1)
				buf.append(buffer, 0, count);
		} catch (IOException e) {
			return null;
		}
		return buf.toString();
	}
}
