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
 *         if(!$$_{'wsDElemEx'})
 *         {
 *             $$_{'wsDElemEx'} = $$_{'wsDElem'}
 *         }
 *         if(!$$_{'wsSynElemEx'})
 *         {
 *             $$_{'wsSynElemEx'} = $$_{'wsSynElem'}
 *         }
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
		 *             "FormatterMessages.WhiteSpaceOptions_" . $$_{'wsSynElemEx'} . ", " .
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
		final InnerNode expressions = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_expressions);
		
		// TODO remove when there's actual options in aggregate_declaration
		final InnerNode aggregate_declaration = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_aggregate_declaration);
		
		// Declarations
		roots.add(declarations);
		function_declaration.setParent(declarations);
		function_template_params.setParent(function_declaration);
		function_decl_params.setParent(function_declaration);
		out_declaration.setParent(function_declaration);
		variable_declaration.setParent(declarations);
		version_debug.setParent(declarations);
		pragma.setParent(declarations);
		mixin.setParent(declarations);
		align_declaration.setParent(declarations);
		aggregate_declaration.setParent(declarations);
		aggregate_template_params.setParent(aggregate_declaration);
		template_declaration.setParent(declarations);
		extern_declarations.setParent(declarations);
		
		// Statements
		roots.add(statements);
		for_statement.setParent(statements);
		foreach_statement.setParent(statements);
		function_invocation.setParent(statements);
		function_invocation_args.setParent(function_invocation);
		new_params.setParent(function_invocation);
		while_statement.setParent(statements);
		switch_statement.setParent(statements);
		synchronized_statement.setParent(statements);
		scope_statement.setParent(statements);
		catch_statement.setParent(statements);
		assert_statement.setParent(statements);
		with_statement.setParent(statements);
		if_statements.setParent(statements);
		
		// Expressions
		roots.add(expressions);
		function_delegate_type.setParent(expressions);
		c_style_function_pointer.setParent(function_delegate_type);
		typeof.setParent(expressions);
		typeid.setParent(expressions);
		is_expressions.setParent(expressions);
		file_import_declarations.setParent(expressions);
		casts.setParent(expressions);
		
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
		 *             "FormatterMessages.WhiteSpaceOptions_" . $$_{'wsDElemEx'} . ", " .
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
		    "int bar(int x, inout long[] y ...)in{}out(result){}body{return x + y;}" +
		    "void bar()() {}" +
		    "void quux(T, U : T*)(int a, int b){}"
		);
	
	private final PreviewSnippet FUNCTION_CALL_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_STATEMENTS, 
			"foo();\n" +
			"bar(x, y);" +
			"baz!()();" +
			"quux!(int, long)(z, t);"
		);
	
	private final PreviewSnippet MULT_LOCAL_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"int a= 0, b= 1, c= 2, d= 3;"
		);
	
	private final PreviewSnippet TRY_CATCH_FINALLY_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"try{file.open();}catch(Exception e){Stdout(e);}" +
			"finally{file.close();}"
		);
	
	private final PreviewSnippet WHILE_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"while(true){foo();}"
		);
	
	private final PreviewSnippet SYNCHRONIZED_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"synchronized(foo){bar(foo);}"
		);
	
	private final PreviewSnippet SWITCH_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"switch(x){case 1:foo();break;case 2:bar();break;" +
			"default:baz();break;}"
		);
	
	private final PreviewSnippet ASSERT_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"assert(file.canWrite(), " +
			"\"File \" ~ file.name ~ \" is read-only\");"
		);
	
	private final PreviewSnippet SCOPE_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"Socket s = connect(\"127.0.0.1\");" +
			"scope(exit){s.close();}"
		);
	
	private final PreviewSnippet WITH_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"with(some.hard.to.type.name){func();}"
		);
	
	private final PreviewSnippet TYPEOF_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"typeof(s) t;"
		);
	
	private final PreviewSnippet TYPEID_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"TypeInfo ti = typeid(k);"
		);
	
	private final PreviewSnippet DELEGATE_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"void function(int, string) fp;" +
			"string delegate() dg;" +
			"int(*c_style)(int);"
		);
	
	private final PreviewSnippet ALIGN_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_COMPILATION_UNIT, 
			"struct S{align(3){int* very_misaligned_pointer;}" +
			"align(15):int* this_one_is_worse;}"
		);
	
	private final PreviewSnippet AGGREGATE_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_COMPILATION_UNIT, 
			"class A(){} interface B{} class C(T:int, K...):A,B{}"
		);
	
	private final PreviewSnippet TEMPLATE_DECLARATION_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_COMPILATION_UNIT, 
			"template Foo(){} template Bar(T:int, K...){}"
		);
	
	private final PreviewSnippet VERSION_DEBUG_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_COMPILATION_UNIT, 
			"version(_32Bit){alias int size_t;}" +
			"else version(_64Bit){alias long size_t;}" +
			"debug{}"
		);
	
	private final PreviewSnippet MIXIN_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_COMPILATION_UNIT, 
			"mixin(\"int x = 5;\")"
		);
	
	private final PreviewSnippet PRAGMA_PREVIEW =
		new PreviewSnippet(
			CodeFormatter.K_COMPILATION_UNIT, 
			"pragma(msg, \"Compiling...\");"
		);
	
	private final PreviewSnippet CONSTRUCTOR_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"auto x = new(10) WalterBright!(int)(25);"
		);
	
	private final PreviewSnippet EXTERN_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_COMPILATION_UNIT, 
			"extern(C) int c_int;" +
			"extern(Windows): int win_int;" +
			"extern(Pascal){int pasc_int;}"
		);
	
	private final PreviewSnippet FILE_IMPORT_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"ubyte[] image = cast(ubyte) import(\"funny_looking_cat.jpg\");"
		);
	
	private final PreviewSnippet IF_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"if(true){writef(\"true\");}" +
			"else if(false){writef(\"false\");}" +
			"else{writef(\"logic bomb\");}"
		);
	
	private final PreviewSnippet IS_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"const bool a = is(T);" +
			"const bool b = is(T : U);" +
			"const bool c = is(T == U);" +
			"const bool d = is(T U);" +
			"const bool e = is(T U == return);" +
			"const bool f = is(T U : U*);"
		);
	
	private final PreviewSnippet CAST_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"int a = cast(int) b;"
		);
	
	private final PreviewSnippet OPCALL_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			"Stdout(\"x = \")(x)(\" right now.\").newline;"
		);
	
	private final PreviewSnippet NO_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			""
		);
}
