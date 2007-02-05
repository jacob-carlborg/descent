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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Assert;
import org.eclipse.text.edits.CopySourceEdit;
import org.eclipse.text.edits.CopyTargetEdit;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MoveSourceEdit;
import org.eclipse.text.edits.MoveTargetEdit;
import org.eclipse.text.edits.RangeMarker;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.dom.*;
import descent.core.dom.AggregateDeclaration.Kind;
import descent.core.dom.Argument.PassageMode;
import descent.core.dom.ExternDeclaration.Linkage;
import descent.core.dom.rewrite.TargetSourceRangeComputer;
import descent.core.dom.rewrite.TargetSourceRangeComputer.SourceRange;
import descent.core.formatter.IndentManipulation;
import descent.internal.compiler.parser.ScannerHelper;
import descent.internal.core.dom.rewrite.ASTRewriteFormatter.BlockContext;
import descent.internal.core.dom.rewrite.ASTRewriteFormatter.NodeMarker;
import descent.internal.core.dom.rewrite.ASTRewriteFormatter.Prefix;
import descent.internal.core.dom.rewrite.NodeInfoStore.CopyPlaceholderData;
import descent.internal.core.dom.rewrite.NodeInfoStore.StringPlaceholderData;
import descent.internal.core.dom.rewrite.RewriteEventStore.CopySourceInfo;


/**
 * Infrastructure to support code modifications. Existing code must stay untouched, new code
 * added with correct formatting, moved code left with the user's formatting / comments.
 * Idea:
 * - Get the AST for existing code 
 * - Describe changes
 * - This visitor analyzes the changes or annotations and generates text edits
 * (text manipulation API) that describe the required code changes. 
 */
public final class ASTRewriteAnalyzer extends ASTVisitor {
	
	class ListRewriter {
		protected String contantSeparator;
		protected int startPos;
		
		protected RewriteEvent[] list;
		
		protected int getEndOfNode(ASTNode node) {
			return getExtendedEnd(node);
		}
		
		protected int getInitialIndent() {
			return getIndent(this.startPos);
		}
		
		protected final ASTNode getNewNode(int index) {
			return (ASTNode) this.list[index].getNewValue();
		}
		
		protected int getNodeIndent(int nodeIndex) {
			ASTNode node= getOriginalNode(nodeIndex);
			if (node == null) {
				for (int i= nodeIndex - 1; i>= 0; i--) {
					ASTNode curr= getOriginalNode(i);
					if (curr != null) {
						return getIndent(curr.getStartPosition());
					}
				}
				return getInitialIndent();
			}
			return getIndent(node.getStartPosition());
		}
				
		protected final ASTNode getOriginalNode(int index) {
			return (ASTNode) this.list[index].getOriginalValue();
		}
		
		protected String getSeparatorString(int nodeIndex) {
			return this.contantSeparator;
		}
		
		protected int getStartOfNextNode(int nextIndex, int defaultPos) {
			for (int i= nextIndex; i < this.list.length; i++) {
				RewriteEvent elem= this.list[i];
				if (elem.getChangeKind() != RewriteEvent.INSERTED) {
					ASTNode node= (ASTNode) elem.getOriginalValue();
					return getExtendedOffset(node);
				}
			}
			return defaultPos;
		}		
		
		private boolean insertAfterSeparator(ASTNode node) {
			return !isInsertBoundToPrevious(node);
		}
		
		public final int rewriteList(ASTNode parent, StructuralPropertyDescriptor property, int offset, String keyword) {
			this.startPos= offset;
			this.list= getEvent(parent, property).getChildren();
			
			int total= this.list.length;
			if (total == 0) {
				return this.startPos;
			}
		
			int currPos= -1;
			
			int lastNonInsert= -1;
			int lastNonDelete= -1;
					
			for (int i= 0; i < total; i++) {
				int currMark= this.list[i].getChangeKind();
				
				if (currMark != RewriteEvent.INSERTED) {
					lastNonInsert= i;
					if (currPos == -1) {
						ASTNode elem= (ASTNode) this.list[i].getOriginalValue();
						currPos= getExtendedOffset(elem);
					}
				}
				if (currMark != RewriteEvent.REMOVED) {
					lastNonDelete= i;
				}			
			}
		
			if (currPos == -1) { // only inserts
				if (keyword.length() > 0) {  // creating a new list -> insert keyword first (e.g. " throws ")
					TextEditGroup editGroup= getEditGroup(this.list[0]); // first node is insert
					doTextInsert(offset, keyword, editGroup);
				}
				currPos= offset;
			}
			if (lastNonDelete == -1) { // all removed, set back to start so the keyword is removed as well
				currPos= offset;
			}
			
			int prevEnd= currPos;
			
			final int NONE= 0, NEW= 1, EXISTING= 2;
			int separatorState= NEW;

			for (int i= 0; i < total; i++) {
				RewriteEvent currEvent= this.list[i];
				int currMark= currEvent.getChangeKind();
				int nextIndex= i + 1;

				if (currMark == RewriteEvent.INSERTED) {
					TextEditGroup editGroup= getEditGroup(currEvent);
					ASTNode node= (ASTNode) currEvent.getNewValue();
					
					if (separatorState == NONE) { // element after last existing element (but not first)
						doTextInsert(currPos, getSeparatorString(i - 1), editGroup); // insert separator
						separatorState= NEW;
					}
					if (separatorState == NEW || insertAfterSeparator(node)) {
						doTextInsert(currPos, node, getNodeIndent(i), true, editGroup); // insert node
						
						separatorState= NEW;
						if (i != lastNonDelete) {
							if (this.list[nextIndex].getChangeKind() != RewriteEvent.INSERTED) {
								doTextInsert(currPos, getSeparatorString(i), editGroup); // insert separator
							} else {
								separatorState= NONE;
							}
						}
					} else { // EXISTING && insert before separator
						doTextInsert(prevEnd, getSeparatorString(i - 1), editGroup);
						doTextInsert(prevEnd, node, getNodeIndent(i), true, editGroup);
					}
				} else if (currMark == RewriteEvent.REMOVED) {
					ASTNode node= (ASTNode) currEvent.getOriginalValue();
					TextEditGroup editGroup= getEditGroup(currEvent);
					int currEnd= getEndOfNode(node);
					if (i > lastNonDelete && separatorState == EXISTING) {
						// is last, remove previous separator: split delete to allow range copies
						doTextRemove(prevEnd, currPos - prevEnd, editGroup); // remove separator
						doTextRemoveAndVisit(currPos, currEnd - currPos, node, editGroup); // remove node
						currPos= currEnd;
						prevEnd= currEnd;
					} else {
						// remove element and next separator
						int end= getStartOfNextNode(nextIndex, currEnd); // start of next
						doTextRemoveAndVisit(currPos, currEnd - currPos, node, getEditGroup(currEvent)); // remove node
						doTextRemove(currEnd, end - currEnd, editGroup); // remove separator
						currPos= end;
						prevEnd= currEnd;
						separatorState= NEW;
					}
				} else { // replaced or unchanged
					if (currMark == RewriteEvent.REPLACED) {
						ASTNode node= (ASTNode) currEvent.getOriginalValue();
						int currEnd= getEndOfNode(node);
						
						TextEditGroup editGroup= getEditGroup(currEvent);
						ASTNode changed= (ASTNode) currEvent.getNewValue();
						doTextRemoveAndVisit(currPos, currEnd - currPos, node, editGroup);
						doTextInsert(currPos, changed, getNodeIndent(i), true, editGroup);
						
						prevEnd= currEnd;
					} else { // is unchanged
						ASTNode node= (ASTNode) currEvent.getOriginalValue();
						voidVisit(node);
					}
					if (i == lastNonInsert) { // last node or next nodes are all inserts
						separatorState= NONE;
						if (currMark == RewriteEvent.UNCHANGED) {
							ASTNode node= (ASTNode) currEvent.getOriginalValue();
							prevEnd= getEndOfNode(node);
						}
						currPos= prevEnd;
					} else if (this.list[nextIndex].getChangeKind() != RewriteEvent.UNCHANGED) {
						// no updates needed while nodes are unchanged
						if (currMark == RewriteEvent.UNCHANGED) {
							ASTNode node= (ASTNode) currEvent.getOriginalValue();
							prevEnd= getEndOfNode(node);
						}
						currPos= getStartOfNextNode(nextIndex, prevEnd); // start of next
						separatorState= EXISTING;							
					}
				}

			}
			return currPos;
		}
		
		public final int rewriteList(ASTNode parent, StructuralPropertyDescriptor property, int offset, String keyword, String separator) {
			this.contantSeparator= separator;
			return rewriteList(parent, property, offset, keyword);
		}
		
	}
	class ModifierRewriter extends ListRewriter {
		
	}
	
	class ParagraphListRewriter extends ListRewriter {
		
		public final static int DEFAULT_SPACING= 1;
		
		private int initialIndent;
		private int separatorLines;
		
		public ParagraphListRewriter(int initialIndent, int separator) {
			this.initialIndent= initialIndent;
			this.separatorLines= separator;
		}
		
		private int countEmptyLines(ASTNode last) {
			LineInformation lineInformation= getLineInformation();
			int lastLine= lineInformation.getLineOfOffset(getExtendedEnd(last));
			if (lastLine >= 0) {
				int startLine= lastLine + 1;
				int start= lineInformation.getLineOffset(startLine);
				if (start < 0) {
					return 0;
				}
				char[] cont= getContent();
				int i= start;
				while (i < cont.length && ScannerHelper.isWhitespace(cont[i])) {
					i++;
				}
				if (i > start) {
					lastLine= lineInformation.getLineOfOffset(i);
					if (lastLine > startLine) {
						return lastLine - startLine;
					}
				}
			}
			return 0;
		}
						
		protected int getInitialIndent() {
			return this.initialIndent;
		}
		
		private int getNewLines(int nodeIndex) {
			ASTNode curr= getNode(nodeIndex);
			ASTNode next= getNode(nodeIndex + 1);
			
			int currKind= curr.getNodeType();
			int nextKind= next.getNodeType();

			ASTNode last= null;
			ASTNode secondLast= null;
			for (int i= 0; i < this.list.length; i++) {
				ASTNode elem= (ASTNode) this.list[i].getOriginalValue();
				if (elem != null) {
					if (last != null) {
						if (elem.getNodeType() == nextKind && last.getNodeType() == currKind) {
							return countEmptyLines(last);
						}
						secondLast= last;
					}
					last= elem;
				}
			}
			// TODO double check this comparisons. They determine how much lines to keep
			// between the current node and the next node
			if (currKind == ASTNode.VARIABLE_DECLARATION && nextKind == ASTNode.VARIABLE_DECLARATION) {
				return 0;
			}
			if (currKind == ASTNode.ALIAS_DECLARATION && nextKind == ASTNode.ALIAS_DECLARATION) {
				return 0;
			}
			if (currKind == ASTNode.TYPEDEF_DECLARATION && nextKind == ASTNode.TYPEDEF_DECLARATION) {
				return 0;
			}
			if (secondLast != null) {
				return countEmptyLines(secondLast);
			}
			return DEFAULT_SPACING;
		}
		
		private ASTNode getNode(int nodeIndex) {
			ASTNode elem= (ASTNode) this.list[nodeIndex].getOriginalValue();
			if (elem == null) {
				elem= (ASTNode) this.list[nodeIndex].getNewValue();
			}
			return elem;
		}

