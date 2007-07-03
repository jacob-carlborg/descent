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


package descent.internal.ui.preferences.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import descent.core.formatter.CodeFormatter;
import descent.core.formatter.DefaultCodeFormatterConstants;

import descent.internal.ui.preferences.formatter.SnippetPreview.PreviewSnippet;


/**
 * Manage code formatter white space options on a higher level. 
 */
public final class WhiteSpaceOptions
{
	
	/**
	 * Creates the tree for the two-pane view where code elements are associated
	 * with syntax elements.
	 */
	// TODO formatter ui - grouping stuff : we don't want one huge list
	// Maybe another perl variable defining which group it's in...? Doing this
	// manually will definitley take too long & be error-prone...
	public List<Node> createTreeByDElement(Map<String, String> workingValues)
	{	
		final InnerNode statements = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_statements);
		createOption(statements, workingValues, FormatterMessages.WhiteSpaceOptions_before_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, SEMICOLON_PREVIEW);
		
		final InnerNode foreach_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_foreach_statement);
		createOption(foreach_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		createOption(foreach_statement, workingValues, FormatterMessages.WhiteSpaceOptions_after_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		
		final InnerNode for_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_for_statement);
		createOption(for_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT, FOR_PREVIEW);
		createOption(for_statement, workingValues, FormatterMessages.WhiteSpaceOptions_after_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT, FOR_PREVIEW);
		
		final List<Node> roots = new ArrayList<Node>();
		roots.add(for_statement);
		roots.add(foreach_statement);
		roots.add(statements);
		return roots;
	}
	
	/**
	 * Creates the tree for the one-pane view where a syntax element (colon,
	 * comma, etc.) is associated with code elements.
	 */
	public List<Node> createTreeBySyntaxElement(Map<String, String> workingValues)
	{
		final List<Node> roots = new ArrayList<Node>();
		InnerNode parent;
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_after_semicolon);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_for_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT, FOR_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_foreach_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_before_semicolon);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_statements, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, SEMICOLON_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_for_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT, FOR_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_foreach_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		
		return roots;
	}
	
	private InnerNode createParentNode(List<Node> roots,
			Map<String, String> workingValues, String text)
	{
		final InnerNode parent = new InnerNode(null, workingValues, text);
		roots.add(parent);
		return parent;
	}
	
	/**
	 * Represents a node in the options tree.
	 */
	public abstract static class Node
	{
		public int index;
		protected final List<Node> fChildren;
		protected final Map<String, String> fWorkingValues;
		private final String fName;
		private final InnerNode fParent;
		
		public Node(InnerNode parent, Map<String, String> workingValues,
				String message)
		{
			if(workingValues == null || message == null)
				throw new IllegalArgumentException();
			fParent = parent;
			fWorkingValues = workingValues;
			fName = message;
			fChildren = new ArrayList<Node>();
			if(fParent != null)
				fParent.add(this);
		}
		
		public abstract void getCheckedLeafs(List<Node> list);
		
		public List<Node> getChildren()
		{
			return Collections.unmodifiableList(fChildren);
		}
		
		public InnerNode getParent()
		{
			return fParent;
		}
		
		public abstract List<PreviewSnippet> getSnippets();
		
		public boolean hasChildren()
		{
			return !fChildren.isEmpty();
		}
		
		public abstract void setChecked(boolean checked);
		
		public final String toString()
		{
			return fName;
		}
	}
	
	/**
	 * A node representing a group of options in the tree.
	 */
	public static class InnerNode extends Node
	{
		public InnerNode(InnerNode parent, Map<String, String> workingValues,
				String messageKey)
		{
			super(parent, workingValues, messageKey);
		}
		
		public void add(Node child)
		{
			fChildren.add(child);
		}
		
		public void getCheckedLeafs(List<Node> list)
		{
			for(Iterator<Node> iter = fChildren.iterator(); iter.hasNext();)
			{
				iter.next().getCheckedLeafs(list);
			}
		}
		
		public List<PreviewSnippet> getSnippets()
		{
			final List<PreviewSnippet> snippets = new ArrayList<PreviewSnippet>(
					fChildren.size());
			for(Iterator<Node> iter = fChildren.iterator(); iter.hasNext();)
			{
				final List<PreviewSnippet> childSnippets = iter.next()
						.getSnippets();
				for(final Iterator<PreviewSnippet> chIter = childSnippets
						.iterator(); chIter.hasNext();)
				{
					final PreviewSnippet snippet = chIter.next();
					if(!snippets.contains(snippet))
						snippets.add(snippet);
				}
			}
			return snippets;
		}
		
		public void setChecked(boolean checked)
		{
			for(Iterator<Node> iter = fChildren.iterator(); iter.hasNext();)
				iter.next().setChecked(checked);
		}
	}
	
	/**
	 * A node representing a concrete white space option in the tree.
	 */
	public static class OptionNode extends Node
	{
		private final String fKey;
		private final List<PreviewSnippet> fSnippets;
		
		public OptionNode(InnerNode parent, Map<String, String> workingValues,
				String messageKey, String key, PreviewSnippet snippet)
		{
			super(parent, workingValues, messageKey);
			fKey = key;
			fSnippets = new ArrayList<PreviewSnippet>(1);
			fSnippets.add(snippet);
		}
		
		public boolean getChecked()
		{
			return DefaultCodeFormatterConstants.TRUE.equals(fWorkingValues
					.get(fKey));
		}
		
		public void getCheckedLeafs(List<Node> list)
		{
			if(getChecked())
				list.add(this);
		}
		
		public List<PreviewSnippet> getSnippets()
		{
			return fSnippets;
		}
		
		public void setChecked(boolean checked)
		{
			fWorkingValues.put(fKey,
					checked ? DefaultCodeFormatterConstants.TRUE
							: DefaultCodeFormatterConstants.FALSE);
		}
	}
	
	public static void makeIndexForNodes(List<Node> tree, List<Node> flatList)
	{
		for(final Iterator<Node> iter = tree.iterator(); iter.hasNext();)
		{
			final Node node = (Node) iter.next();
			node.index = flatList.size();
			flatList.add(node);
			makeIndexForNodes(node.getChildren(), flatList);
		}
	}
	
	private static OptionNode createOption(InnerNode root,
			Map<String, String> workingValues, String message, String key,
			PreviewSnippet snippet)
	{
		return new OptionNode(root, workingValues, message, key, snippet);
	}
	
	/**
	 * Preview snippets.
	 */
	private static final PreviewSnippet SEMICOLON_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_STATEMENTS,
			"int a= 4; foo(); bar(x, y);"
		);
	
	private static final PreviewSnippet FOR_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_STATEMENTS, 
		    "for (int i = 0, j = array.length; i < array.length; i++, j--){}\n\n" +
		    "foreach(int i,string s;names){}"
		);
}
