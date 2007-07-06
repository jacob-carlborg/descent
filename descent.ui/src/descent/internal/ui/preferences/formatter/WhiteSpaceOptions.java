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
	public List<Node> createTreeByDElement(Map<String, String> workingValues)
	{	
		final InnerNode statements = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_statements);
		createOption(statements, workingValues, FormatterMessages.WhiteSpaceOptions_before_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, SEMICOLON_PREVIEW);
		
		final InnerNode function_invocation = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_function_invocation);
		createOption(function_invocation, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION, FUNCTION_CALL_PREVIEW);
		createOption(function_invocation, workingValues, FormatterMessages.WhiteSpaceOptions_after_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION, FUNCTION_CALL_PREVIEW);
		createOption(function_invocation, workingValues, FormatterMessages.WhiteSpaceOptions_before_closing_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_INVOCATION, FUNCTION_CALL_PREVIEW);
		createOption(function_invocation, workingValues, FormatterMessages.WhiteSpaceOptions_between_empty_parens, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_INVOCATION, FUNCTION_CALL_PREVIEW);
		
		final InnerNode version_debug = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_version_debug);
		createOption(version_debug, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_VERSION_DEBUG, VERSION_DEBUG_PREVIEW);
		
		final InnerNode variable_declaration = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_variable_declaration);
		createOption(variable_declaration, workingValues, FormatterMessages.WhiteSpaceOptions_before_comma, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, MULT_LOCAL_PREVIEW);
		createOption(variable_declaration, workingValues, FormatterMessages.WhiteSpaceOptions_after_comma, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, MULT_LOCAL_PREVIEW);
		
		final InnerNode foreach_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_foreach_statement);
		createOption(foreach_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOREACH_LOOPS, FOR_PREVIEW);
		createOption(foreach_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		createOption(foreach_statement, workingValues, FormatterMessages.WhiteSpaceOptions_after_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		createOption(foreach_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_comma, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		createOption(foreach_statement, workingValues, FormatterMessages.WhiteSpaceOptions_after_comma, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		
		final InnerNode function_arguments = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_function_arguments);
		createOption(function_arguments, workingValues, FormatterMessages.WhiteSpaceOptions_before_comma, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS, FUNCTION_CALL_PREVIEW);
		createOption(function_arguments, workingValues, FormatterMessages.WhiteSpaceOptions_after_comma, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS, FUNCTION_CALL_PREVIEW);
		
		final InnerNode function_delegate_type = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_function_delegate_type);
		createOption(function_delegate_type, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_DELEGATE, DELEGATE_PREVIEW);
		
		final InnerNode anonymous_function = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_anonymous_function);
		createOption(anonymous_function, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANON_FUNCTION, NO_PREVIEW /* TODO */);
		
		final InnerNode while_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_while_statement);
		createOption(while_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE_LOOPS, WHILE_PREVIEW);
		
		final InnerNode typeof = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_typeof);
		createOption(typeof, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEOF_STATEMENTS, TYPEOF_PREVIEW);
		
		final InnerNode switch_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_switch_statement);
		createOption(switch_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH_STATEMENTS, SWITCH_PREVIEW);
		
		final InnerNode anonymous_class = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_anonymous_class);
		createOption(anonymous_class, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANON_CLASS, NO_PREVIEW /* TODO */);
		
		final InnerNode pragma = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_pragma);
		createOption(pragma, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PRAGMAS, PRAGMA_PREVIEW);
		
		final InnerNode mixin = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_mixin);
		createOption(mixin, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MIXINS, MIXIN_PREVIEW);
		
		final InnerNode synchronized_volatile_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_synchronized_volatile_statement);
		createOption(synchronized_volatile_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren_in_synchronized_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SYNCHRONIZED_STATEMENT, SYNCHRONIZED_PREVIEW);
		
		final InnerNode typeid = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_typeid);
		createOption(typeid, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEID_STATEMENTS, TYPEID_PREVIEW);
		
		final InnerNode align_declaration = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_align_declaration);
		createOption(align_declaration, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ALIGN_DECLARATIONS, ALIGN_PREVIEW);
		
		final InnerNode scope_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_scope_statement);
		createOption(scope_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SCOPE_STATEMENTS, SCOPE_PREVIEW);
		
		final InnerNode catch_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_catch_statement);
		createOption(catch_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH, TRY_CATCH_FINALLY_PREVIEW);
		
		final InnerNode for_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_for_statement);
		createOption(for_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR_LOOPS, FOR_PREVIEW);
		createOption(for_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT, FOR_PREVIEW);
		createOption(for_statement, workingValues, FormatterMessages.WhiteSpaceOptions_after_semicolon, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT, FOR_PREVIEW);
		
		final InnerNode function_declaration = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_function_declaration);
		createOption(function_declaration, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		createOption(function_declaration, workingValues, FormatterMessages.WhiteSpaceOptions_between_template_and_arg_parens, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_TEMPLATE_AND_ARG_PARENS_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		createOption(function_declaration, workingValues, FormatterMessages.WhiteSpaceOptions_between_empty_parens, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		createOption(function_declaration, workingValues, FormatterMessages.WhiteSpaceOptions_after_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		createOption(function_declaration, workingValues, FormatterMessages.WhiteSpaceOptions_before_closing_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		
		final InnerNode aggregate_declaration = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_aggregate_declaration);
		createOption(aggregate_declaration, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren_in_template_argument_list, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_TEMPLATE_PARAMS, AGGREGATE_PREVIEW);
		
		final InnerNode assert_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_assert_statement);
		createOption(assert_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS, ASSERT_PREVIEW);
		
		final InnerNode with_statement = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_with_statement);
		createOption(with_statement, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WITH_STATEMENTS, WITH_PREVIEW);
		
		// Manually seems to be the best way to do this -- just ensure that this
		// list is updated every time a new white space option is added.
		final List<Node> roots = new ArrayList<Node>();
		final InnerNode declarations = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_declarations);
		final InnerNode expressions = new InnerNode(null, workingValues, FormatterMessages.WhiteSpaceOptions_expressions);
		
		// Declarations
		roots.add(declarations);
		function_declaration.setParent(declarations);
		variable_declaration.setParent(declarations);
		version_debug.setParent(declarations);
		pragma.setParent(declarations);
		mixin.setParent(declarations);
		align_declaration.setParent(declarations);
		aggregate_declaration.setParent(declarations);
		
		// Statements
		roots.add(statements);
		for_statement.setParent(statements);
		foreach_statement.setParent(statements);
		function_invocation.setParent(statements);
		function_arguments.setParent(function_invocation);
		while_statement.setParent(statements);
		switch_statement.setParent(statements);
		synchronized_volatile_statement.setParent(statements);
		scope_statement.setParent(statements);
		catch_statement.setParent(statements);
		assert_statement.setParent(statements);
		with_statement.setParent(statements);
		
		// Expressions
		roots.add(expressions);
		function_delegate_type.setParent(expressions);
		anonymous_function.setParent(expressions);
		typeof.setParent(expressions);
		anonymous_class.setParent(expressions);
		typeid.setParent(expressions);
		
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
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_after_comma);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_variable_declaration, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, MULT_LOCAL_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_arguments, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS, FUNCTION_CALL_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_foreach_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_after_opening_paren);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_declaration, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_invocation, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION, FUNCTION_CALL_PREVIEW);
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_after_semicolon);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_for_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT, FOR_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_foreach_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_before_closing_paren);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_declaration, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_invocation, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_INVOCATION, FUNCTION_CALL_PREVIEW);
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_before_comma);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_variable_declaration, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, MULT_LOCAL_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_invocation_arguments, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS, FUNCTION_CALL_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_foreach_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_before_opening_paren);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_declaration, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_invocation, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION, FUNCTION_CALL_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_catch_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH, TRY_CATCH_FINALLY_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_for_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR_LOOPS, FOR_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_while_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE_LOOPS, WHILE_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_foreach_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOREACH_LOOPS, FOR_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_synchronized_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SYNCHRONIZED_STATEMENT, SYNCHRONIZED_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_switch_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH_STATEMENTS, SWITCH_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_align_declaration, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ALIGN_DECLARATIONS, ALIGN_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_aggregate_declaration, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_TEMPLATE_PARAMS, AGGREGATE_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_assert_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS, ASSERT_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_version_debug, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_VERSION_DEBUG, VERSION_DEBUG_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_mixin, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MIXINS, MIXIN_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_pragma, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PRAGMAS, PRAGMA_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_scope_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SCOPE_STATEMENTS, SCOPE_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_with_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WITH_STATEMENTS, WITH_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_typeof, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEOF_STATEMENTS, TYPEOF_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_typeid, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEID_STATEMENTS, TYPEID_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_anonymous_class, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANON_CLASS, NO_PREVIEW /* TODO */);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_anonymous_function, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANON_FUNCTION, NO_PREVIEW /* TODO */);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_delegate_type, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_DELEGATE, DELEGATE_PREVIEW);
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_before_semicolon);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_statements, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, SEMICOLON_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_for_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT, FOR_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_foreach_statement, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT, FOR_PREVIEW);
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_between_adjacent_parens);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_between_template_and_arg_parens, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_TEMPLATE_AND_ARG_PARENS_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		
		parent = createParentNode(roots, workingValues, FormatterMessages.WhiteSpaceOptions_between_empty_parens);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_declaration, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION, FUNCTION_DECL_PREVIEW);
		createOption(parent, workingValues, FormatterMessages.WhiteSpaceOptions_function_invocation, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_INVOCATION, FUNCTION_CALL_PREVIEW);
		
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
			"void foo()() {}" +
		    "int bar(T, U)(T x, inout U[] y ...)in{}out(result){}body{return x + y;}"
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
			"string delegate() dg;"
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
	
	private final PreviewSnippet NO_PREVIEW =
		new PreviewSnippet(CodeFormatter.K_STATEMENTS, 
			""
		);
}