		protected String getSeparatorString(int nodeIndex) {
			int newLines= this.separatorLines == -1 ? getNewLines(nodeIndex) : this.separatorLines;
			
			String lineDelim= getLineDelimiter();
			StringBuffer buf= new StringBuffer(lineDelim);
			for (int i= 0; i < newLines; i++) {
				buf.append(lineDelim);
			}
			buf.append(createIndentString(getNodeIndent(nodeIndex + 1)));
			return buf.toString();
		}		
	}
	
	class SwitchListRewriter extends ParagraphListRewriter {

		public SwitchListRewriter(int initialIndent) {
			super(initialIndent, 0);
		}
		
		protected int getNodeIndent(int nodeIndex) {
			int indent= getInitialIndent();
			ASTNode node= (ASTNode) this.list[nodeIndex].getOriginalValue();
			if (node == null) {
				node= (ASTNode) this.list[nodeIndex].getNewValue();
			}
			if (node.getNodeType() != ASTNode.SWITCH_CASE) {
				indent++;
			}
			return indent;
		}		
	}
	TextEdit currentEdit;
	
	final RewriteEventStore eventStore; // used from inner classes
	private TokenScanner tokenScanner; // shared scanner
	private final Map sourceCopyInfoToEdit;
	private final Stack sourceCopyEndNodes;
	private final char[] content;
	private final LineInformation lineInfo;
	
	private final ASTRewriteFormatter formatter;
		
	private final NodeInfoStore nodeInfos;
	
	private final TargetSourceRangeComputer extendedSourceRangeComputer;
	
	private final LineCommentEndOffsets lineCommentEndOffsets;
	
	/**
	 * Constructor for ASTRewriteAnalyzer.
	 * @param content the content of the compilation unit to rewrite.
	 * @param lineInfo line information for the content of the compilation unit to rewrite.
	 * @param rootEdit the edit to add all generated edits to
	 * @param eventStore the event store containing the description of changes
	 * @param nodeInfos annotations to nodes, such as if a node is a string placeholder or a copy target
	 * @param comments list of comments of the compilation unit to rewrite (elements of type <code>Comment</code>) or <code>null</code>.
	 * 	@param options the current jdt.core options (formatting/compliance) or <code>null</code>.
	 * 	@param extendedSourceRangeComputer the source range computer to use
	 */
	public ASTRewriteAnalyzer(char[] content, LineInformation lineInfo, String lineDelim, TextEdit rootEdit, RewriteEventStore eventStore, NodeInfoStore nodeInfos, List comments, Map options, TargetSourceRangeComputer extendedSourceRangeComputer) {
		this.eventStore= eventStore;
		this.content= content;
		this.lineInfo= lineInfo;
		this.nodeInfos= nodeInfos;
		this.tokenScanner= null;
		this.currentEdit= rootEdit;
		this.sourceCopyInfoToEdit= new IdentityHashMap();
		this.sourceCopyEndNodes= new Stack();
		
		this.formatter= new ASTRewriteFormatter(nodeInfos, eventStore, options, lineDelim);
		
		this.extendedSourceRangeComputer = extendedSourceRangeComputer;
		this.lineCommentEndOffsets= new LineCommentEndOffsets(comments);
	}
	
	final void addEdit(TextEdit edit) {
		this.currentEdit.addChild(edit);
	}
	
	final void addEditGroup(TextEditGroup editGroup, TextEdit edit) {
		editGroup.addTextEdit(edit);
	}
	
	private void changeNotSupported(ASTNode node) {
		Assert.isTrue(false, "Change not supported in " + node.getClass().getName()); //$NON-NLS-1$
	}
	
	final String createIndentString(int indent) {
	    return this.formatter.createIndentString(indent);
	}
	
	final void doCopySourcePostVisit(ASTNode node, Stack nodeEndStack) {
		while (!nodeEndStack.isEmpty() && nodeEndStack.peek() == node) {
			nodeEndStack.pop();
			this.currentEdit= this.currentEdit.getParent();
		}
	}
	
	final void doCopySourcePreVisit(CopySourceInfo[] infos, Stack nodeEndStack) {
		if (infos != null) {
			for (int i= 0; i < infos.length; i++) {
				CopySourceInfo curr= infos[i];
				TextEdit edit= getCopySourceEdit(curr);
				addEdit(edit);
				this.currentEdit= edit;
				nodeEndStack.push(curr.getNode());
			}
		}
	}
	
	private final TextEdit doTextCopy(TextEdit sourceEdit, int destOffset, int sourceIndentLevel, String destIndentString, TextEditGroup editGroup) {
		TextEdit targetEdit;
		SourceModifier modifier= new SourceModifier(sourceIndentLevel, destIndentString, this.formatter.tabWidth, this.formatter.indentWidth);
		
		if (sourceEdit instanceof MoveSourceEdit) {
			MoveSourceEdit moveEdit= (MoveSourceEdit) sourceEdit;
			moveEdit.setSourceModifier(modifier);
			
			targetEdit= new MoveTargetEdit(destOffset, moveEdit);
			addEdit(targetEdit);
		} else {
			CopySourceEdit copyEdit= (CopySourceEdit) sourceEdit;
			copyEdit.setSourceModifier(modifier);
			
			targetEdit= new CopyTargetEdit(destOffset, copyEdit);
			addEdit(targetEdit);
		}
		
		if (editGroup != null) {
			addEditGroup(editGroup, sourceEdit);
			addEditGroup(editGroup, targetEdit);
		}
		return targetEdit;			

	}
	
	final void doTextInsert(int insertOffset, ASTNode node, int initialIndentLevel, boolean removeLeadingIndent, TextEditGroup editGroup) {		
		ArrayList markers= new ArrayList();
		String formatted= this.formatter.getFormattedResult(node, initialIndentLevel, markers);

		
		int currPos= 0;
		if (removeLeadingIndent) {
			while (currPos < formatted.length() && ScannerHelper.isWhitespace(formatted.charAt(currPos))) {
				currPos++;
			}
		}
		for (int i= 0; i < markers.size(); i++) { // markers.size can change!
			NodeMarker curr= (NodeMarker) markers.get(i);
			
			int offset= curr.offset;
			if (offset != currPos) {
				String insertStr= formatted.substring(currPos, offset); 
				doTextInsert(insertOffset, insertStr, editGroup); // insert until the marker's begin
			}

			Object data= curr.data;
			if (data instanceof TextEditGroup) { // tracking a node
				// need to split and create 2 edits as tracking node can surround replaced node.
				TextEdit edit= new RangeMarker(insertOffset, 0);
				addEditGroup((TextEditGroup) data, edit);
				addEdit(edit);
				if (curr.length != 0) {
					int end= offset + curr.length;
					int k= i + 1;
					while (k < markers.size() && ((NodeMarker) markers.get(k)).offset < end) {
						k++;
					}
					curr.offset= end;
					curr.length= 0;
					markers.add(k, curr); // add again for end position
				}
				currPos= offset;
			} else {
				String destIndentString=  this.formatter.getIndentString(getCurrentLine(formatted, offset));
				if (data instanceof CopyPlaceholderData) { // replace with a copy/move target
					CopySourceInfo copySource= ((CopyPlaceholderData) data).copySource;
					int srcIndentLevel= getIndent(copySource.getNode().getStartPosition());
					TextEdit sourceEdit= getCopySourceEdit(copySource);
					doTextCopy(sourceEdit, insertOffset, srcIndentLevel, destIndentString, editGroup);
					currPos= offset + curr.length; // continue to insert after the replaced string
				} else if (data instanceof StringPlaceholderData) { // replace with a placeholder
					String code= ((StringPlaceholderData) data).code;
					String str= this.formatter.changeIndent(code, 0, destIndentString); 
					doTextInsert(insertOffset, str, editGroup);
					currPos= offset + curr.length; // continue to insert after the replaced string
				}
			}

		}
		if (currPos < formatted.length()) {
			String insertStr= formatted.substring(currPos);
			doTextInsert(insertOffset, insertStr, editGroup);
		}
	}	
		
	final void doTextInsert(int offset, String insertString, TextEditGroup editGroup) {
		if (insertString.length() > 0) {
			// bug fix for 95839: problem with inserting at the end of a line comment
			if (this.lineCommentEndOffsets.isEndOfLineComment(offset, this.content)) {
				if (!insertString.startsWith(getLineDelimiter())) {
					TextEdit edit= new InsertEdit(offset, getLineDelimiter());  // add a line delimiter
					addEdit(edit);
					if (editGroup != null) {
						addEditGroup(editGroup, edit);
					}
				}
				this.lineCommentEndOffsets.remove(offset); // only one line delimiter per line comment required
			}
			TextEdit edit= new InsertEdit(offset, insertString);
			addEdit(edit);
			if (editGroup != null) {
				addEditGroup(editGroup, edit);
			}
		}
	}
	
	final TextEdit doTextRemove(int offset, int len, TextEditGroup editGroup) {
		if (len == 0) {
			return null;
		}
		TextEdit edit= new DeleteEdit(offset, len);
		addEdit(edit);
		if (editGroup != null) {
			addEditGroup(editGroup, edit);
		}
		return edit;
	}
	
	final void doTextRemoveAndVisit(int offset, int len, ASTNode node, TextEditGroup editGroup) {
		TextEdit edit= doTextRemove(offset, len, editGroup);
		if (edit != null) {
			this.currentEdit= edit;
			voidVisit(node);
			this.currentEdit= edit.getParent();
		} else {
			voidVisit(node);
		}
	}
	
	private final void doTextReplace(int offset, int len, String insertString, TextEditGroup editGroup) {
		if (len > 0 || insertString.length() > 0) {
			TextEdit edit= new ReplaceEdit(offset, len, insertString);
			addEdit(edit);
			if (editGroup != null) {
				addEditGroup(editGroup, edit);
			}
		}
	}
	
	final int doVisit(ASTNode node) {
		node.accept(this);
		return getExtendedEnd(node);
	}	
	
	private final int doVisit(ASTNode parent, StructuralPropertyDescriptor property, int offset) {
		Object node= getOriginalValue(parent, property);
		if (property.isChildProperty() && node != null) {
			return doVisit((ASTNode) node);
		} else if (property.isChildListProperty()) {
			return doVisitList((List) node, offset);
		}
		return offset;
	}
	
	private int doVisitList(List list, int offset) {
		int endPos= offset;
		for (Iterator iter= list.iterator(); iter.hasNext();) {
			ASTNode curr= ((ASTNode) iter.next());
			endPos= doVisit(curr);
		}
		return endPos;
	}
	
	private final boolean doVisitUnchangedChildren(ASTNode parent) {
		List properties= parent.structuralPropertiesForType();
		for (int i= 0; i < properties.size(); i++) {
			voidVisit(parent, (StructuralPropertyDescriptor) properties.get(i));
		}
		return false;
	}
	
	private final int getChangeKind(ASTNode node, StructuralPropertyDescriptor property) {
		RewriteEvent event= getEvent(node, property);
		if (event != null) {
			return event.getChangeKind();
		}
		return RewriteEvent.UNCHANGED;
	}
	
		
	final char[] getContent() {
		return this.content;
	}
	
