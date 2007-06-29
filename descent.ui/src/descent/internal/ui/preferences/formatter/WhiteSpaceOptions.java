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
	 * Represents a node in the options tree.
	 */
	public abstract static class Node
	{
		private final InnerNode fParent;
		private final String fName;
		public int index;
		protected final Map<String, String> fWorkingValues;
		protected final List<Node> fChildren;
		
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
		
		public abstract void setChecked(boolean checked);
		
		public boolean hasChildren()
		{
			return !fChildren.isEmpty();
		}
		
		public List<Node> getChildren()
		{
			return Collections.unmodifiableList(fChildren);
		}
		
		public InnerNode getParent()
		{
			return fParent;
		}
		
		public final String toString()
		{
			return fName;
		}
		
		public abstract List<PreviewSnippet> getSnippets();
		
		public abstract void getCheckedLeafs(List<Node> list);
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
		
		public void setChecked(boolean checked)
		{
			for(Iterator<Node> iter = fChildren.iterator(); iter.hasNext();)
				iter.next().setChecked(checked);
		}
		
		public void add(Node child)
		{
			fChildren.add(child);
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
		
		public void getCheckedLeafs(List<Node> list)
		{
			for(Iterator<Node> iter = fChildren.iterator(); iter.hasNext();)
			{
				iter.next().getCheckedLeafs(list);
			}
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
		
		public void setChecked(boolean checked)
		{
			fWorkingValues.put(fKey,
					checked ? DefaultCodeFormatterConstants.TRUE
							: DefaultCodeFormatterConstants.FALSE);
		}
		
		public boolean getChecked()
		{
			return DefaultCodeFormatterConstants.TRUE.equals(fWorkingValues
					.get(fKey));
		}
		
		public List<PreviewSnippet> getSnippets()
		{
			return fSnippets;
		}
		
		public void getCheckedLeafs(List<Node> list)
		{
			if(getChecked())
				list.add(this);
		}
	}
	
	/**
	 * Preview snippets.
	 */
	private final PreviewSnippet SEMICOLON_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_STATEMENTS,
			"int a= 4; foo(); bar(x, y);" //$NON-NLS-1$
		);
	
	/**
	 * Create the tree, in this order: syntax element - position - abstract element
	 * @param workingValues
	 * @return returns roots (type <code>Node</code>)
	 */
	public List<Node> createTreeBySyntaxElem(Map<String, String> workingValues)
	{
		final List<Node> roots = new ArrayList<Node>();
		
		InnerNode element;
		
		element = new InnerNode(null, workingValues,
				FormatterMessages.WhiteSpaceOptions_semicolon);
		createBeforeSemicolonTree(workingValues, createChild(element,
				workingValues, FormatterMessages.WhiteSpaceOptions_before));
		
		return roots;
	}
	
	/**
	 * Create the tree, in this order: position - syntax element - abstract
	 * element
	 * @param workingValues
	 * @return returns roots (type <code>Node</code>)
	 */
	public List<Node> createAltTree(Map<String, String> workingValues)
	{
		
		final List<Node> roots = new ArrayList<Node>();
		
		InnerNode parent;
		
		parent = createParentNode(roots, workingValues,
				FormatterMessages.WhiteSpaceOptions_before_semicolon);
		createBeforeSemicolonTree(workingValues, parent);
		
		return roots;
	}
	
	private InnerNode createParentNode(List<Node> roots,
			Map<String, String> workingValues, String text)
	{
		final InnerNode parent = new InnerNode(null, workingValues, text);
		roots.add(parent);
		return parent;
	}
	
	public List<Node> createTreeByJavaElement(Map<String, String> workingValues)
	{
		
		final InnerNode statements = new InnerNode(null, workingValues,
				FormatterMessages.WhiteSpaceOptions_statements);
		createOption(
				statements,
				workingValues,
				FormatterMessages.WhiteSpaceOptions_before_semicolon,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON,
				SEMICOLON_PREVIEW);
		
		final List<Node> roots = new ArrayList<Node>();
		roots.add(statements);
		return roots;
	}
	
	private void createBeforeSemicolonTree(Map<String, String> workingValues,
			final InnerNode parent)
	{
		createOption(
				parent,
				workingValues,
				FormatterMessages.WhiteSpaceOptions_statements,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON,
				SEMICOLON_PREVIEW);
	}
	
	private static InnerNode createChild(InnerNode root,
			Map<String, String> workingValues, String message)
	{
		return new InnerNode(root, workingValues, message);
	}
	
	private static OptionNode createOption(InnerNode root,
			Map<String, String> workingValues, String message, String key,
			PreviewSnippet snippet)
	{
		return new OptionNode(root, workingValues, message, key, snippet);
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
}
