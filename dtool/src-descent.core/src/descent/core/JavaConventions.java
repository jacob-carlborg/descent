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
package descent.core;

import org.eclipse.core.runtime.IPath;

import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.compiler.InvalidInputException;
import descent.core.dom.AST;
import descent.internal.compiler.lookup.TypeConstants;
import descent.internal.compiler.util.SuffixConstants;

/**
 * Provides methods for checking Java-specific conventions such as name syntax.
 * <p>
 * This class provides static methods and constants only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 */
public final class JavaConventions {

	private final static char DOT= '.';
	private static final String PACKAGE_INFO = new String(TypeConstants.PACKAGE_INFO_NAME);
	private final static IScanner SCANNER = ToolFactory.createScanner(true, true, true, false, AST.D2);

	private JavaConventions() {
		// Not instantiable
	}

	/**
	 * Returns whether the given package fragment root paths are considered
	 * to overlap.
	 * <p>
	 * Two root paths overlap if one is a prefix of the other, or they point to
	 * the same location. However, a JAR is allowed to be nested in a root.
	 *
	 * @param rootPath1 the first root path
	 * @param rootPath2 the second root path
	 * @return true if the given package fragment root paths are considered to overlap, false otherwise
	 * @deprecated Overlapping roots are allowed in 2.1
	 */
	public static boolean isOverlappingRoots(IPath rootPath1, IPath rootPath2) {
		if (rootPath1 == null || rootPath2 == null) {
			return false;
		}
		String extension1 = rootPath1.getFileExtension();
		String extension2 = rootPath2.getFileExtension();
		if (extension1 != null && (extension1.equalsIgnoreCase(SuffixConstants.EXTENSION_JAR) || extension1.equalsIgnoreCase(SuffixConstants.EXTENSION_ZIP))) {
			return false;
		} 
		if (extension2 != null && (extension2.equalsIgnoreCase(SuffixConstants.EXTENSION_JAR) || extension2.equalsIgnoreCase(SuffixConstants.EXTENSION_ZIP))) {
			return false;
		}
		return rootPath1.isPrefixOf(rootPath2) || rootPath2.isPrefixOf(rootPath1);
	}

	/*
	 * Returns the current identifier extracted by the scanner (without unicode
	 * escapes) from the given id.
	 * Returns <code>null</code> if the id was not valid
	 */
	static synchronized char[] scannedIdentifier(String id) {
		if (id == null) {
			return null;
		}
		String trimmed = id.trim();
		if (!trimmed.equals(id)) {
			return null;
		}
		try {
			SCANNER.setSource(id.toCharArray());
			int token = SCANNER.getNextToken();
			char[] currentIdentifier;
			try {
				currentIdentifier = SCANNER.getRawTokenSource();
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
			int nextToken= SCANNER.getNextToken();
			if (token == ITerminalSymbols.TokenNameIdentifier 
				&& nextToken == ITerminalSymbols.TokenNameEOF
				&& SCANNER.getCurrentTokenEndPosition() == id.length()) { // to handle case where we had an ArrayIndexOutOfBoundsException 
																     // while reading the last token
				return currentIdentifier;
			} else {
				return null;
			}
		}
		catch (InvalidInputException e) {
			return null;
		}
	}

}
