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

import java.util.Map;

import descent.core.compiler.IScanner;
import descent.core.dom.AST;
import descent.core.dom.PublicScanner;
import descent.core.formatter.CodeFormatter;
import descent.internal.formatter.DefaultCodeFormatter;

/**
 * Factory for creating various compiler tools, such as scanners.
 * <p>
 *  This class provides static methods only; it is not intended to be instantiated or subclassed by clients.
 * </p>
 */
public class ToolFactory {

	/**
	 * Create a scanner, indicating the level of detail requested for tokenizing. The scanner can then be
	 * used to tokenize some source in a Java aware way.
	 * Here is a typical scanning loop:
	 * 
	 * <code>
	 * <pre>
	 *   IScanner scanner = ToolFactory.createScanner(false, false, false, AST.D1);
	 *   scanner.setSource("int i = 0;".toCharArray());
	 *   while (true) {
	 *     int token = scanner.getNextToken();
	 *     if (token == ITerminalSymbols.TokenNameEOF) break;
	 *     System.out.println(token + " : " + new String(scanner.getCurrentTokenSource()));
	 *   }
	 * </pre>
	 * </code>
	 * @param tokenizeComments if set to <code>false</code>, comments will be silently consumed
	 * @param tokenizeWhiteSpace if set to <code>false</code>, white spaces will be silently consumed,
	 * @param tokenizePragmas if set to <code>false</code>, pragmas will be silently consumed,
	 * @param recordLineSeparator if set to <code>true</code>, the scanner will record positions of encountered line 
	 * separator ends. In case of multi-character line separators, the last character position is considered. These positions
	 * can then be extracted using <code>IScanner#getLineEnds</code>. Only non-unicode escape sequences are 
	 * considered as valid line separators.
	 * @param apiLevel one of AST constants indicating the level of compatibility
  	 * @return a scanner
	 * @see descent.core.compiler.IScanner
	 */
	public static IScanner createScanner(boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator, int apiLevel) {
		return new PublicScanner(tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator, apiLevel);
	}
	

	/**
	 * Create a scanner for the latest D version (currently 2.0). The scanner can then be
	 * used to tokenize some source in a Java aware way.
	 * Here is a typical scanning loop:
	 * 
	 * <code>
	 * <pre>
	 *   IScanner scanner = ToolFactory.createScanner(false, false, false, AST.D1);
	 *   scanner.setSource("int i = 0;".toCharArray());
	 *   while (true) {
	 *     int token = scanner.getNextToken();
	 *     if (token == ITerminalSymbols.TokenNameEOF) break;
	 *     System.out.println(token + " : " + new String(scanner.getCurrentTokenSource()));
	 *   }
	 * </pre>
	 * </code>
	 * @param tokenizeComments if set to <code>false</code>, comments will be silently consumed
	 * @param tokenizeWhiteSpace if set to <code>false</code>, white spaces will be silently consumed,
	 * @param tokenizePragmas if set to <code>false</code>, pragmas will be silently consumed,
	 * @param recordLineSeparator if set to <code>true</code>, the scanner will record positions of encountered line 
	 * separator ends. In case of multi-character line separators, the last character position is considered. These positions
	 * can then be extracted using <code>IScanner#getLineEnds</code>. Only non-unicode escape sequences are 
	 * considered as valid line separators.
	 * @param apiLevel one of AST constants indicating the level of compatibility
  	 * @return a scanner
	 * @see descent.core.compiler.IScanner
	 */
	public static IScanner createScanner(boolean tokenizeComments, boolean tokenizePragmas, boolean tokenizeWhiteSpace, boolean recordLineSeparator) {
		return new PublicScanner(tokenizeComments, tokenizePragmas, tokenizeWhiteSpace, recordLineSeparator, AST.D2);
	}
	
	/**
	 * Create an instance of the built-in code formatter.
	 * <p>The given options should at least provide the source level ({@link JavaCore#COMPILER_SOURCE}),
	 * the  compiler compliance level ({@link JavaCore#COMPILER_COMPLIANCE}) and the target platform
	 * ({@link JavaCore#COMPILER_CODEGEN_TARGET_PLATFORM}).
	 * Without these options, it is not possible for the code formatter to know what kind of source it needs to format.
	 * </p>
	 * @param options - the options map to use for formatting with the default code formatter. Recognized options
	 * 	are documented on <code>JavaCore#getDefaultOptions()</code>. If set to <code>null</code>, then use 
	 * 	the current settings from <code>JavaCore#getOptions</code>.
	 * @return an instance of the built-in code formatter
	 * @see CodeFormatter
	 * @see JavaCore#getOptions()
	 * @since 3.0
	 */
	public static CodeFormatter createCodeFormatter(Map options){
		if (options == null) options = DMDCore.getOptions();
		return new DefaultCodeFormatter(options);
	}
	
}