	final TextEdit getCopySourceEdit(CopySourceInfo info) {
		TextEdit edit= (TextEdit) this.sourceCopyInfoToEdit.get(info);
		if (edit == null) {
			SourceRange range= getExtendedRange(info.getNode());
			int start= range.getStartPosition();
			int end= start + range.getLength();
			if (info.isMove) {
				MoveSourceEdit moveSourceEdit= new MoveSourceEdit(start, end - start);
				moveSourceEdit.setTargetEdit(new MoveTargetEdit(0));
				edit= moveSourceEdit;
			} else {
				CopySourceEdit copySourceEdit= new CopySourceEdit(start, end - start);
				copySourceEdit.setTargetEdit(new CopyTargetEdit(0));
				edit= copySourceEdit;
			}
			this.sourceCopyInfoToEdit.put(info, edit);
		}
		return edit;
	}
	
	private String getCurrentLine(String str, int pos) {
		for (int i= pos - 1; i>= 0; i--) {
			char ch= str.charAt(i);
			if (IndentManipulation.isLineDelimiterChar(ch)) {
				return str.substring(i + 1, pos);
			}
		}
		return str.substring(0, pos);
	}
	
	private final TextEditGroup getEditGroup(ASTNode parent, StructuralPropertyDescriptor property) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null) {
			return getEditGroup(event);
		}
		return null;
	}
	
	final TextEditGroup getEditGroup(RewriteEvent change) {
		return this.eventStore.getEventEditGroup(change);
	}
	
	final RewriteEvent getEvent(ASTNode parent, StructuralPropertyDescriptor property) {
		return this.eventStore.getEvent(parent, property);
	}
	
	final int getExtendedEnd(ASTNode node) {
		TargetSourceRangeComputer.SourceRange range= getExtendedRange(node);
		return range.getStartPosition() + range.getLength();
	}
	
	final int getExtendedOffset(ASTNode node) {
		return getExtendedRange(node).getStartPosition();
	}
	
	/**
	 * Returns the extended source range for a node.
	 * 
	 * @return an extended source range (never null)
	 * @since 3.1
	 */
	final SourceRange getExtendedRange(ASTNode node) {
		if (this.eventStore.isRangeCopyPlaceholder(node)) {
			return new SourceRange(node.getStartPosition(), node.getLength());
		}
		return this.extendedSourceRangeComputer.computeSourceRange(node);
	}
	
	final int getIndent(int offset) {
		return this.formatter.computeIndentUnits(getIndentOfLine(offset));
	}
	
	final String getIndentAtOffset(int pos) {
		return this.formatter.getIndentString(getIndentOfLine(pos));
	}
	
	final private String getIndentOfLine(int pos) {
		int line= getLineInformation().getLineOfOffset(pos);
		if (pos >= 0) {
			char[] cont= getContent();
			int lineStart= getLineInformation().getLineOffset(line);
		    int i= lineStart;
			while (i < cont.length && IndentManipulation.isIndentChar(content[i])) {
			    i++;
			}
			return new String(cont, lineStart, i - lineStart);
		}
		return new String();
	}
	
	
	final String getLineDelimiter() {
		return this.formatter.lineDelimiter;
	}
		
	final LineInformation getLineInformation() {
		return this.lineInfo;
	}
			
	private final Object getNewValue(ASTNode parent, StructuralPropertyDescriptor property) {
		return this.eventStore.getNewValue(parent, property);
	}
	
	
	private final Object getOriginalValue(ASTNode parent, StructuralPropertyDescriptor property) {
		return this.eventStore.getOriginalValue(parent, property);
	}
				
	/*
	 * Next token is a left brace. Returns the offset after the brace. For incomplete code, return the start offset.  
	 */
	private int getPosAfterLeftBrace(int pos) {
		try {
			int nextToken= getScanner().readNext(pos, true);
			if (nextToken == ITerminalSymbols.TokenNameLCURLY) {
				return getScanner().getCurrentEndOffset();
			}
		} catch (CoreException e) {
			handleException(e);
		}
		return pos;
	}
		
	/*
	 * Next token is a left brace or colon. Returns the offset after the brace or colon. For incomplete code, return the start offset.  
	 */
	private int getPosAfterLeftBraceOrColon(int pos) {
		try {
			int nextToken= getScanner().readNext(pos, true);
			if (nextToken == ITerminalSymbols.TokenNameLCURLY || nextToken == ITerminalSymbols.TokenNameCOLON) {
				return getScanner().getCurrentEndOffset();
			}
		} catch (CoreException e) {
			handleException(e);
		}
		return pos;
	}
	
	/*
	 * Next token is a left brace or semicolon.
	 * Returns the offset after the brace or semicolon. For incomplete code, return the start offset.
	 * 
	 * wasSemicolon[0] is set to true if it was a semicolon, else to false.
	 */
	private int getPosAfterLeftBraceOrColon(int pos, boolean[] wasSemicolon) {
		try {
			int nextToken= getScanner().readNext(pos, true);
			if (nextToken == ITerminalSymbols.TokenNameLCURLY) {
				wasSemicolon[0] = false;
				return getScanner().getCurrentEndOffset();
			} else if (nextToken == ITerminalSymbols.TokenNameSEMICOLON) {
				wasSemicolon[0] = true;
				return getScanner().getCurrentEndOffset();
			}
		} catch (CoreException e) {
			handleException(e);
		}
		return pos;
	}
	
	/*
	 * Next token is a left brace or semicolon. Returns the offset after the brace or semicolon. For incomplete code, return the start offset.  
	 */
	private int getPosAfterLeftBraceOrSemicolon(int pos) {
		try {
			int nextToken= getScanner().readNext(pos, true);
			if (nextToken == ITerminalSymbols.TokenNameLCURLY || nextToken == ITerminalSymbols.TokenNameSEMICOLON) {
				return getScanner().getCurrentEndOffset();
			}
		} catch (CoreException e) {
			handleException(e);
		}
		return pos;
	}
		
	final TokenScanner getScanner() {
		if (this.tokenScanner == null) {
			IScanner scanner= ToolFactory.createScanner(true, true, false, false, AST.D1);
			scanner.setSource(this.content);
			this.tokenScanner= new TokenScanner(scanner);
		}
		return this.tokenScanner;
	}
	
	final void handleException(Throwable e) {
		IllegalArgumentException runtimeException= new IllegalArgumentException("Document does not match the AST"); //$NON-NLS-1$
		runtimeException.initCause(e);
		throw runtimeException;
	}
	
	private final boolean hasChildrenChanges(ASTNode node) {
		return this.eventStore.hasChangedProperties(node);
	}	
	
	private boolean isAllOfKind(RewriteEvent[] children, int kind) {
		for (int i= 0; i < children.length; i++) {
			if (children[i].getChangeKind() != kind) {
				return false;
			}
		}
		return true;
	}
	
	private final boolean isChanged(ASTNode node, StructuralPropertyDescriptor property) {
		RewriteEvent event= getEvent(node, property);
		if (event != null) {
			return event.getChangeKind() != RewriteEvent.UNCHANGED;
		}
		return false;
	}
	
	
	private final boolean isCollapsed(ASTNode node) {
		return this.nodeInfos.isCollapsed(node);
	}
	
	final boolean isInsertBoundToPrevious(ASTNode node) {
		return this.eventStore.isInsertBoundToPrevious(node);
	}
		
	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#postVisit(ASTNode)
	 */
	public void postVisit(ASTNode node) {
		TextEditGroup editGroup= this.eventStore.getTrackedNodeData(node);
		if (editGroup != null) {
			this.currentEdit= this.currentEdit.getParent();
		}
		// remove copy source edits
		doCopySourcePostVisit(node, this.sourceCopyEndNodes);
	}
	
	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#preVisit(ASTNode)
	 */
	public void preVisit(ASTNode node) {		
		// copies, then range marker
		
		CopySourceInfo[] infos= this.eventStore.getNodeCopySources(node);
		doCopySourcePreVisit(infos, this.sourceCopyEndNodes);

		TextEditGroup editGroup= this.eventStore.getTrackedNodeData(node);
		if (editGroup != null) {
			SourceRange range= getExtendedRange(node);
			int offset= range.getStartPosition();
			int length= range.getLength();
			TextEdit edit= new RangeMarker(offset, length);
			addEditGroup(editGroup, edit);
			addEdit(edit);
			this.currentEdit= edit;
		}
	}
	
	private void replaceOperation(int posBeforeOperation, String newOperation, TextEditGroup editGroup) {
		try {
			getScanner().readNext(posBeforeOperation, true);
			doTextReplace(getScanner().getCurrentStartOffset(), getScanner().getCurrentLength(), newOperation, editGroup);
		} catch (CoreException e) {
			handleException(e);
		}
	}
	
	private int rewriteVersion(ASTNode parent, StructuralPropertyDescriptor property, int parentToken, int pos) {
		int versionChange = getChangeKind(parent, property);
		if (versionChange != RewriteEvent.UNCHANGED) {
			ASTNode originalVersion = (ASTNode) getEvent(parent, property).getOriginalValue();
			switch(versionChange) {
			case RewriteEvent.INSERTED:
				try {
					pos = getScanner().getTokenEndOffset(parentToken, pos);
					doTextInsert(pos, "(", getEditGroup(parent, property));
					rewriteNode(parent, property, pos, ASTRewriteFormatter.NONE);
					doTextInsert(pos, ")", getEditGroup(parent, property));
				} catch (CoreException e) {
					
				}
				break;
			case RewriteEvent.REPLACED:
				rewriteNode(parent, property, originalVersion.getStartPosition(), ASTRewriteFormatter.NONE);
				break;
			case RewriteEvent.REMOVED:
				try {
					pos = getScanner().getTokenStartOffset(ITerminalSymbols.TokenNameLPAREN, pos);
					int end = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRPAREN, originalVersion.getStartPosition() + originalVersion.getLength());
					doTextRemove(pos, end - pos, getEditGroup(parent, property));
					
					pos = end;
				} catch (CoreException e) {
					handleException(e);
				}
				break;
			}
		} else {
			doVisit(parent, property, pos);
			
			ASTNode node = (ASTNode) getOriginalValue(parent, property);
			try {
				if (node == null) {
					pos = getScanner().getTokenEndOffset(parentToken, pos);
				} else {
					pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRPAREN, node.getStartPosition() + node.getLength());
				}
			} catch (CoreException e) {
				handleException(e);
			}
		}
		
		return pos;
	}
	
	private int rewriteThenElseBody(ASTNode parent, StructuralPropertyDescriptor thenProperty, StructuralPropertyDescriptor elseProperty, int pos) {
		RewriteEvent thenEvent= getEvent(parent, thenProperty);
		int elseChange= getChangeKind(parent, elseProperty);

		if (thenEvent != null && thenEvent.getChangeKind() != RewriteEvent.UNCHANGED) {
			try {
				int indent= getIndent(parent.getStartPosition());
				
				int endPos= -1;
				Object elseStatement= getOriginalValue(parent, elseProperty);
				if (elseStatement != null) {
					ASTNode thenStatement = (ASTNode) thenEvent.getOriginalValue();
					endPos= getScanner().getTokenStartOffset(ITerminalSymbols.TokenNameelse, thenStatement.getStartPosition() + thenStatement.getLength()); // else keyword
				}
				if (elseStatement == null || elseChange != RewriteEvent.UNCHANGED) {
					pos= rewriteBodyNode(parent, thenProperty, pos, endPos, indent, this.formatter.IF_BLOCK_NO_ELSE); 
				} else {
					pos= rewriteBodyNode(parent, thenProperty, pos, endPos, indent, this.formatter.IF_BLOCK_WITH_ELSE); 
				}
			} catch (CoreException e) {
				handleException(e);
			}
		} else {
			pos= doVisit(parent, thenProperty, pos);
		}

		if (elseChange != RewriteEvent.UNCHANGED) {
			int indent= getIndent(parent.getStartPosition());
			Object newThen= getNewValue(parent, thenProperty);
			if (newThen instanceof Block) {
				rewriteBodyNode(parent, elseProperty, pos, -1, indent, this.formatter.ELSE_AFTER_BLOCK);
			} else {
				rewriteBodyNode(parent, elseProperty, pos, -1, indent, this.formatter.ELSE_AFTER_STATEMENT);
			}
		} else {
			pos= doVisit(parent, elseProperty, pos);
		}
		
		return pos;
	}
	
	private int rewriteThenElseDeclarations(ASTNode parent, StructuralPropertyDescriptor thenProperty, StructuralPropertyDescriptor elseProperty, int pos) {
		RewriteEvent thenEvent = getEvent(parent, thenProperty);
		if (thenEvent != null && thenEvent.getChangeKind() != RewriteEvent.UNCHANGED) {
			try {
				int token;
				try {
					token = getScanner().readNext(true);
				} catch (CoreException e) {
					token = -1;
				}
				if (token == ITerminalSymbols.TokenNameLCURLY) {
					pos = getScanner().getCurrentEndOffset();
					pos = rewriteParagraphList(parent, thenProperty, pos, 0, -1, 2);
					pos = getScanner().getNextEndOffset(pos, true);
				} else if (token == ITerminalSymbols.TokenNameCOLON) {
					pos = getScanner().getCurrentEndOffset();
					pos = rewriteParagraphList(parent, thenProperty, pos, 0, -1, 2);
				} else {
					doTextInsert(pos, "{", getEditGroup(parent, thenProperty));
					pos = rewriteParagraphList(parent, thenProperty, pos, 0, -1, 2);
					doTextInsert(pos, "}", getEditGroup(parent, thenProperty));
				}
			} catch (CoreException e) {
				handleException(e);
			}
		} else {
			try {
				int token;
				try {
					token = getScanner().readNext(true);
				} catch (CoreException e) {
					token = -1;
				}
				if (token == ITerminalSymbols.TokenNameLCURLY) {
					pos = getScanner().getCurrentEndOffset();
					pos = doVisit(parent, thenProperty, pos);
					pos = getScanner().getNextEndOffset(pos, true);
				} else {
					pos = doVisit(parent, thenProperty, pos);
				}
			} catch (CoreException e) {
				handleException(e);
			}
		}
		
		RewriteEvent elseEvent = getEvent(parent, elseProperty);
		if (elseEvent != null && elseEvent.getChangeKind() != RewriteEvent.UNCHANGED) {
			List originalElse = (List) elseEvent.getOriginalValue();
			List newElse = (List) elseEvent.getNewValue();
			
			if (originalElse.isEmpty()) {
				int token;
				try {
					token = getScanner().readNext(pos, true);
				} catch (CoreException e) {
					token = -1;
				}
				if (token != ITerminalSymbols.TokenNameelse) {
					doTextInsert(pos, " else ", getEditGroup(parent, elseProperty));
				}
			} else if (newElse.isEmpty()) {
				try {
					pos = getScanner().getTokenStartOffset(ITerminalSymbols.TokenNameelse, pos);
					ASTNode last = (ASTNode) originalElse.get(originalElse.size() - 1);
					int end = last.getStartPosition() + last.getLength();
					
					getScanner().setOffset(end);
					int token;
					try {
						token = getScanner().readNext(true);
					} catch (CoreException e) {
						token = -1;
					}
					if (token == ITerminalSymbols.TokenNameRCURLY) {
						end = getScanner().getCurrentEndOffset();
					}
					doTextRemove(pos, end - pos, getEditGroup(parent, elseProperty));
				} catch (CoreException e) {
					handleException(e);
				}
				return pos;
			}
			
			try {
				int token;
				try {
					token = getScanner().readNext(true);
				} catch (CoreException e) {
					token = -1;
				}
				if (token == ITerminalSymbols.TokenNameLCURLY) {
					pos = getScanner().getCurrentEndOffset();
					pos = rewriteParagraphList(parent, elseProperty, pos, 0, -1, 2);
					pos = getScanner().getNextEndOffset(pos, true);
				} else {
					doTextInsert(pos, "{", getEditGroup(parent, elseProperty));
					pos = rewriteParagraphList(parent, elseProperty, pos, 0, -1, 2);
					doTextInsert(pos, "}", getEditGroup(parent, elseProperty));
				}
			} catch (CoreException e) {
				handleException(e);
			}
		} else {
			List originalElse = (List) getOriginalValue(parent, elseProperty);
			if (!originalElse.isEmpty()) { 
				try {
					int token = getScanner().readNext(true);
					if (token == ITerminalSymbols.TokenNameLCURLY) {
						pos = getScanner().getCurrentEndOffset();
						pos = doVisit(parent, elseProperty, pos);
						pos = getScanner().getNextEndOffset(pos, true);
					} else {
						pos = doVisit(parent, elseProperty, pos);
					}
				} catch (CoreException e) {
					handleException(e);
				}
			}
		}
		
		return pos;
	}
	
	/*
	 * endpos can be -1 -> use the end pos of the body
	 */
	private int rewriteBodyNode(ASTNode parent, StructuralPropertyDescriptor property, int offset, int endPos, int indent, BlockContext context) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null) {
			switch (event.getChangeKind()) {
				case RewriteEvent.INSERTED: {
					ASTNode node= (ASTNode) event.getNewValue();
					TextEditGroup editGroup= getEditGroup(event);
					
					String[] strings= context.getPrefixAndSuffix(indent, node, this.eventStore);
					
					doTextInsert(offset, strings[0], editGroup);
					doTextInsert(offset, node, indent, true, editGroup);
					doTextInsert(offset, strings[1], editGroup);
					return offset;
				}
				case RewriteEvent.REMOVED: {
					ASTNode node= (ASTNode) event.getOriginalValue();
					if (endPos == -1) {
						endPos= getExtendedEnd(node);
					}
					
					TextEditGroup editGroup= getEditGroup(event);
					// if there is a prefix, remove the prefix as well
					int len= endPos - offset;
					doTextRemoveAndVisit(offset, len, node, editGroup);
					return endPos;
				}
				case RewriteEvent.REPLACED: {
					ASTNode node= (ASTNode) event.getOriginalValue();
					if (endPos == -1) {
						endPos= getExtendedEnd(node);
					}
					TextEditGroup editGroup= getEditGroup(event);
					int nodeLen= endPos - offset; 
					
					ASTNode replacingNode= (ASTNode) event.getNewValue();
					String[] strings= context.getPrefixAndSuffix(indent, replacingNode, this.eventStore);
					doTextRemoveAndVisit(offset, nodeLen, node, editGroup);
					
					String prefix= strings[0];
					doTextInsert(offset, prefix, editGroup);
					String lineInPrefix= getCurrentLine(prefix, prefix.length());
					if (prefix.length() != lineInPrefix.length()) {
						// prefix contains a new line: update the indent to the one used in the prefix
						indent= this.formatter.computeIndentUnits(lineInPrefix);
					}
					doTextInsert(offset, replacingNode, indent, true, editGroup);
					doTextInsert(offset, strings[1], editGroup);
					return endPos;
				}
			}
		}
		int pos= doVisit(parent, property, offset);
		if (endPos != -1) {
			return endPos;
		}
		return pos;
	}

	private int rewriteModifiers(ASTNode node, ChildListPropertyDescriptor property, int pos) {
		RewriteEvent event= getEvent(node, property);
		if (event == null || event.getChangeKind() == RewriteEvent.UNCHANGED) {
			return doVisit(node, property, pos);
		}
		RewriteEvent[] children= event.getChildren();
		boolean isAllInsert= isAllOfKind(children, RewriteEvent.INSERTED);
		boolean isAllRemove= isAllOfKind(children, RewriteEvent.REMOVED);
		if (isAllInsert || isAllRemove) {
			// update pos
			try {
				pos= getScanner().getNextStartOffset(pos, false);
			} catch (CoreException e) {
				handleException(e);
			}
		}
		
		int endPos= new ModifierRewriter().rewriteList(node, property, pos, "", " "); //$NON-NLS-1$ //$NON-NLS-2$

		if (isAllInsert) {
			RewriteEvent lastChild= children[children.length - 1];
			String separator= String.valueOf(' ');
			doTextInsert(endPos, separator, getEditGroup(lastChild));
		} else if (isAllRemove) {
			try {
				int nextPos= getScanner().getNextStartOffset(endPos, false); // to the next token
				doTextRemove(endPos, nextPos - endPos, getEditGroup(children[children.length - 1]));
				return nextPos;
			} catch (CoreException e) {
				handleException(e);
			}
		}
		return endPos;
	}
	
	private int rewriteNode(ASTNode parent, StructuralPropertyDescriptor property, int offset, Prefix prefix) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null) {
			switch (event.getChangeKind()) {
				case RewriteEvent.INSERTED: {
					ASTNode node= (ASTNode) event.getNewValue();
					TextEditGroup editGroup= getEditGroup(event);
					int indent= getIndent(offset);
					doTextInsert(offset, prefix.getPrefix(indent), editGroup);
					doTextInsert(offset, node, indent, true, editGroup);
					return offset;
				}
				case RewriteEvent.REMOVED: {	
					ASTNode node= (ASTNode) event.getOriginalValue();
					TextEditGroup editGroup= getEditGroup(event);
					
					int nodeEnd= getExtendedEnd(node);
					// if there is a prefix, remove the prefix as well
					int len= nodeEnd - offset;
					doTextRemoveAndVisit(offset, len, node, editGroup);
					return nodeEnd;
				}
				case RewriteEvent.REPLACED: {
					ASTNode node= (ASTNode) event.getOriginalValue();
					TextEditGroup editGroup= getEditGroup(event);
					SourceRange range= getExtendedRange(node);
					int nodeOffset= range.getStartPosition();
					int nodeLen= range.getLength();
					doTextRemoveAndVisit(nodeOffset, nodeLen, node, editGroup);
					doTextInsert(nodeOffset, (ASTNode) event.getNewValue(), getIndent(offset), true, editGroup);
					return nodeOffset + nodeLen;
				}
			}
		}
		return doVisit(parent, property, offset);
	}
	
	private int rewriteNodeList(ASTNode parent, StructuralPropertyDescriptor property, int pos, String keyword, String separator) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null && event.getChangeKind() != RewriteEvent.UNCHANGED) {
			return new ListRewriter().rewriteList(parent, property, pos, keyword, separator);
		}
		return doVisit(parent, property, pos);
	}
	
	private void rewriteOperation(ASTNode parent, StructuralPropertyDescriptor property, int posBeforeOperation) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null && event.getChangeKind() != RewriteEvent.UNCHANGED) {
			try {
				String newOperation= event.getNewValue().toString();
				TextEditGroup editGroup= getEditGroup(event);
				getScanner().readNext(posBeforeOperation, true);
				doTextReplace(getScanner().getCurrentStartOffset(), getScanner().getCurrentLength(), newOperation, editGroup);
			} catch (CoreException e) {
				handleException(e);
			}
		}
	}
	
	private void rewriteOperationUntil(ASTNode parent, StructuralPropertyDescriptor property, int terminalSymbol, int posBeforeOperation) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null && event.getChangeKind() != RewriteEvent.UNCHANGED) {
			try {
				String newOperation= event.getNewValue().toString();
				TextEditGroup editGroup= getEditGroup(event);
				int end = getScanner().getTokenStartOffset(terminalSymbol, posBeforeOperation);
				doTextReplace(posBeforeOperation, end - posBeforeOperation, newOperation, editGroup);
			} catch (CoreException e) {
				handleException(e);
			}
		}
	}
	
	private int rewriteOptionalTemplateParameters(ASTNode parent, StructuralPropertyDescriptor property, int offset, String keyword, boolean adjustOnNext, boolean needsSpaceOnRemoveAll) {
		int pos= offset;
		RewriteEvent event= getEvent(parent, property);
		if (event != null && event.getChangeKind() != RewriteEvent.UNCHANGED) {
			RewriteEvent[] children= event.getChildren();
			try {
				boolean isAllInserted= isAllOfKind(children, RewriteEvent.INSERTED);
				if (isAllInserted && adjustOnNext) {
					pos= getScanner().getNextStartOffset(pos, false); // adjust on next element
				}
				boolean isAllRemoved= !isAllInserted && isAllOfKind(children, RewriteEvent.REMOVED);
				if (isAllRemoved) { // all removed: set start to left bracket
					int posBeforeOpenBracket= getScanner().getTokenStartOffset(ITerminalSymbols.TokenNameLPAREN, pos);
					if (posBeforeOpenBracket != pos) {
						needsSpaceOnRemoveAll= false;
					}
					pos= posBeforeOpenBracket;
				}
				pos= new ListRewriter().rewriteList(parent, property, pos, String.valueOf('('), ", "); //$NON-NLS-1$
				if (isAllRemoved) { // all removed: remove right and space up to next element
					int endPos= getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRPAREN, pos); // set pos to ')'
					endPos= getScanner().getNextStartOffset(endPos, false);
					String replacement= needsSpaceOnRemoveAll ? String.valueOf(' ') : new String();
					doTextReplace(pos, endPos - pos, replacement, getEditGroup(children[children.length - 1]));
					return endPos;
				} else if (isAllInserted) {
					doTextInsert(pos, String.valueOf(')' + keyword), getEditGroup(children[children.length - 1]));
					return pos;
				}
			} catch (CoreException e) {
				handleException(e);
			}
		} else {
			pos= doVisit(parent, property, pos);
		}
		if (pos != offset) { // list contained some type -> parse after closing bracket
			try {
				return getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRPAREN, pos);
			} catch (CoreException e) {
				handleException(e);
			}
		}
		return pos;
	}

	/**
	 * 
	 * @param parent
	 * @param property
	 * @param insertPos
	 * @param insertIndent
	 * @param separator how many lines to insert between each node. -1 to guess depending
	 * on the nodes types
	 * @param lead how many lines to insert before the new nodes
	 * @return
	 */
	private int rewriteParagraphList(ASTNode parent, StructuralPropertyDescriptor property, int insertPos, int insertIndent, int separator, int lead) {
		RewriteEvent event= getEvent(parent, property);
		if (event == null || event.getChangeKind() == RewriteEvent.UNCHANGED) {
			return doVisit(parent, property, insertPos);
		}
		
		RewriteEvent[] events= event.getChildren();
		ParagraphListRewriter listRewriter= new ParagraphListRewriter(insertIndent, separator);
		StringBuffer leadString= new StringBuffer();
		if (isAllOfKind(events, RewriteEvent.INSERTED)) {
			for (int i= 0; i < lead; i++) {
				leadString.append(getLineDelimiter());
			}
			leadString.append(createIndentString(insertIndent));
		}
		return listRewriter.rewriteList(parent, property, insertPos, leadString.toString());
	}
	
	private int rewriteRequiredNode(ASTNode parent, StructuralPropertyDescriptor property) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null && event.getChangeKind() == RewriteEvent.REPLACED) {
			ASTNode node= (ASTNode) event.getOriginalValue();
			TextEditGroup editGroup= getEditGroup(event);
			SourceRange range= getExtendedRange(node);
			int offset= range.getStartPosition();
			int length= range.getLength();
			doTextRemoveAndVisit(offset, length, node, editGroup);
			doTextInsert(offset, (ASTNode) event.getNewValue(), getIndent(offset), true, editGroup);
			return offset + length;	
		}
		return doVisit(parent, property, 0);
	}
	
	@Override
	public boolean visit(AggregateDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, AggregateDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, AggregateDeclaration.MODIFIERS_PROPERTY, pos);
		
		AggregateDeclaration.Kind oldKind = (Kind) getOriginalValue(node, AggregateDeclaration.KIND_PROPERTY);
		int kindTerminalSymbol = 0;
		switch(oldKind) {
		case CLASS: kindTerminalSymbol = ITerminalSymbols.TokenNameclass; break;
		case INTERFACE: kindTerminalSymbol = ITerminalSymbols.TokenNameinterface; break;
		case UNION: kindTerminalSymbol = ITerminalSymbols.TokenNameunion; break;
		case STRUCT: kindTerminalSymbol = ITerminalSymbols.TokenNamestruct; break;
		}
		
		if (isChanged(node, AggregateDeclaration.KIND_PROPERTY)) {
			AggregateDeclaration.Kind newKind = (Kind) getNewValue(node, AggregateDeclaration.KIND_PROPERTY);
			try {
				getScanner().readToToken(kindTerminalSymbol, node.getStartPosition());
				int start = getScanner().getCurrentStartOffset();
				int end = getScanner().getCurrentEndOffset();
				
				doTextReplace(start, end - start, newKind.toString(), getEditGroup(node, AggregateDeclaration.KIND_PROPERTY));
			} catch (CoreException e) {
				handleException(e);
			}
		}

		try {
			pos = getScanner().getTokenEndOffset(kindTerminalSymbol, node.getStartPosition());
			pos = rewriteNode(node, AggregateDeclaration.NAME_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			handleException(e);
		}
		
		pos = rewriteOptionalTemplateParameters(node, AggregateDeclaration.TEMPLATE_PARAMETERS_PROPERTY, pos, "", false, false);
		pos = rewriteNodeList(node, AggregateDeclaration.BASE_CLASSES_PROPERTY, pos, ":", ", ");
		
		// startPos : find position after left brace of type, be aware that bracket might be missing
		boolean[] wasSemicolon = { false };
		int startIndent= getIndent(node.getStartPosition()) + 1;
		int startPos= getPosAfterLeftBraceOrColon(pos, wasSemicolon);
		
		int declarationsChange = getChangeKind(node, AggregateDeclaration.DECLARATIONS_PROPERTY);
		
		if (wasSemicolon[0] && declarationsChange != RewriteEvent.UNCHANGED) {
			doTextReplace(startPos - 1, 1, "{", getEditGroup(node, AggregateDeclaration.DECLARATIONS_PROPERTY));
		}
		
		pos = rewriteParagraphList(node, AggregateDeclaration.DECLARATIONS_PROPERTY, startPos, startIndent, -1, 2);
		
		if (wasSemicolon[0] && declarationsChange != RewriteEvent.UNCHANGED) {
			doTextInsert(pos, "}", getEditGroup(node, AggregateDeclaration.DECLARATIONS_PROPERTY));
		}
		
		try {
			if (!wasSemicolon[0]) {
				pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRCURLY, pos);
			}
			rewriteNode(node, AggregateDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			handleException(e);
		}
	
		return false;
	}
	
	@Override
	public boolean visit(AliasDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, AliasDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, AliasDeclaration.MODIFIERS_PROPERTY, pos);
		
		pos= rewriteNode(node, AliasDeclaration.TYPE_PROPERTY, pos, ASTRewriteFormatter.NONE);
		pos = rewriteNodeList(node, AliasDeclaration.FRAGMENTS_PROPERTY, pos, "", ", "); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameSEMICOLON, pos);
			rewriteNode(node, AliasDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			handleException(e);
		}
		
		return false;
	}
	
	@Override
	public boolean visit(AliasDeclarationFragment node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, AliasDeclarationFragment.NAME_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(AliasTemplateParameter node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteRequiredNode(node, AliasTemplateParameter.NAME_PROPERTY);
		pos = rewriteNode(node, AliasTemplateParameter.SPECIFIC_TYPE_PROPERTY, pos, ASTRewriteFormatter.COLON);
		pos = rewriteNode(node, AliasTemplateParameter.DEFAULT_TYPE_PROPERTY, pos, ASTRewriteFormatter.EQUALS);
		return false;
	}
	
	@Override
	public boolean visit(AlignDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, AlignDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, AlignDeclaration.MODIFIERS_PROPERTY, pos);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameLPAREN, pos);
		} catch (CoreException e) {
			handleException(e);
		}
		
		rewriteOperation(node, AlignDeclaration.ALIGN_PROPERTY, pos);
		
		try {
			pos = getScanner().getNextEndOffset(pos, true);
			pos = getScanner().getNextEndOffset(pos, true);
		} catch (CoreException e) {
			handleException(e);
		}
		
		// startPos : find position after left brace of type, be aware that bracket might be missing
		int startIndent= getIndent(node.getStartPosition()) + 1;
		int startPos= getPosAfterLeftBraceOrColon(pos);
		pos = rewriteParagraphList(node, AlignDeclaration.DECLARATIONS_PROPERTY, startPos, startIndent, -1, 2);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRCURLY, pos);
		} catch (CoreException e) {
			// Maybe is was a colon
		}
		rewriteNode(node, AlignDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		
		return false;
	}
	
	@Override
	public boolean visit(Argument node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = node.getStartPosition();
		
		Argument.PassageMode oldPassage = (Argument.PassageMode) getOriginalValue(node, Argument.PASSAGE_MODE_PROPERTY);
		int passageTerminalSymbol = 0;
		switch(oldPassage) {
		case DEFAULT: passageTerminalSymbol = -1; break;
		case IN: passageTerminalSymbol = ITerminalSymbols.TokenNamein; break;
		case INOUT: passageTerminalSymbol = ITerminalSymbols.TokenNameinout; break;
		case LAZY: passageTerminalSymbol = ITerminalSymbols.TokenNamelazy; break;
		case OUT: passageTerminalSymbol = ITerminalSymbols.TokenNameout; break;
		}
		
		int passageChangeKind = getChangeKind(node, Argument.PASSAGE_MODE_PROPERTY);
		if (passageChangeKind != RewriteEvent.UNCHANGED) {
			Argument.PassageMode newPassage = (Argument.PassageMode) getNewValue(node, Argument.PASSAGE_MODE_PROPERTY);
			if (oldPassage == Argument.PassageMode.DEFAULT) {
				doTextInsert(pos, newPassage.toString().toLowerCase() + " ", getEditGroup(node, Argument.PASSAGE_MODE_PROPERTY));
			} else {
				try {
					getScanner().readToToken(passageTerminalSymbol, node.getStartPosition());
					int start = getScanner().getCurrentStartOffset();
					int end = getScanner().getCurrentEndOffset();
				
					if (newPassage == Argument.PassageMode.DEFAULT) {
						end = getScanner().getNextStartOffset(end, false);
						doTextRemove(start, end - start, getEditGroup(node, Argument.PASSAGE_MODE_PROPERTY));
					} else {
						doTextReplace(start, end - start, newPassage.toString().toLowerCase(), getEditGroup(node, Argument.PASSAGE_MODE_PROPERTY));
					}
					
					pos = end;
				} catch (CoreException e) { 
					handleException(e);
				}
			}
		} else {
			if (oldPassage != Argument.PassageMode.DEFAULT) {
				try {
					pos = getScanner().getTokenEndOffset(passageTerminalSymbol, pos);
				} catch (CoreException e) { 
					handleException(e);
				}
			}
		}
		
		pos = rewriteNode(node, Argument.TYPE_PROPERTY, pos, oldPassage == PassageMode.DEFAULT ? ASTRewriteFormatter.NONE : ASTRewriteFormatter.SPACE);
		pos = rewriteNode(node, Argument.NAME_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		pos = rewriteNode(node, Argument.DEFAULT_VALUE_PROPERTY, pos, ASTRewriteFormatter.EQUALS);
		
		return false;
	}
	
	@Override
	public boolean visit(ArrayAccess node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteRequiredNode(node, ArrayAccess.ARRAY_PROPERTY);
		rewriteNodeList(node, ArrayAccess.INDEXES_PROPERTY, pos, "", ", ");
		return false;
	}
	
	@Override
	public boolean visit(ArrayInitializer node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteNodeList(node, ArrayInitializer.FRAGMENTS_PROPERTY, node.getStartPosition() + 1, "", ", ");
		return false;
	}
	
	@Override
	public boolean visit(ArrayInitializerFragment node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int changeKind = getChangeKind(node, ArrayInitializerFragment.EXPRESSION_PROPERTY);
		int pos = rewriteNode(node, ArrayInitializerFragment.EXPRESSION_PROPERTY, node.getStartPosition(), ASTRewriteFormatter.NONE);
		switch(changeKind) {
		case RewriteEvent.INSERTED:
			doTextInsert(pos, ": ", getEditGroup(node, ArrayInitializerFragment.EXPRESSION_PROPERTY));
			break;
		case RewriteEvent.REMOVED:
			try {
				int end = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameCOLON, pos);
				doTextRemove(pos, end - pos, getEditGroup(node, ArrayInitializerFragment.EXPRESSION_PROPERTY));
			} catch (CoreException e) { 
				handleException(e);
			}
			break;
		}
		
		rewriteRequiredNode(node, ArrayInitializerFragment.INITIALIZER_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(ArrayLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteNodeList(node, ArrayLiteral.ARGUMENTS_PROPERTY, node.getStartPosition() + 1, "", ", ");
		return false;
	}
	
	@Override
	public boolean visit(AsmBlock node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int startIndent= getIndent(node.getStartPosition()) + 1;
		int startPos= getPosAfterLeftBrace(node.getStartPosition() + 3);
		
		rewriteParagraphList(node, AsmBlock.STATEMENTS_PROPERTY, startPos, startIndent, -1, 2);
		return false;
	}
	
	@Override
	public boolean visit(AsmStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteNodeList(node, AsmStatement.TOKENS_PROPERTY, node.getStartPosition(), "", " ");
		return false;
	}
	
	@Override
	public boolean visit(AsmToken node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteOperation(node, AsmToken.TOKEN_PROPERTY, node.getStartPosition());
		return false;
	}
	
	@Override
	public boolean visit(AssertExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteRequiredNode(node, AssertExpression.EXPRESSION_PROPERTY);
		rewriteNode(node, AssertExpression.MESSAGE_PROPERTY, pos, ASTRewriteFormatter.COMMA);
		
		return false;
	}
	
	@Override
	public boolean visit(Assignment node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= rewriteRequiredNode(node, Assignment.LEFT_HAND_SIDE_PROPERTY);
		rewriteOperation(node, Assignment.OPERATOR_PROPERTY, pos);
		rewriteRequiredNode(node, Assignment.RIGHT_HAND_SIDE_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(AssociativeArrayType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, AssociativeArrayType.COMPONENT_TYPE_PROPERTY);
		rewriteRequiredNode(node, AssociativeArrayType.KEY_TYPE_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(BaseClass node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int changeKind = getChangeKind(node, BaseClass.MODIFIER_PROPERTY);
		int pos = rewriteNode(node, BaseClass.MODIFIER_PROPERTY, node.getStartPosition(), ASTRewriteFormatter.NONE);
		switch(changeKind) {
		case RewriteEvent.INSERTED:
			doTextInsert(pos, " ", getEditGroup(node, BaseClass.MODIFIER_PROPERTY));
			break;
		}
		
		rewriteRequiredNode(node, BaseClass.TYPE_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(Block node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int startPos;
		if (isCollapsed(node)) {
			startPos= node.getStartPosition();
		} else {
			startPos= getPosAfterLeftBraceOrSemicolon(node.getStartPosition());
		}
		int startIndent= getIndent(node.getStartPosition()) + 1;
		rewriteParagraphList(node, Block.STATEMENTS_PROPERTY, startPos, startIndent, 0, 1);
		return false;
	}
	
	@Override
	public boolean visit(BooleanLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		Boolean newLiteral= (Boolean) getNewValue(node, BooleanLiteral.BOOLEAN_VALUE_PROPERTY);
		TextEditGroup group = getEditGroup(node, BooleanLiteral.BOOLEAN_VALUE_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newLiteral.toString(), group);
		return false;
	}
	
	@Override
	public boolean visit(BreakStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}

		try {
			int offset= getScanner().getTokenEndOffset(ITerminalSymbols.TokenNamebreak, node.getStartPosition());
			rewriteNode(node, BreakStatement.LABEL_PROPERTY, offset, ASTRewriteFormatter.SPACE); // space between break and label
		} catch (CoreException e) {
			handleException(e);
		}
		return false;		
	}
	
	@Override
	public boolean visit(CallExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteRequiredNode(node, CallExpression.EXPRESSION_PROPERTY);
		rewriteNodeList(node, CallExpression.ARGUMENTS_PROPERTY, pos, "", ", ");
		
		return false;
	}
	
	@Override
	public boolean visit(CastExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, CastExpression.TYPE_PROPERTY);
		rewriteRequiredNode(node, CastExpression.EXPRESSION_PROPERTY);
		
		return false;
	}	

	@Override
	public boolean visit(CharacterLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		String escapedSeq= (String) getNewValue(node, CharacterLiteral.ESCAPED_VALUE_PROPERTY);
		TextEditGroup group = getEditGroup(node, CharacterLiteral.ESCAPED_VALUE_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), escapedSeq, group);
		return false;
	}
	
	@Override
	public boolean visit(CatchClause node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int typeChange = getChangeKind(node, CatchClause.TYPE_PROPERTY);
		int nameChange = getChangeKind(node, CatchClause.NAME_PROPERTY);
		
		if (typeChange == RewriteEvent.INSERTED || typeChange == RewriteEvent.REMOVED) {
			if (typeChange != nameChange) {
				throw new IllegalStateException("The type and name of a CatchClause must be removed or inserted together");
			}
		}
		
		int pos = node.getStartPosition();
		
		if (typeChange == RewriteEvent.INSERTED) {
			try {
				pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNamecatch, pos);
				doTextInsert(pos, "(", getEditGroup(node, CatchClause.TYPE_PROPERTY));
				pos = rewriteNode(node, CatchClause.TYPE_PROPERTY, pos, ASTRewriteFormatter.NONE);
				pos = rewriteNode(node, CatchClause.NAME_PROPERTY, pos, ASTRewriteFormatter.SPACE);
				doTextInsert(pos, ")", getEditGroup(node, CatchClause.TYPE_PROPERTY));
			} catch (Exception e) {
				
			}			
		} else if (typeChange == RewriteEvent.REMOVED) {
			try {
				ASTNode oldName = (ASTNode) getEvent(node, CatchClause.NAME_PROPERTY).getOriginalValue();
				
				pos = getScanner().getTokenStartOffset(ITerminalSymbols.TokenNameLPAREN, pos);
				int end = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRPAREN, oldName.getStartPosition() + oldName.getLength());
				doTextRemove(pos, end - pos, getEditGroup(node, CatchClause.TYPE_PROPERTY));
			} catch (Exception e) {
				
			}
		} else {
			rewriteRequiredNode(node, CatchClause.NAME_PROPERTY);
			rewriteRequiredNode(node, CatchClause.TYPE_PROPERTY);
		}
		
		rewriteRequiredNode(node, CatchClause.BODY_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(CodeComment node) {
		return false;
	}
	
	@Override 
	public boolean visit(CompilationUnit node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int startPos= rewriteNode(node, CompilationUnit.MODULE_DECLARATION_PROPERTY, 0, ASTRewriteFormatter.NONE);
			
		if (getChangeKind(node, CompilationUnit.MODULE_DECLARATION_PROPERTY) == RewriteEvent.INSERTED) {
			doTextInsert(0, getLineDelimiter(), getEditGroup(node, CompilationUnit.MODULE_DECLARATION_PROPERTY));
		}
				
		startPos= rewriteParagraphList(node, CompilationUnit.DECLARATIONS_PROPERTY, startPos, 0, -1, 2);
		return false;
	}
	
	@Override
	public boolean visit(ConditionalExpression node) { // expression ? thenExpression : elseExpression
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, ConditionalExpression.EXPRESSION_PROPERTY);
		rewriteRequiredNode(node, ConditionalExpression.THEN_EXPRESSION_PROPERTY);
		rewriteRequiredNode(node, ConditionalExpression.ELSE_EXPRESSION_PROPERTY);	
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		try {
			int offset= getScanner().getTokenEndOffset(ITerminalSymbols.TokenNamecontinue, node.getStartPosition());
			rewriteNode(node, ContinueStatement.LABEL_PROPERTY, offset, ASTRewriteFormatter.SPACE); // space between continue and label
		} catch (CoreException e) {
			handleException(e);
		}
		return false;
	}

	@Override
	public boolean visit(DDocComment node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		RewriteEvent event= getEvent(node, DDocComment.TEXT_PROPERTY);
		if (event != null && event.getChangeKind() != RewriteEvent.UNCHANGED) {
			try {
				String newOperation= event.getNewValue().toString();
				TextEditGroup editGroup= getEditGroup(event);
				getScanner().readNext(node.getStartPosition(), false);
				doTextReplace(getScanner().getCurrentStartOffset(), getScanner().getCurrentLength(), newOperation, editGroup);
			} catch (CoreException e) {
				handleException(e);
			}
		}
		
		return false;
	}

	@Override
	public boolean visit(DebugAssignment node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, DebugAssignment.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, DebugAssignment.MODIFIERS_PROPERTY, pos);
		
		rewriteRequiredNode(node, DebugAssignment.VERSION_PROPERTY);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameSEMICOLON, pos);
			rewriteNode(node, DebugAssignment.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			handleException(e);
		}
		
		return false;
	}
	
	@Override
	public boolean visit(DebugDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, DebugDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, DebugDeclaration.MODIFIERS_PROPERTY, pos);		
		pos = rewriteVersion(node, DebugDeclaration.VERSION_PROPERTY, ITerminalSymbols.TokenNamedebug, pos);		
		pos = rewriteThenElseDeclarations(node, DebugDeclaration.THEN_DECLARATIONS_PROPERTY, DebugDeclaration.ELSE_DECLARATIONS_PROPERTY, pos);		
		rewriteNode(node, DebugDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		
		return false;
	}
	
	@Override
	public boolean visit(DebugStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = node.getStartPosition();		
		pos = rewriteVersion(node, DebugStatement.VERSION_PROPERTY, ITerminalSymbols.TokenNamedebug, pos);		
		rewriteThenElseBody(node, DebugStatement.THEN_BODY_PROPERTY, DebugStatement.ELSE_BODY_PROPERTY, pos);
		
		return false;
	}
	
	@Override
	public boolean visit(DeclarationStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, DeclarationStatement.DECLARATION_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(DefaultStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, DefaultStatement.BODY_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(DelegateType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteRequiredNode(node, DelegateType.RETURN_TYPE_PROPERTY);
		
		if (isChanged(node, DelegateType.FUNCTION_POINTER_PROPERTY)) {
			try {
				int token = getScanner().readNext(pos, true);
				String newValue = token == ITerminalSymbols.TokenNamefunction ? "delegate" : "function";
				doTextReplace(getScanner().getCurrentStartOffset(), getScanner().getCurrentLength(), newValue, getEditGroup(node, DelegateType.FUNCTION_POINTER_PROPERTY));
			} catch (CoreException e) {
				handleException(e);
			}
		}
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameLPAREN, pos);
		} catch (CoreException e) {
			handleException(e);
		}
		
		pos = rewriteNodeList(node, DelegateType.ARGUMENTS_PROPERTY, pos, "", ", ");
		
		if (isChanged(node, DelegateType.VARIADIC_PROPERTY)) {
			boolean originalVariadic = (Boolean) getOriginalValue(node, DelegateType.VARIADIC_PROPERTY);
			if (originalVariadic) {
				try {
					getScanner().readNext(pos, true);
					doTextRemove(getScanner().getCurrentStartOffset(), getScanner().getCurrentLength(), getEditGroup(node, DelegateType.VARIADIC_PROPERTY));
				} catch (CoreException e) {
					handleException(e);
				}
			} else {
				doTextInsert(pos, "...", getEditGroup(node, DelegateType.VARIADIC_PROPERTY));
			}
		}
		
		return false;
	}
	
	@Override
	public boolean visit(DeleteExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, DeleteExpression.EXPRESSION_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(DotIdentifierExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteNode(node, DotIdentifierExpression.EXPRESSION_PROPERTY, node.getStartPosition(), ASTRewriteFormatter.NONE);
		rewriteRequiredNode(node, DotIdentifierExpression.NAME_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(DotTemplateTypeExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteNode(node, DotTemplateTypeExpression.EXPRESSION_PROPERTY, node.getStartPosition(), ASTRewriteFormatter.NONE);
		rewriteRequiredNode(node, DotTemplateTypeExpression.TEMPLATE_TYPE_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(DynamicArrayType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, DynamicArrayType.COMPONENT_TYPE_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(DollarLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		changeNotSupported(node); // no modification possible
		return false;
	}

	@Override
	public boolean visit(DoStatement node) { // do statement while expression
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= node.getStartPosition();
		try {
			RewriteEvent event= getEvent(node, DoStatement.BODY_PROPERTY);
			if (event != null && event.getChangeKind() == RewriteEvent.REPLACED) {
				int startOffset= getScanner().getTokenEndOffset(ITerminalSymbols.TokenNamedo, pos);
				ASTNode body= (ASTNode) event.getOriginalValue();
				int bodyEnd= body.getStartPosition() + body.getLength();
				int endPos= getScanner().getTokenStartOffset(ITerminalSymbols.TokenNamewhile, bodyEnd);
				rewriteBodyNode(node, DoStatement.BODY_PROPERTY, startOffset, endPos, getIndent(node.getStartPosition()), this.formatter.DO_BLOCK); // body
			} else {
				voidVisit(node, DoStatement.BODY_PROPERTY);
			}
		} catch (CoreException e) {
			handleException(e);
		}

		rewriteRequiredNode(node, DoStatement.EXPRESSION_PROPERTY);	
		return false;
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, EnumDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, EnumDeclaration.MODIFIERS_PROPERTY, pos);

		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameenum, node.getStartPosition());
			pos = rewriteNode(node, EnumDeclaration.NAME_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			handleException(e);
		}
		
		pos = rewriteNode(node, EnumDeclaration.BASE_TYPE_PROPERTY, pos, ASTRewriteFormatter.COLON);
		
		// startPos : find position after left brace of type, be aware that bracket might be missing
		int startIndent= getIndent(node.getStartPosition()) + 1;
		int startPos= getPosAfterLeftBrace(pos);
		
		pos = rewriteParagraphList(node, EnumDeclaration.ENUM_MEMBERS_PROPERTY, startPos, startIndent, -1, 2);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRCURLY, pos);
			rewriteNode(node, EnumDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			handleException(e);
		}
	
		return false;
	}
	
	@Override
	public boolean visit(EnumMember node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteRequiredNode(node, EnumMember.NAME_PROPERTY);
		
		rewriteNode(node, EnumMember.VALUE_PROPERTY, pos, ASTRewriteFormatter.EQUALS);
		
		return false;
	}
	
	@Override
	public boolean visit(ExpressionInitializer node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, ExpressionInitializer.EXPRESSION_PROPERTY);
		
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement node) { // expression
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, ExpressionStatement.EXPRESSION_PROPERTY);	
		return false;
	}
	
	@Override
	public boolean visit(ExternDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, ExternDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, ExternDeclaration.MODIFIERS_PROPERTY, pos);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameLPAREN, pos);
		} catch (CoreException e) {
			handleException(e);
		}
		
		rewriteOperationUntil(node, ExternDeclaration.LINKAGE_PROPERTY, ITerminalSymbols.TokenNameRPAREN, pos);
		
		try {
			pos = getScanner().getNextEndOffset(pos, true);			
			if (getOriginalValue(node, ExternDeclaration.LINKAGE_PROPERTY) != Linkage.DEFAULT) {
				pos = getScanner().getNextEndOffset(pos, true);
			}
		} catch (CoreException e) {
			handleException(e);
		}
		
		// startPos : find position after left brace of type, be aware that bracket might be missing
		int startIndent= getIndent(node.getStartPosition()) + 1;
		int startPos= getPosAfterLeftBraceOrColon(pos);
		pos = rewriteParagraphList(node, ExternDeclaration.DECLARATIONS_PROPERTY, startPos, startIndent, -1, 2);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRCURLY, pos);
		} catch (CoreException e) {
			// Maybe is was a colon
		}
		rewriteNode(node, ExternDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		
		return false;
	}
	
	@Override
	public boolean visit(ForeachStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = node.getStartPosition();
		
		if (isChanged(node, ForeachStatement.REVERSE_PROPERTY)) {
			try {
				int token = getScanner().readNext(pos, true);
				String newValue = token == ITerminalSymbols.TokenNameforeach ? "foreach_reverse" : "foreach";
				doTextReplace(getScanner().getCurrentStartOffset(), getScanner().getCurrentLength(), newValue, getEditGroup(node, ForeachStatement.REVERSE_PROPERTY));
				
				pos = getScanner().getCurrentEndOffset();
			} catch (CoreException e) {
				handleException(e);
			}
		}
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameLPAREN, pos);
		} catch (CoreException e) {
			handleException(e);
		}
		
		rewriteNodeList(node, ForeachStatement.ARGUMENTS_PROPERTY, pos, "", ", ");		
		rewriteRequiredNode(node, ForeachStatement.EXPRESSION_PROPERTY);		
		rewriteRequiredNode(node, ForeachStatement.BODY_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(ForStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		// TODO
		
		return false;
	}
	
	@Override
	public boolean visit(GotoCaseStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, GotoCaseStatement.LABEL_PROPERTY);	
		return false;
	}
	
	@Override
	public boolean visit(GotoDefaultStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		changeNotSupported(node); // no modification possible
		return false;
	}
	
	@Override
	public boolean visit(GotoStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, GotoStatement.LABEL_PROPERTY);	
		return false;
	}

	@Override
	public boolean visit(InfixExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= rewriteRequiredNode(node, InfixExpression.LEFT_OPERAND_PROPERTY);
		
		boolean needsNewOperation= isChanged(node, InfixExpression.OPERATOR_PROPERTY);
		String operation= getNewValue(node, InfixExpression.OPERATOR_PROPERTY).toString();
		if (needsNewOperation) {
			replaceOperation(pos, operation, getEditGroup(node, InfixExpression.OPERATOR_PROPERTY));
		}
			
		pos= rewriteRequiredNode(node, InfixExpression.RIGHT_OPERAND_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(InvariantDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, InvariantDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, InvariantDeclaration.MODIFIERS_PROPERTY, pos);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRCURLY, pos);
			rewriteNode(node, InvariantDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			
		}
		
		return false;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, LabeledStatement.LABEL_PROPERTY);
		rewriteRequiredNode(node, LabeledStatement.BODY_PROPERTY);		
		return false;
	}

	@Override
	public boolean visit(Modifier node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		String newText= getNewValue(node, Modifier.MODIFIER_KEYWORD_PROPERTY).toString(); // type Modifier.ModifierKeyword
		TextEditGroup group = getEditGroup(node, Modifier.MODIFIER_KEYWORD_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newText, group);
		return false;
	}
	
	@Override
	public boolean visit(ModuleDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, ModuleDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		
		rewriteRequiredNode(node, ModuleDeclaration.NAME_PROPERTY);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameSEMICOLON, pos);
			rewriteNode(node, ModuleDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			handleException(e);
		}
	
		return false;
	}

	@Override
	public boolean visit(NullLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		changeNotSupported(node); // no modification possible
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		String newLiteral= (String) getNewValue(node, NumberLiteral.TOKEN_PROPERTY);
		TextEditGroup group = getEditGroup(node, NumberLiteral.TOKEN_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newLiteral, group);
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, ParenthesizedExpression.EXPRESSION_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(PointerType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, PointerType.COMPONENT_TYPE_PROPERTY);
		return false;
	}

	@Override
	public boolean visit(PostfixExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= rewriteRequiredNode(node, PostfixExpression.OPERAND_PROPERTY);
		rewriteOperation(node, PostfixExpression.OPERATOR_PROPERTY, pos);
		return false;		
	}
	
	@Override
	public boolean visit(Pragma node) {
		return false;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteOperation(node, PrefixExpression.OPERATOR_PROPERTY, node.getStartPosition());
		rewriteRequiredNode(node, PrefixExpression.OPERAND_PROPERTY);
		return false;	
	}

	@Override
	public boolean visit(PrimitiveType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		PrimitiveType.Code newCode= (PrimitiveType.Code) getNewValue(node, PrimitiveType.PRIMITIVE_TYPE_CODE_PROPERTY);
		TextEditGroup group = getEditGroup(node, PrimitiveType.PRIMITIVE_TYPE_CODE_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newCode.toString(), group);
		return false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, QualifiedName.QUALIFIER_PROPERTY);
		rewriteRequiredNode(node, QualifiedName.NAME_PROPERTY);
		return false;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		try {
			int offset= getScanner().getTokenEndOffset(ITerminalSymbols.TokenNamereturn, node.getStartPosition());
			
			// bug 103970
			if (getChangeKind(node, ReturnStatement.EXPRESSION_PROPERTY) == RewriteEvent.REPLACED) {
				if (offset == getExtendedOffset((ASTNode) getOriginalValue(node, ReturnStatement.EXPRESSION_PROPERTY))) {
					doTextInsert(offset, String.valueOf(' '), getEditGroup(node, ReturnStatement.EXPRESSION_PROPERTY));
				}
			}
			rewriteNode(node, ReturnStatement.EXPRESSION_PROPERTY, offset, ASTRewriteFormatter.SPACE);	

		} catch (CoreException e) {
			handleException(e);
		}
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		String newString= (String) getNewValue(node, SimpleName.IDENTIFIER_PROPERTY);
		TextEditGroup group = getEditGroup(node, SimpleName.IDENTIFIER_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newString, group);
		return false;
	}

	@Override
	public boolean visit(SimpleType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, SimpleType.NAME_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(StaticArrayType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, StaticArrayType.COMPONENT_TYPE_PROPERTY);
		rewriteRequiredNode(node, StaticArrayType.SIZE_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(StaticIfDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, StaticIfDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, StaticIfDeclaration.MODIFIERS_PROPERTY, pos);		
		pos = rewriteRequiredNode(node, StaticIfDeclaration.EXPRESSION_PROPERTY);
		
		try {
			pos = getScanner().getNextEndOffset(pos, true);
		} catch (CoreException e) {
			handleException(e);
		}
		
		pos = rewriteThenElseDeclarations(node, StaticIfDeclaration.THEN_DECLARATIONS_PROPERTY, StaticIfDeclaration.ELSE_DECLARATIONS_PROPERTY, pos);		
		rewriteNode(node, StaticIfDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		
		return false;
	}
	
	@Override
	public boolean visit(StaticIfStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteRequiredNode(node, StaticIfDeclaration.EXPRESSION_PROPERTY);
		
		try {
			pos = getScanner().getNextEndOffset(pos, true);
		} catch (CoreException e) {
			handleException(e);
		}
		
		rewriteThenElseBody(node, DebugStatement.THEN_BODY_PROPERTY, DebugStatement.ELSE_BODY_PROPERTY, pos);
		
		return false;
	}
	
	@Override
	public boolean visit(MixinDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, MixinDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, MixinDeclaration.MODIFIERS_PROPERTY, pos);		
		pos = rewriteRequiredNode(node, MixinDeclaration.TYPE_PROPERTY);
		pos = rewriteNode(node, MixinDeclaration.NAME_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameSEMICOLON, pos);
		} catch (CoreException e) {
			handleException(e);
		}
		
		rewriteNode(node, MixinDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		
		return false;
	}
	
	@Override
	public boolean visit(QualifiedType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = node.getStartPosition();
		rewriteNode(node, QualifiedType.QUALIFIER_PROPERTY, pos, ASTRewriteFormatter.NONE);
		rewriteRequiredNode(node, QualifiedType.TYPE_PROPERTY);
		
		return false;
	}

	@Override
	public boolean visit(StringLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		String escapedSeq= (String) getNewValue(node, StringLiteral.ESCAPED_VALUE_PROPERTY);
		TextEditGroup group = getEditGroup(node, StringLiteral.ESCAPED_VALUE_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), escapedSeq, group);

		return false;
	}
	
	@Override
	public boolean visit(StringsExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteNodeList(node, StringsExpression.STRING_LITERALS_PROPERTY, node.getStartPosition(), "", " ");
		
		return false;
	}
	
	@Override
	public boolean visit(StructInitializer node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteNodeList(node, StructInitializer.FRAGMENTS_PROPERTY, node.getStartPosition() + 1, "", ", ");
		
		return false;
	}
	
	@Override
	public boolean visit(SuperLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		changeNotSupported(node); // no modification possible
		return false;
	}
	
	@Override
	public boolean visit(SwitchStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, SwitchStatement.EXPRESSION_PROPERTY);
		rewriteRequiredNode(node, SwitchStatement.BODY_PROPERTY);
		
		return false;
	}

	@Override
	public boolean visit(ThisLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		changeNotSupported(node); // no modification possible
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, ThrowStatement.EXPRESSION_PROPERTY);		
		return false;	
	}
	@Override
	public boolean visit(TryStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= rewriteRequiredNode(node, TryStatement.BODY_PROPERTY);
		
		if (isChanged(node, TryStatement.CATCH_CLAUSES_PROPERTY)) {
			int indent= getIndent(node.getStartPosition());
			String prefix= this.formatter.CATCH_BLOCK.getPrefix(indent);
			pos= rewriteNodeList(node, TryStatement.CATCH_CLAUSES_PROPERTY, pos, prefix, prefix);
		} else {
			pos= doVisit(node, TryStatement.CATCH_CLAUSES_PROPERTY, pos);
		}
		rewriteNode(node, TryStatement.FINALLY_PROPERTY, pos, this.formatter.FINALLY_BLOCK);
		return false;
	}
	
	@Override
	public boolean visit(TupleTemplateParameter node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, TupleTemplateParameter.NAME_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(TypedefDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		int pos = rewriteParagraphList(node, TypedefDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, TypedefDeclaration.MODIFIERS_PROPERTY, pos);
		
		pos= rewriteNode(node, TypedefDeclaration.TYPE_PROPERTY, pos, ASTRewriteFormatter.NONE);
		pos = rewriteNodeList(node, TypedefDeclaration.FRAGMENTS_PROPERTY, pos, "", ", "); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameSEMICOLON, pos);
			rewriteNode(node, TypedefDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			
		}
		
		return false;
	}
	
	@Override
	public boolean visit(TypeDotIdentifierExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, TypeDotIdentifierExpression.TYPE_PROPERTY);
		rewriteRequiredNode(node, TypeDotIdentifierExpression.NAME_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(TypeExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, TypeExpression.TYPE_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(TypeidExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, TypeidExpression.TYPE_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(TypeofType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, TypeofType.EXPRESSION_PROPERTY);
		
		return false;	
	}
	
	@Override
	public boolean visit(TypeTemplateParameter node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteRequiredNode(node, TypeTemplateParameter.NAME_PROPERTY);
		pos = rewriteNode(node, TypeTemplateParameter.SPECIFIC_TYPE_PROPERTY, pos, ASTRewriteFormatter.COLON);
		pos = rewriteNode(node, TypeTemplateParameter.DEFAULT_TYPE_PROPERTY, pos, ASTRewriteFormatter.EQUALS);
		return false;
	}
	
	@Override
	public boolean visit(UnitTestDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, UnitTestDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, UnitTestDeclaration.MODIFIERS_PROPERTY, pos);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRCURLY, pos);
			rewriteNode(node, UnitTestDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ValueTemplateParameter node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteRequiredNode(node, ValueTemplateParameter.TYPE_PROPERTY);
		pos = rewriteRequiredNode(node, ValueTemplateParameter.NAME_PROPERTY);
		pos = rewriteNode(node, ValueTemplateParameter.SPECIFIC_VALUE_PROPERTY, pos, ASTRewriteFormatter.COLON);
		pos = rewriteNode(node, ValueTemplateParameter.DEFAULT_VALUE_PROPERTY, pos, ASTRewriteFormatter.EQUALS);
		return false;
	}

	@Override
	public boolean visit(VariableDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		int pos = rewriteParagraphList(node, VariableDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, VariableDeclaration.MODIFIERS_PROPERTY, pos);
		
		pos= rewriteNode(node, VariableDeclaration.TYPE_PROPERTY, pos, ASTRewriteFormatter.NONE);
		pos = rewriteNodeList(node, VariableDeclaration.FRAGMENTS_PROPERTY, pos, "", ", "); //$NON-NLS-1$ //$NON-NLS-2$
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameSEMICOLON, pos);
			rewriteNode(node, VariableDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			
		}
		
		return false;
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= rewriteRequiredNode(node, VariableDeclarationFragment.NAME_PROPERTY);
		rewriteNode(node, VariableDeclarationFragment.INITIALIZER_PROPERTY, pos, this.formatter.VAR_INITIALIZER);
		return false;
	}
	
	@Override
	public boolean visit(Version node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		String newValue= (String) getNewValue(node, Version.VALUE_PROPERTY);
		TextEditGroup group = getEditGroup(node, Version.VALUE_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newValue, group);
		return false;
	}
		
	@Override
	public boolean visit(VersionAssignment node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, VersionAssignment.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, VersionAssignment.MODIFIERS_PROPERTY, pos);
		
		rewriteRequiredNode(node, VersionAssignment.VERSION_PROPERTY);
		
		try {
			pos = getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameSEMICOLON, pos);
			rewriteNode(node, VersionAssignment.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		} catch (CoreException e) {
			
		}
		
		return false;
	}
	
	@Override
	public boolean visit(VersionDeclaration node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = rewriteParagraphList(node, VersionDeclaration.PRE_D_DOCS_PROPERTY, 0, 0, 0, 0);
		pos = rewriteModifiers(node, VersionDeclaration.MODIFIERS_PROPERTY, pos);		
		pos = rewriteVersion(node, VersionDeclaration.VERSION_PROPERTY, ITerminalSymbols.TokenNameversion, pos);		
		pos = rewriteThenElseDeclarations(node, VersionDeclaration.THEN_DECLARATIONS_PROPERTY, VersionDeclaration.ELSE_DECLARATIONS_PROPERTY, pos);		
		rewriteNode(node, VersionDeclaration.POST_D_DOC_PROPERTY, pos, ASTRewriteFormatter.SPACE);
		
		return false;
	}
	
	@Override
	public boolean visit(VersionStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos = node.getStartPosition();		
		pos = rewriteVersion(node, VersionStatement.VERSION_PROPERTY, ITerminalSymbols.TokenNameversion, pos);		
		rewriteThenElseBody(node, VersionStatement.THEN_BODY_PROPERTY, VersionStatement.ELSE_BODY_PROPERTY, pos);
		
		return false;
	}
	
	@Override
	public boolean visit(VoidInitializer node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		changeNotSupported(node); // no modification possible
		return false;
	}
	
	@Override
	public boolean visit(VolatileStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, VolatileStatement.BODY_PROPERTY);
		
		return false;
	}
	
	@Override
	public boolean visit(WhileStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= rewriteRequiredNode(node, WhileStatement.EXPRESSION_PROPERTY);
		
		try {
			if (isChanged(node, WhileStatement.BODY_PROPERTY)) {
				int startOffset= getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRPAREN, pos);
				rewriteBodyNode(node, WhileStatement.BODY_PROPERTY, startOffset, -1, getIndent(node.getStartPosition()), this.formatter.WHILE_BLOCK); // body
			} else {
				voidVisit(node, WhileStatement.BODY_PROPERTY);
			}
		} catch (CoreException e) {
			handleException(e);
		}
		return false;
	}
	
	@Override
	public boolean visit(WithStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= rewriteRequiredNode(node, WithStatement.EXPRESSION_PROPERTY);
		
		try {
			if (isChanged(node, WithStatement.BODY_PROPERTY)) {
				int startOffset= getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameRPAREN, pos);
				rewriteBodyNode(node, WithStatement.BODY_PROPERTY, startOffset, -1, getIndent(node.getStartPosition()), this.formatter.WITH_BLOCK); // body
			} else {
				voidVisit(node, WithStatement.BODY_PROPERTY);
			}
		} catch (CoreException e) {
			handleException(e);
		}
		return false;
	}
	
	final void voidVisit(ASTNode node) {
		node.accept(this);
	}
	
	private final void voidVisit(ASTNode parent, StructuralPropertyDescriptor property) {
		Object node= getOriginalValue(parent, property);
		if (property.isChildProperty() && node != null) {
			voidVisit((ASTNode) node);
		} else if (property.isChildListProperty()) {
			voidVisitList((List) node);
		}
	}
	
	private void voidVisitList(List list) {
		for (Iterator iter= list.iterator(); iter.hasNext();) {
			doVisit(((ASTNode) iter.next()));
		}
	}
}
