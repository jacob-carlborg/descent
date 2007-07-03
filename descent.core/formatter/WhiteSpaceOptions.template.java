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

/* EVAL-ONCE
 * 
 * # These are hashes that contain references to arrays of references to hashes.
 * # If that doesn't boggle your mind, I give up. Basically,
 * # %$@{$dElements{'ELEMENT_NAME'}}[n] is an option for that element. The extra
 * # curlies after the @ are nessescary so Perl doesn't treat it as a hash
 * # slice.
 * 
 * our %dElements = ();
 * our %syntaxElements = ();
 * 
 * foreach(@options)
 * {
 *     my $opt = $_;
 *     if($$_{'wsDElem'} && $$_{'wsSynElem'})
 *     {
 *         if($dElements{$$_{'wsDElem'}})
 *         {
 *             push(@{$dElements{$$_{'wsDElem'}}}, $opt);
 *         }
 *         else
 *         {
 *             my @dElemList;
 *             push(@dElemList, $_);
 *             $dElements{$$_{'wsDElem'}} = \@dElemList;
 *         }
 *         
 *         if($syntaxElements{$$_{'wsSynElem'}})
 *         {
 *             push(@{$syntaxElements{$$_{'wsSynElem'}}}, $opt);
 *         }
 *         else
 *         {
 *             my @synElemList;
 *             push(@synElemList, $_);
 *             $syntaxElements{$$_{'wsSynElem'}} = \@synElemList;
 *         }
 *     }
 * }
 *
 */

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
	public List<Node> createTreeByDElement(Map<String, String> workingValues)
	{	
		/* EVAL-ONCE
		 * 
		 * my @vars = ();
		 * our %dElements;
		 * 
		 * for my $element (keys %dElements)
		 * {
		 *     print DST "\t\tfinal InnerNode " . $element . " = new InnerNode(" .
		 *         "null, workingValues, FormatterMessages.WhiteSpaceOptions_" .
		 *         $element . ");\n";
		 *     foreach(@{$dElements{$element}})
		 *     {
		 *         print DST "\t\tcreateOption($element, workingValues, " .
		 *             "FormatterMessages.WhiteSpaceOptions_" . $$_{'wsSynElem'} . ", " .
		 *             "DefaultCodeFormatterConstants." . $$_{'constName'} . ", " .
		 *             $$_{'wsPreview'} . ");\n";
		 *     }
		 *     push(@vars, $element);
		 *     print DST "\t\t\n";
		 * }
		 *
		 */
		// Manually seems to be the best way to do this -- just ensure that this
		// list is updated every time a new white space option is added.
		final List<Node> roots = new ArrayList<Node>();
		final InnerNode declarations = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_declarations);
		
		// Declarations
		function_declaration.setParent(declarations);
		variable_declaration.setParent(declarations);
		roots.add(declarations);
		
		// Statements
		for_statement.setParent(statements);
		foreach_statement.setParent(statements);
		function_invocation.setParent(statements);
		function_arguments.setParent(function_invocation);
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
		
		/* EVAL-ONCE
		 * 
		 * our %syntaxElements;
		 * 
		 * for my $element (sort (keys %syntaxElements))
		 * {
		 *     print DST "\t\tparent = createParentNode(roots, workingValues, " .
		 *         "FormatterMessages.WhiteSpaceOptions_$element);\n";
		 *     foreach(@{$syntaxElements{$element}})
		 *     {
		 *         print DST "\t\tcreateOption(parent, workingValues, " .
		 *             "FormatterMessages.WhiteSpaceOptions_" . $$_{'wsDElem'} . ", " .
		 *             "DefaultCodeFormatterConstants." . $$_{'constName'} . ", " .
		 *             $$_{'wsPreview'} . ");\n";
		 *     }
		 *     print DST "\t\t\n";
		 * }
		 */
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
public abstract static class Node {
	    
	    private InnerNode fParent;
	    private final String fName;
	    
	    public int index;
	    
	    protected final Map<String, String> fWorkingValues;
	    protected final ArrayList<Node> fChildren;

	    public Node(InnerNode parent, Map<String, String> workingValues,
	    		String message) {
	        if (workingValues == null || message == null)
	            throw new IllegalArgumentException();
	        fParent= parent;
	        fWorkingValues= workingValues;
	        fName= message;
	        fChildren= new ArrayList<Node>();
	        if (fParent != null)
	            fParent.add(this);
	    }
	    
	    public abstract void setChecked(boolean checked);
		
		public final void setParent(InnerNode parent)
		{
			if(null != fParent)
				throw new IllegalStateException("Parent can only be set once!");
			fParent = parent;
			fParent.add(this);
		}
		
	    public boolean hasChildren() { 
	        return !fChildren.isEmpty();
	    }
	    
	    public List<Node> getChildren() {
	        return Collections.unmodifiableList(fChildren);
	    }
	    
	    public InnerNode getParent() {
	        return fParent;
	    }

	    public final String toString() {
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
	
	private final PreviewSnippet FUNCTION_DECL_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_COMPILATION_UNIT, 
			"void foo() {}" +
		    "int bar(int x, inout int y)in{}out(result){}body{return x + y;}"
		);
	
	private final PreviewSnippet FUNCTION_CALL_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_STATEMENTS, 
			"foo();\n" +
			"bar(x, y);"
		);
	
	private final PreviewSnippet MULT_LOCAL_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"int a= 0, b= 1, c= 2, d= 3;"
		);
}
