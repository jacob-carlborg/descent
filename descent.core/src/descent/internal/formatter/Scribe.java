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
package descent.internal.formatter;

import descent.core.compiler.IScanner;
import descent.internal.formatter.align.Alignment;

/**
 * This class is responsible for dumping formatted source
 * @since 2.1
 * 
 * TODO JDT format stub
 */
public class Scribe {
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final int INITIAL_SIZE = 100;
	
	private boolean checkLineWrapping;
	/** one-based column */
	public int column;
	private int[][] commentPositions;
		
	// Most specific alignment. 
	public Alignment currentAlignment;
	public int currentToken;
	
	// edits management
	private OptimizedReplaceEdit[] edits;
	public int editsIndex;
	
	//public CodeFormatterVisitor formatter;
	public int indentationLevel;	
	public int lastNumberOfNewLines;
	public int line;
	
	private int[] lineEnds;
	private String lineSeparator;
	public Alignment memberAlignment;
	public boolean needSpace = false;
	
	public int nlsTagCounter;
	public int pageWidth;
	public boolean pendingSpace = false;

	public IScanner scanner;
	public int scannerEndPosition;
	public int tabLength;	
	public int indentationSize;	
	private int textRegionEnd;
	private int textRegionStart;
	public int tabChar;
	public int numberOfIndentations;
	private boolean useTabsOnlyForLeadingIndents;

    /** indent empty lines*/
    private boolean indentEmptyLines;
    
    public int getNextIndentationLevel(int someColumn) {
    	return 0;
    }
    
    public void printNewLine() {
		
	}
    
    public OptimizedReplaceEdit getLastEdit() {
    	return null;
    }
    
}
