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

package descent.internal.ui.text.javadoc;

/**
 * Html tag constants.
 *
 * @since 3.0
 */
public interface IHtmlTagConstants {

	/** Html tag close prefix */
	public static final String HTML_CLOSE_PREFIX= "</"; //$NON-NLS-1$

	/** Html entity characters */
	public static final char[] HTML_ENTITY_CHARACTERS= new char[] { '<', '>', ' ', '&', '^', '~', '\"' };

	/** Html entity codes */
	public static final String[] HTML_ENTITY_CODES= new String[] { "&lt;", "&gt;", "&nbsp;", "&amp;", "&circ;", "&tilde;", "&quot;" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	/** Html general tags */
	public static final String[] HTML_GENERAL_TAGS= new String[] { "b", "blockquote", "br", "code", "dd", "dl", "dt", "em", "hr", "h1", "h2", "h3", "h4", "h5", "h6", "i", "li", "nl", "ol", "p", "pre", "q", "strong", "td", "th", "tr", "tt", "ul" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$ //$NON-NLS-21$ //$NON-NLS-22$ //$NON-NLS-23$ //$NON-NLS-24$ //$NON-NLS-25$ //$NON-NLS-26$ //$NON-NLS-27$ //$NON-NLS-28$

	/** Html tag postfix */
	public static final char HTML_TAG_POSTFIX= '>';

	/** Html tag prefix */
	public static final char HTML_TAG_PREFIX= '<';
}
