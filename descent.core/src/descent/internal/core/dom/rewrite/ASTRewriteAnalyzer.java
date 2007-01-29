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
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.Assignment;
import descent.core.dom.Block;
import descent.core.dom.BooleanLiteral;
import descent.core.dom.BreakStatement;
import descent.core.dom.CharacterLiteral;
import descent.core.dom.ChildPropertyDescriptor;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ConditionalExpression;
import descent.core.dom.ContinueStatement;
import descent.core.dom.DoStatement;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.InfixExpression;
import descent.core.dom.LabeledStatement;
import descent.core.dom.Modifier;
import descent.core.dom.NullLiteral;
import descent.core.dom.NumberLiteral;
import descent.core.dom.ParenthesizedExpression;
import descent.core.dom.PostfixExpression;
import descent.core.dom.PrefixExpression;
import descent.core.dom.PrimitiveType;
import descent.core.dom.QualifiedName;
import descent.core.dom.QualifiedType;
import descent.core.dom.ReturnStatement;
import descent.core.dom.SimpleName;
import descent.core.dom.SimpleType;
import descent.core.dom.StringLiteral;
import descent.core.dom.StructuralPropertyDescriptor;
import descent.core.dom.SwitchCase;
import descent.core.dom.SynchronizedStatement;
import descent.core.dom.ThisLiteral;
import descent.core.dom.ThrowStatement;
import descent.core.dom.ToolFactory;
import descent.core.dom.TryStatement;
import descent.core.dom.WhileStatement;
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
		
	final TokenScanner getScanner() {
		if (this.tokenScanner == null) {
			IScanner scanner= ToolFactory.createScanner(true, true, false, false, AST.D1);
			scanner.setSource(this.content);
			this.tokenScanner= new TokenScanner(scanner);
		}
		return this.tokenScanner;
	}
	
	final char[] getContent() {
		return this.content;
	}
	
	final LineInformation getLineInformation() {
		return this.lineInfo;
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
	
	final int getExtendedOffset(ASTNode node) {
		return getExtendedRange(node).getStartPosition();
	}
	
	final int getExtendedEnd(ASTNode node) {
		TargetSourceRangeComputer.SourceRange range= getExtendedRange(node);
		return range.getStartPosition() + range.getLength();
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
	
	private final int getChangeKind(ASTNode node, StructuralPropertyDescriptor property) {
		RewriteEvent event= getEvent(node, property);
		if (event != null) {
			return event.getChangeKind();
		}
		return RewriteEvent.UNCHANGED;
	}
	
	private final boolean hasChildrenChanges(ASTNode node) {
		return this.eventStore.hasChangedProperties(node);
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
		
	private final TextEditGroup getEditGroup(ASTNode parent, StructuralPropertyDescriptor property) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null) {
			return getEditGroup(event);
		}
		return null;
	}
	
	final RewriteEvent getEvent(ASTNode parent, StructuralPropertyDescriptor property) {
		return this.eventStore.getEvent(parent, property);
	}
	
	final TextEditGroup getEditGroup(RewriteEvent change) {
		return this.eventStore.getEventEditGroup(change);
	}
	
	private final Object getOriginalValue(ASTNode parent, StructuralPropertyDescriptor property) {
		return this.eventStore.getOriginalValue(parent, property);
	}
	
	private final Object getNewValue(ASTNode parent, StructuralPropertyDescriptor property) {
		return this.eventStore.getNewValue(parent, property);
	}	
	
	final void addEdit(TextEdit edit) {
		this.currentEdit.addChild(edit);
	}
	
	final String getLineDelimiter() {
		return this.formatter.lineDelimiter;
	}
	
	final String createIndentString(int indent) {
	    return this.formatter.createIndentString(indent);
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
	
		
	final String getIndentAtOffset(int pos) {
		return this.formatter.getIndentString(getIndentOfLine(pos));
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
	
	final void addEditGroup(TextEditGroup editGroup, TextEdit edit) {
		editGroup.addTextEdit(edit);
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
	
	private final boolean doVisitUnchangedChildren(ASTNode parent) {
		List properties= parent.structuralPropertiesForType();
		for (int i= 0; i < properties.size(); i++) {
			voidVisit(parent, (StructuralPropertyDescriptor) properties.get(i));
		}
		return false;
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
			
	private void changeNotSupported(ASTNode node) {
		Assert.isTrue(false, "Change not supported in " + node.getClass().getName()); //$NON-NLS-1$
	}
	
	
	class ListRewriter {
		protected String contantSeparator;
		protected int startPos;
		
		protected RewriteEvent[] list;
		
		protected final ASTNode getOriginalNode(int index) {
			return (ASTNode) this.list[index].getOriginalValue();
		}
		
		protected final ASTNode getNewNode(int index) {
			return (ASTNode) this.list[index].getNewValue();
		}
		
		protected String getSeparatorString(int nodeIndex) {
			return this.contantSeparator;
		}
		
		protected int getInitialIndent() {
			return getIndent(this.startPos);
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
		
		protected int getEndOfNode(ASTNode node) {
			return getExtendedEnd(node);
		}		
		
		public final int rewriteList(ASTNode parent, StructuralPropertyDescriptor property, int offset, String keyword, String separator) {
			this.contantSeparator= separator;
			return rewriteList(parent, property, offset, keyword);
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
	
	private int rewriteJavadoc(ASTNode node, StructuralPropertyDescriptor property) {
		int pos= rewriteNode(node, property, node.getStartPosition(), ASTRewriteFormatter.NONE);
		int changeKind= getChangeKind(node, property);
		if (changeKind == RewriteEvent.INSERTED) {
			String indent= getLineDelimiter() + getIndentAtOffset(pos);
			doTextInsert(pos, indent, getEditGroup(node, property));
		} else if (changeKind == RewriteEvent.REMOVED) {
			try {
				getScanner().readNext(pos, false);
				doTextRemove(pos, getScanner().getCurrentStartOffset() - pos, getEditGroup(node, property));
				pos= getScanner().getCurrentStartOffset();
			} catch (CoreException e) {
				handleException(e);
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
	
	private int rewriteOptionalQualifier(ASTNode parent, StructuralPropertyDescriptor property, int startPos) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null) {
			switch (event.getChangeKind()) {
				case RewriteEvent.INSERTED: {
					ASTNode node= (ASTNode) event.getNewValue();
					TextEditGroup editGroup= getEditGroup(event);
					doTextInsert(startPos, node, getIndent(startPos), true, editGroup);
					doTextInsert(startPos, ".", editGroup); //$NON-NLS-1$
					return startPos;
				}
				case RewriteEvent.REMOVED: {
					try {
						ASTNode node= (ASTNode) event.getOriginalValue();
						TextEditGroup editGroup= getEditGroup(event);
						int dotEnd= getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameDOT, node.getStartPosition() + node.getLength());
						doTextRemoveAndVisit(startPos, dotEnd - startPos, node, editGroup);
						return dotEnd;
					} catch (CoreException e) {
						handleException(e);
					}
					break;
				}
				case RewriteEvent.REPLACED: {
					ASTNode node= (ASTNode) event.getOriginalValue();
					TextEditGroup editGroup= getEditGroup(event);
					SourceRange range= getExtendedRange(node);
					int offset= range.getStartPosition();
					int length= range.getLength();
					
					doTextRemoveAndVisit(offset, length, node, editGroup);
					doTextInsert(offset, (ASTNode) event.getNewValue(), getIndent(startPos), true, editGroup);
					try {
						return getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameDOT, offset + length);
					} catch (CoreException e) {
						handleException(e);
					}
					break;
				}
			}
		}
		Object node= getOriginalValue(parent, property);
		if (node == null) {
			return startPos;
		}
		int pos= doVisit((ASTNode) node);
		try {
			return getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameDOT, pos);
		} catch (CoreException e) {
			handleException(e);
		}
		return pos;
	}	
	
	class ParagraphListRewriter extends ListRewriter {
		
		public final static int DEFAULT_SPACING= 1;
		
		private int initialIndent;
		private int separatorLines;
		
		public ParagraphListRewriter(int initialIndent, int separator) {
			this.initialIndent= initialIndent;
			this.separatorLines= separator;
		}
		
		protected int getInitialIndent() {
			return this.initialIndent;
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
		
		private ASTNode getNode(int nodeIndex) {
			ASTNode elem= (ASTNode) this.list[nodeIndex].getOriginalValue();
			if (elem == null) {
				elem= (ASTNode) this.list[nodeIndex].getNewValue();
			}
			return elem;
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
			/* TODO JDT Priority
			if (currKind == ASTNode.FIELD_DECLARATION && nextKind == ASTNode.FIELD_DECLARATION ) {
				return 0;
			}
			*/
			if (secondLast != null) {
				return countEmptyLines(secondLast);
			}
			return DEFAULT_SPACING;
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
	}
		
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
	
	private int rewriteOptionalTypeParameters(ASTNode parent, StructuralPropertyDescriptor property, int offset, String keyword, boolean adjustOnNext, boolean needsSpaceOnRemoveAll) {
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
					int posBeforeOpenBracket= getScanner().getTokenStartOffset(ITerminalSymbols.TokenNameLESS, pos);
					if (posBeforeOpenBracket != pos) {
						needsSpaceOnRemoveAll= false;
					}
					pos= posBeforeOpenBracket;
				}
				pos= new ListRewriter().rewriteList(parent, property, pos, String.valueOf('<'), ", "); //$NON-NLS-1$
				if (isAllRemoved) { // all removed: remove right and space up to next element
					int endPos= getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameGREATER, pos); // set pos to '>'
					endPos= getScanner().getNextStartOffset(endPos, false);
					String replacement= needsSpaceOnRemoveAll ? String.valueOf(' ') : new String();
					doTextReplace(pos, endPos - pos, replacement, getEditGroup(children[children.length - 1]));
					return endPos;
				} else if (isAllInserted) {
					doTextInsert(pos, String.valueOf('>' + keyword), getEditGroup(children[children.length - 1]));
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
				return getScanner().getTokenEndOffset(ITerminalSymbols.TokenNameGREATER, pos);
			} catch (CoreException e) {
				handleException(e);
			}
		}
		return pos;
	}
	
	private boolean isAllOfKind(RewriteEvent[] children, int kind) {
		for (int i= 0; i < children.length; i++) {
			if (children[i].getChangeKind() != kind) {
				return false;
			}
		}
		return true;
	}	
	
	private int rewriteNodeList(ASTNode parent, StructuralPropertyDescriptor property, int pos, String keyword, String separator) {
		RewriteEvent event= getEvent(parent, property);
		if (event != null && event.getChangeKind() != RewriteEvent.UNCHANGED) {
			return new ListRewriter().rewriteList(parent, property, pos, keyword, separator);
		}
		return doVisit(parent, property, pos);
	}
	
	private void rewriteMethodBody(FunctionDeclaration parent, int startPos) { 
		RewriteEvent event= getEvent(parent, FunctionDeclaration.BODY_PROPERTY);
		if (event != null) {
			switch (event.getChangeKind()) {
				case RewriteEvent.INSERTED: {
					int endPos= parent.getStartPosition() + parent.getLength();
					TextEditGroup editGroup= getEditGroup(event);
					ASTNode body= (ASTNode) event.getNewValue();
					doTextRemove(startPos, endPos - startPos, editGroup);
					int indent= getIndent(parent.getStartPosition());
					String prefix= this.formatter.METHOD_BODY.getPrefix(indent);
					doTextInsert(startPos, prefix, editGroup); 
					doTextInsert(startPos, body, indent, true, editGroup);
					return;
				}
				case RewriteEvent.REMOVED: {
					TextEditGroup editGroup= getEditGroup(event);
					ASTNode body= (ASTNode) event.getOriginalValue();
					int endPos= parent.getStartPosition() + parent.getLength();
					doTextRemoveAndVisit(startPos, endPos - startPos, body, editGroup);
					doTextInsert(startPos, ";", editGroup); //$NON-NLS-1$
					return;
				}
				case RewriteEvent.REPLACED: {
					TextEditGroup editGroup= getEditGroup(event);
					ASTNode body= (ASTNode) event.getOriginalValue();
					doTextRemoveAndVisit(body.getStartPosition(), body.getLength(), body, editGroup);
					doTextInsert(body.getStartPosition(), (ASTNode) event.getNewValue(), getIndent(body.getStartPosition()), true, editGroup);
					return;
				}
			}
		}
		voidVisit(parent, FunctionDeclaration.BODY_PROPERTY);
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
	
	final int getIndent(int offset) {
		return this.formatter.computeIndentUnits(getIndentOfLine(offset));
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
	
	private String getCurrentLine(String str, int pos) {
		for (int i= pos - 1; i>= 0; i--) {
			char ch= str.charAt(i);
			if (IndentManipulation.isLineDelimiterChar(ch)) {
				return str.substring(i + 1, pos);
			}
		}
		return str.substring(0, pos);
	}
	
	private void replaceOperation(int posBeforeOperation, String newOperation, TextEditGroup editGroup) {
		try {
			getScanner().readNext(posBeforeOperation, true);
			doTextReplace(getScanner().getCurrentStartOffset(), getScanner().getCurrentLength(), newOperation, editGroup);
		} catch (CoreException e) {
			handleException(e);
		}
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
	
	final void doCopySourcePostVisit(ASTNode node, Stack nodeEndStack) {
		while (!nodeEndStack.isEmpty() && nodeEndStack.peek() == node) {
			nodeEndStack.pop();
			this.currentEdit= this.currentEdit.getParent();
		}
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(CompilationUnit)
	 */ 
	public boolean visit(CompilationUnit node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int startPos= rewriteNode(node, CompilationUnit.MODULE_DECLARATION_PROPERTY, 0, ASTRewriteFormatter.NONE);
			
		if (getChangeKind(node, CompilationUnit.MODULE_DECLARATION_PROPERTY) == RewriteEvent.INSERTED) {
			doTextInsert(0, getLineDelimiter(), getEditGroup(node, CompilationUnit.MODULE_DECLARATION_PROPERTY));
		}
				
		startPos= rewriteParagraphList(node, CompilationUnit.DECLARATIONS_PROPERTY, startPos, 0, 0, 2);
		return false;
	}

	private void rewriteReturnType(FunctionDeclaration node, boolean isConstructor, boolean isConstructorChange) {
		ChildPropertyDescriptor property= FunctionDeclaration.RETURN_TYPE_PROPERTY;

		// weakness in the AST: return type can exist, even if missing in source
		ASTNode originalReturnType= (ASTNode) getOriginalValue(node, property);
		boolean returnTypeExists=  originalReturnType != null && originalReturnType.getStartPosition() != -1;
		if (!isConstructorChange && returnTypeExists) {
			rewriteRequiredNode(node, property);
			return;
		}
		// difficult cases: return type insert or remove
		ASTNode newReturnType= (ASTNode) getNewValue(node, property);
		if (isConstructorChange || !returnTypeExists && newReturnType != originalReturnType) {
			// use the start offset of the method name to insert
			ASTNode originalMethodName= (ASTNode) getOriginalValue(node, FunctionDeclaration.NAME_PROPERTY);
			int nextStart= originalMethodName.getStartPosition(); // see bug 84049: can't use extended offset
			TextEditGroup editGroup= getEditGroup(node, property);
			if (isConstructor || !returnTypeExists) { // insert
				doTextInsert(nextStart, newReturnType, getIndent(nextStart), true, editGroup);
				doTextInsert(nextStart, " ", editGroup); //$NON-NLS-1$
			} else { // remove up to the method name
				int offset= getExtendedOffset(originalReturnType);
				doTextRemoveAndVisit(offset, nextStart - offset, originalReturnType, editGroup);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(Block)
	 */
	public boolean visit(Block node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int startPos;
		if (isCollapsed(node)) {
			startPos= node.getStartPosition();
		} else {
			startPos= getPosAfterLeftBrace(node.getStartPosition());
		}
		int startIndent= getIndent(node.getStartPosition()) + 1;
		rewriteParagraphList(node, Block.STATEMENTS_PROPERTY, startPos, startIndent, 0, 1);
		return false;
	}
	
	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(ReturnStatement)
	 */
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

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(Assignment)
	 */
	public boolean visit(Assignment node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= rewriteRequiredNode(node, Assignment.LEFT_HAND_SIDE_PROPERTY);
		rewriteOperation(node, Assignment.OPERATOR_PROPERTY, pos);
		rewriteRequiredNode(node, Assignment.RIGHT_HAND_SIDE_PROPERTY);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(BooleanLiteral)
	 */
	public boolean visit(BooleanLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		Boolean newLiteral= (Boolean) getNewValue(node, BooleanLiteral.BOOLEAN_VALUE_PROPERTY);
		TextEditGroup group = getEditGroup(node, BooleanLiteral.BOOLEAN_VALUE_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newLiteral.toString(), group);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(BreakStatement)
	 */
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

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(CharacterLiteral)
	 */
	public boolean visit(CharacterLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		String escapedSeq= (String) getNewValue(node, CharacterLiteral.ESCAPED_VALUE_PROPERTY);
		TextEditGroup group = getEditGroup(node, CharacterLiteral.ESCAPED_VALUE_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), escapedSeq, group);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(ConditionalExpression)
	 */
	public boolean visit(ConditionalExpression node) { // expression ? thenExpression : elseExpression
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, ConditionalExpression.EXPRESSION_PROPERTY);
		rewriteRequiredNode(node, ConditionalExpression.THEN_EXPRESSION_PROPERTY);
		rewriteRequiredNode(node, ConditionalExpression.ELSE_EXPRESSION_PROPERTY);	
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(ContinueStatement)
	 */
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

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(DoStatement)
	 */
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

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(ExpressionStatement)
	 */
	public boolean visit(ExpressionStatement node) { // expression
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, ExpressionStatement.EXPRESSION_PROPERTY);	
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(InfixExpression)
	 */
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

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(LabeledStatement)
	 */
	public boolean visit(LabeledStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, LabeledStatement.LABEL_PROPERTY);
		rewriteRequiredNode(node, LabeledStatement.BODY_PROPERTY);		
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(NullLiteral)
	 */
	public boolean visit(NullLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		changeNotSupported(node); // no modification possible
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(NumberLiteral)
	 */
	public boolean visit(NumberLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		String newLiteral= (String) getNewValue(node, NumberLiteral.TOKEN_PROPERTY);
		TextEditGroup group = getEditGroup(node, NumberLiteral.TOKEN_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newLiteral, group);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(ParenthesizedExpression)
	 */
	public boolean visit(ParenthesizedExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, ParenthesizedExpression.EXPRESSION_PROPERTY);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(PostfixExpression)
	 */
	public boolean visit(PostfixExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		int pos= rewriteRequiredNode(node, PostfixExpression.OPERAND_PROPERTY);
		rewriteOperation(node, PostfixExpression.OPERATOR_PROPERTY, pos);
		return false;		
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(PrefixExpression)
	 */
	public boolean visit(PrefixExpression node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteOperation(node, PrefixExpression.OPERATOR_PROPERTY, node.getStartPosition());
		rewriteRequiredNode(node, PrefixExpression.OPERAND_PROPERTY);
		return false;	
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(PrimitiveType)
	 */
	public boolean visit(PrimitiveType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		PrimitiveType.Code newCode= (PrimitiveType.Code) getNewValue(node, PrimitiveType.PRIMITIVE_TYPE_CODE_PROPERTY);
		TextEditGroup group = getEditGroup(node, PrimitiveType.PRIMITIVE_TYPE_CODE_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newCode.toString(), group);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(QualifiedName)
	 */
	public boolean visit(QualifiedName node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, QualifiedName.QUALIFIER_PROPERTY);
		rewriteRequiredNode(node, QualifiedName.NAME_PROPERTY);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(SimpleName)
	 */
	public boolean visit(SimpleName node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		String newString= (String) getNewValue(node, SimpleName.IDENTIFIER_PROPERTY);
		TextEditGroup group = getEditGroup(node, SimpleName.IDENTIFIER_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newString, group);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(SimpleType)
	 */
	public boolean visit(SimpleType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, SimpleType.NAME_PROPERTY);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(StringLiteral)
	 */
	public boolean visit(StringLiteral node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		String escapedSeq= (String) getNewValue(node, StringLiteral.ESCAPED_VALUE_PROPERTY);
		TextEditGroup group = getEditGroup(node, StringLiteral.ESCAPED_VALUE_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), escapedSeq, group);

		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(SwitchCase)
	 */
	public boolean visit(SwitchCase node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		// dont allow switching from case to default or back. New statements should be created.
		rewriteRequiredNode(node, SwitchCase.EXPRESSION_PROPERTY);
		return false;
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

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(SynchronizedStatement)
	 */
	public boolean visit(SynchronizedStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, SynchronizedStatement.EXPRESSION_PROPERTY);
		rewriteRequiredNode(node, SynchronizedStatement.BODY_PROPERTY);
		return false;
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(ThisExpression)
	 */
	public boolean visit(ThisLiteral node) {
		return false;		
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(ThrowStatement)
	 */
	public boolean visit(ThrowStatement node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		
		rewriteRequiredNode(node, ThrowStatement.EXPRESSION_PROPERTY);		
		return false;	
	}

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(TryStatement)
	 */
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

	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(WhileStatement)
	 */
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
	
	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(descent.core.dom.Modifier)
	 */
	public boolean visit(Modifier node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		String newText= getNewValue(node, Modifier.MODIFIER_KEYWORD_PROPERTY).toString(); // type Modifier.ModifierKeyword
		TextEditGroup group = getEditGroup(node, Modifier.MODIFIER_KEYWORD_PROPERTY);
		doTextReplace(node.getStartPosition(), node.getLength(), newText, group);
		return false;
	}
	
	/* (non-Javadoc)
	 * @see descent.core.dom.ASTVisitor#visit(descent.core.dom.QualifiedType)
	 */
	public boolean visit(QualifiedType node) {
		if (!hasChildrenChanges(node)) {
			return doVisitUnchangedChildren(node);
		}
		rewriteRequiredNode(node, QualifiedType.QUALIFIER_PROPERTY);
		rewriteRequiredNode(node, QualifiedType.TYPE_PROPERTY);
		return false;
	}
	
	final void handleException(Throwable e) {
		IllegalArgumentException runtimeException= new IllegalArgumentException("Document does not match the AST"); //$NON-NLS-1$
		runtimeException.initCause(e);
		throw runtimeException;
	}
}
