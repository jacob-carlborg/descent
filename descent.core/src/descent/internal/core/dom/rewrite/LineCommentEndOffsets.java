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

package descent.internal.core.dom.rewrite;

import java.util.Arrays;
import java.util.List;

import descent.core.dom.Comment;
import descent.core.formatter.IndentManipulation;

public class LineCommentEndOffsets {
	
	private int[] offsets;
	private final List<Comment> commentList;
	
	public LineCommentEndOffsets(List<Comment> commentList) {
		this.commentList= commentList;
		this.offsets= null; // create on demand
	}
	
	private int[] getOffsets() {
		if (this.offsets == null) {
			if (this.commentList != null) {
				int nComments= this.commentList.size();
				// count the number of line comments
				int count= 0;
				for (int i= 0; i < nComments; i++) {
					Comment curr= this.commentList.get(i);
					if (curr.getKind() == Comment.Kind.LINE_COMMENT) {
						count++;
					}
				}
				// fill the offset table
				this.offsets= new int[count];
				for (int i= 0, k= 0; i < nComments; i++) {
					Comment curr= this.commentList.get(i);
					if (curr.getKind() == Comment.Kind.LINE_COMMENT) {
						this.offsets[k++]= curr.getStartPosition() + curr.getLength();
					}
				}
			} else {
				this.offsets= new int[0];
			}
		}
		return this.offsets;
	}
	
	public boolean isEndOfLineComment(int offset) {
		return offset >= 0 && Arrays.binarySearch(getOffsets(), offset) >= 0;
	}
	
	public boolean isEndOfLineComment(int offset, char[] content) {
		if (offset < 0 || (offset < content.length && !IndentManipulation.isLineDelimiterChar(content[offset]))) {
			return false;
		}
		return Arrays.binarySearch(getOffsets(), offset) >= 0;
	}
	
	public boolean remove(int offset) {
		int[] offsetArray= getOffsets(); // returns the shared array
		int index= Arrays.binarySearch(offsetArray, offset);
		if (index >= 0) {
			if (index > 0) {
				// shift from the beginning and insert -1 (smallest number) at the beginning
				// 1, 2, 3, x, 4, 5 -> -1, 1, 2, 3, 4, 5
				System.arraycopy(offsetArray, 0, offsetArray, 1, index);
			}
			offsetArray[0]= -1;
			return true;
		}
		return false;
	}
	
}
